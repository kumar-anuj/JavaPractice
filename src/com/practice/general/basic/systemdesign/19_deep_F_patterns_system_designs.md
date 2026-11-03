# Part 19 – Deep Dive F: Patterns in Code and Full System Designs

## 19.1 GoF patterns – compact Java examples
```java
// Singleton (thread-safe, enum)
enum Config { INSTANCE; String get(String k){ return System.getProperty(k);} }

// Factory method
interface Notifier { void send(String m); }
class NotifierFactory { static Notifier of(String t){ return switch(t){ case "sms"->new Sms(); default->new Email(); }; } }

// Builder
Order o = Order.builder().id(1).item("A1",2).coupon("X").build();

// Strategy (lambda)
interface Pricing { double price(double base); }
Pricing sale = b -> b * 0.9;

// Decorator (Java I/O): new BufferedReader(new InputStreamReader(new FileInputStream(f)))

// Observer
interface Listener { void onEvent(String e); }
class Bus { List<Listener> ls = new CopyOnWriteArrayList<>(); void publish(String e){ ls.forEach(l->l.onEvent(e)); } }

// Proxy (dynamic): Proxy.newProxyInstance(cl, new Class[]{Svc.class}, (p,m,a)->{ log(m); return m.invoke(real,a); });

// Chain of responsibility (servlet filters): auth -> rateLimit -> logging -> handler
// State: OrderState {NEW->PAID->SHIPPED}, each state object permits only valid transitions
// Template method: abstract class Job { final void run(){ init(); doWork(); cleanup(); } abstract void doWork(); }
```
Existing examples in this repo: `designpatterns/creational|structural|behavioural`.

## 19.2 Distributed patterns – diagrams
**Circuit breaker + bulkhead (Resilience4j style)**
```
call -> [Bulkhead: max 20 concurrent for payments] -> [CircuitBreaker] -> [TimeLimiter 800ms] -> [Retry 2x backoff] -> service
```
**Sidecar / Ambassador / Adapter**
```
Pod: [App] <-localhost-> [Sidecar: logging/TLS/retry]   (Sidecar)
     [App] -> [Ambassador proxy] -> remote DB/shards     (Ambassador hides sharding/connection logic)
     [Legacy XML svc] <- [Adapter svc] <- JSON clients   (Adapter service)
```
**Competing consumers + claim check**
```
Producer -> Queue --> Worker1 / Worker2 / Worker3 (scale by adding workers)
Large video: upload to blob store -> message carries only blob URL (claim check)
```
**Scatter-gather:** `Search API -> [shard1..shardN] -> merge top-K -> respond (with partial results on timeout)`.
**TCC (Try-Confirm-Cancel):** Try = place hold on $50; Confirm = capture hold; Cancel = release hold (holds expire automatically).
**Anti-corruption layer:** translate `LegacyCust{cust_nm, sts_cd}` ↔ `Customer{name,status}` so the legacy model never leaks into the new domain.
**Idempotency key**
```
POST /payments  Idempotency-Key: k-123
server: if key exists -> return stored response ; else process, store (key, response) in same txn
```

## 19.3 System design 1 – URL shortener (full)
**Requirements:** 100 M new URLs/month, 100:1 read:write, redirect p99 < 50 ms, 5-year retention.
**Estimates:** writes ≈ 40/s; reads ≈ 4 K/s avg, 20 K/s peak; storage ≈ 3 TB over 5 yrs.
```
Client -> CDN -> LB -> [Redirect svc] -> Redis (code->url) -> [KV store sharded by code]
                      [Create svc]  -> ID generator (ranges of counters per node) -> base62
                      click events -> Kafka -> analytics (OLAP)
```
Key decisions: base62 of unique ID (no collisions) vs hash+collision check; 301 (cacheable, loses analytics) vs 302; expire via TTL; abuse → rate limit + malware blocklist.
Failure cases: cache down → KV absorbs with rate limit; ID service down → each node pre-allocates a range.

