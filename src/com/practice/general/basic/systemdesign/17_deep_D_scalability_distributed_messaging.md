# Part 17 – Deep Dive D: Scalability, Caching, Distributed Systems, Messaging

## 17.1 Scaling journey of one app (stage by stage)
```
Stage 1: [Client] -> [App+DB on one box]                                   ~100 users
Stage 2: [Client] -> [App] -> [DB]  (separate boxes)                        ~1K
Stage 3: [Client] -> [LB] -> [App x N, stateless] -> [DB primary + replicas]  ~10K-100K
Stage 4: + [Redis cache] + [CDN] + [Queue + workers] for async jobs          ~1M
Stage 5: + sharded DB / NoSQL, search index, service split, multi-AZ         ~10M+
Stage 6: + multi-region, edge, cell architecture                              global
```
Rule: *measure, find the bottleneck, apply the cheapest fix*; every step adds failure modes.

**Making an app stateless – before/after**
```
Before: session in server memory -> LB needs sticky sessions; deploy loses sessions
After : session id in cookie -> Redis (or signed JWT) -> any instance can serve
```

## 17.2 Capacity example
Target 5,000 req/s peak; each request uses 20 ms CPU; 4 cores/instance.
`per-instance = 4 / 0.020 = 200 req/s` → need 25 instances at 100% → at 60% target utilization ≈ 42, plus N+1 (AZ loss with 3 AZs: ×1.5) ≈ 63.
**DB connections:** 63 instances × pool 20 = 1,260 connections > Postgres comfortable ~200–500 → use PgBouncer or smaller pools (pool size ≈ QPS × avg query seconds).

## 17.3 Rate limiting algorithms
```
Token bucket (capacity 10, refill 5/s): each request takes a token; bursts up to 10; avg 5/s.
Leaky bucket: queue drains at fixed rate (smooths output).
Fixed window: count per minute (boundary burst: 2x at the edge).
Sliding window log/counter: accurate, more memory.
```
```java
// simple token bucket
class TokenBucket {
  private final long cap, refillPerSec; private double tokens; private long last = System.nanoTime();
  TokenBucket(long cap,long r){this.cap=cap;this.refillPerSec=r;this.tokens=cap;}
  synchronized boolean tryAcquire(){
    long now=System.nanoTime(); tokens=Math.min(cap,tokens+(now-last)/1e9*refillPerSec); last=now;
    if(tokens>=1){tokens--;return true;} return false; }
}
```
Distributed: Redis Lua script / `INCR`+`EXPIRE`; per API key and per IP; return `429` + `Retry-After`.

## 17.4 Tail latency, hedging, fan-out
```
Aggregator --> 100 shards in parallel; response time = slowest shard
p99 per shard 100 ms  ->  P(at least one slow among 100) = 1 - 0.99^100 = 63%
Hedge: if no reply by p95, send same (idempotent) request to a replica; take first -> cuts tail at ~5% extra load.
```

## 17.5 Caching – patterns with code and races
```java
// cache-aside
Product get(long id){
  Product p = cache.get(id);
  if(p!=null) return p;
  p = db.find(id);                 // miss
  if(p==null) cache.put(id, NULL_MARKER, 60);   // negative caching (penetration)
  else cache.put(id, p, 300 + jitter());        // TTL + jitter (avalanche)
  return p;
}
void update(Product p){ db.save(p); cache.delete(p.id); }   // invalidate after write
```
**Race:** T1 reads old from DB → T2 updates DB and deletes cache → T1 writes old value into cache (stale until TTL). Mitigate: short TTL, versioned values (compare-and-set), delayed second delete, or don't cache critical data.

**Stampede / single-flight**
```
1000 requests miss simultaneously -> only first loads from DB; others wait for same Future (single-flight/lock) or get stale value (stale-while-revalidate)
```
**Write strategies**
```
write-through: App -> Cache -> DB (sync)        safe, slower writes
write-back   : App -> Cache ... async flush DB   fast, data loss if cache dies
```
**Eviction example (capacity 3, access A B C A D):** LRU evicts B (least recently used) when D arrives; LFU evicts the least frequently used; FIFO evicts A.