## 19.4 System design 2 – Chat
```
Client <--WebSocket--> [Gateway pool] --> [Chat svc] --> [Message store: Cassandra, key=(chat_id, ts/seq)]
                           |                 |--> [Presence: Redis TTL heartbeat]
                           |                 |--> [Push/Notification svc] for offline users
                           +-- Redis map: user_id -> gateway_id
```
- Ordering: per-conversation sequence number assigned by the partition owner; clients dedupe by message id.
- Group fan-out: small groups fan-out on write; huge channels fan-out on read.
- Delivery states: sent → delivered → read via acks.
- Reconnect storm: jittered backoff; resume from last seq.

## 19.5 System design 3 – News feed (hybrid fan-out)
```
Post -> [Post svc] -> Kafka -> [Fan-out workers] -> write to followers' feed lists in Redis (normal users)
Celebrity posts are NOT fanned out; at read: merge precomputed feed + pull recent posts from followed celebrities
Read: [Feed svc] -> feed cache -> hydrate post/user data -> rank (ML) -> return page (cursor)
```
Trade-off: write amplification (1 post × 1 M followers) vs read latency; hybrid caps both.

## 19.6 System design 4 – Payment system
```
Client -> API (Idempotency-Key) -> [Payment svc] --state machine: CREATED->AUTHORIZED->CAPTURED->SETTLED/REFUNDED
                                   |-> PSP (Stripe-like) ; webhook confirms async result
                                   |-> Ledger DB (double-entry, append-only, serializable)
                                   |-> Outbox -> Kafka -> notifications, accounting
Reconciliation job: compare internal ledger with PSP/bank reports daily; fix mismatches.
```
Timeout-after-send problem: unknown if charge happened → query PSP by idempotency key before retrying. Never use floats for money (use integer minor units).

## 19.7 System design 5 – Ticket booking (no double booking)
```
Select seat -> HOLD (row seat_id, state=HELD, expires_at=now+5m, SELECT FOR UPDATE / conditional update)
Pay -> state=BOOKED ; timer job/TTL releases expired holds
Flash sale: virtual waiting room (queue) admits N users/sec to protect inventory DB.
```
```sql
UPDATE seats SET state='HELD', held_by=:u, expires_at=now()+interval '5 min'
WHERE id=:s AND (state='FREE' OR expires_at<now());   -- 1 row updated = success
```

## 19.8 System design 6 – RAG system
```
Ingest : docs -> parse -> chunk (300-800 tokens, overlap) -> embed -> vector index + BM25 index (+ metadata: ACL, source, date)
Query  : question -> (rewrite) -> hybrid retrieve top-50 -> filter by user ACL -> rerank top-5 -> prompt + citations -> LLM -> guardrails -> answer
Eval   : retrieval recall@k, groundedness, answer quality; log traces; user feedback loop
```
Risks: prompt injection from retrieved docs (treat as untrusted), ACL leakage (filter before generation), stale index (incremental re-index via CDC), cost/latency (cache, smaller models for rewrite, stream tokens).

## 19.9 System design 7 – Distributed rate limiter, KV store, job scheduler (sketches)
```
Rate limiter : Gateway -> Redis cluster (Lua token bucket per key) ; fail-open vs fail-closed policy; local pre-check
KV store     : client -> coordinator node -> consistent-hash ring -> N=3 replicas (W=2,R=2); WAL+memtable+SSTable; gossip; hinted handoff
Scheduler    : jobs table (run_at, status) -> workers claim via lease (UPDATE ... WHERE lease_expires<now() RETURNING) ; fencing token; idempotent jobs
```

## 19.10 Interview framework (how to present any design)
1. Clarify functional + non-functional requirements (scale, latency, consistency, availability).
2. Estimate (QPS, storage, bandwidth).
3. API + data model.
4. High-level diagram; walk the read and write paths.
5. Deep-dive bottlenecks (cache, sharding, queues, consistency).
6. Failure modes, security, observability, cost.
7. State trade-offs and alternatives explicitly.