**Multi-level**
```
Request -> L1 Caffeine (in-process, µs) -> L2 Redis (ms) -> DB
Invalidation: publish "key X changed" on pub/sub so L1 instances drop X.
```

## 17.6 Distributed-systems failure handling – code and timelines
**Retry with exponential backoff + full jitter**
```java
for (int attempt=0; attempt<5; attempt++) {
  try { return call(); }
  catch (TransientException e) {
    long cap = Math.min(10_000, 100L << attempt);        // 100ms,200ms,400ms... capped
    Thread.sleep(ThreadLocalRandom.current().nextLong(cap));   // full jitter
  }
}
```
Without jitter all clients retry at the same instants → synchronized load spikes.

**Circuit breaker states**
```
CLOSED --failures>threshold--> OPEN --after cool-down--> HALF-OPEN --probe ok--> CLOSED
                                  ^                          | probe fails
                                  +--------------------------+
OPEN: fail fast / fallback (cached data) -> protects the dependency and your threads.
```
**Retry storm example:** 3 layers each retry 3× → one user request becomes 3×3×3 = 27 calls to the bottom service. Fix: retry only at one layer, retry budgets (≤10% extra), deadlines propagated downward.

**Safe distributed lock needs fencing**
```
Client A gets lock (token 33) -> long GC pause -> lease expires
Client B gets lock (token 34) -> writes to storage
Client A wakes, writes with token 33 -> storage rejects (33 < 34)
```
**Leader election with Raft (simplified)**
```
Follower --no heartbeat timeout--> Candidate (term+1) --majority votes--> Leader
Leader appends entry -> replicates to majority -> commit -> apply -> reply
Minority-partition leader cannot commit (no majority) -> steps down.
```
**Clocks:** Lamport: on send `t++`, on receive `t=max(local,msg)+1`. Vector clock `[A:2,B:1]` vs `[A:1,B:2]` = concurrent (conflict); `[A:2,B:1]` vs `[A:1,B:1]` = first is newer.

**Gossip:** each node every second tells 1–3 random peers its state → information reaches all N nodes in ~O(log N) rounds; failure detected via missing heartbeats (SWIM).

## 17.7 Messaging – examples
**Kafka partitioning & ordering**
```
Topic orders (3 partitions), key = orderId
 P0: o1:created, o1:paid, o4:created
 P1: o2:created, o2:cancelled
 Order preserved per key (same partition), not across keys.
Consumer group G1 (3 consumers) -> each owns one partition; 4th consumer idle. Lag = log-end offset − committed offset.
```
**Delivery semantics in code (at-least-once consumer)**
```java
for (ConsumerRecord r : consumer.poll(...)) {
   if (inbox.alreadyProcessed(r.key(), r.offset())) continue;   // idempotency
   process(r);                                                   // side effect + inbox insert in ONE txn
}
consumer.commitSync();   // commit after processing -> crash causes reprocessing, not loss
```
**RabbitMQ-style routing:** `producer -> exchange(topic) --"order.*.eu"--> queue A`, `"order.#" --> queue B`; ack/nack, prefetch limits for backpressure, DLX for dead letters.

**Retry + DLQ flow**
```
main queue --fail--> retry-5s --fail--> retry-1m --fail--> DLQ (alert, inspect, redrive)
```
**Windowing example (event time):** clicks per minute.
```
Tumbling: [00:00-01:00) [01:00-02:00)       Sliding(1m size, 30s slide): overlapping windows
Session: gap 30s of inactivity closes the window
Watermark = max event time − 5 min allowed lateness; events later than that go to side output.
```
**Lambda vs Kappa**
```
Lambda: source -> batch layer (accurate, hourly) + speed layer (approx, real-time) -> serving merges
Kappa : source -> replayable log -> one stream job (reprocess by replaying with new code)
```
**ETL vs ELT:** ETL: extract → transform in pipeline → load clean data. ELT: load raw to warehouse → transform with SQL (dbt) – cheap scalable compute, keeps raw data.

## 17.8 Durable execution example (Temporal-style)
```
workflow checkout(order):
   reserveStock(); chargeCard(); sendEmail()      // each step is a retried "activity"
State/history persisted; if worker crashes it replays history and resumes at the next step.
Rule: workflow code must be deterministic (no random/time directly).
```

