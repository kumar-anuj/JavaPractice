# Part 5 – Scalability and Caching

## 5.1 Scalability
Always specify: **what is scaled** (requests, data, connections, fan-out), **how**, **limiting resource** (CPU, memory, disk IOPS, network, locks, single leader), **failure modes**, **cost**, **ops burden**, **max practical scale**, **assumptions**.

| Concept | Definition | Limit / trade-off |
|---|---|---|
| Vertical scaling | Bigger machine | Simple, no code change; hardware ceiling, cost superlinear, SPOF |
| Horizontal scaling | More machines | Needs statelessness/partitioning; coordination, consistency |
| Diagonal | Scale up until cost-efficient, then out | Pragmatic mix |
| Stateless service | No per-user state in process (state in DB/cache/token) | Easy LB/autoscale; externalized state latency |
| Stateful service | Holds state (DB, sessions, WebSocket) | Needs sticky routing, replication, careful rolling deploys |
| Load balancing | Spread requests (see Part 2) | LB itself must be redundant |
| Auto-scaling / elasticity | Add/remove capacity by metrics | Lag (cold start), flapping, cost spikes, downstream not scaling |
| Capacity planning | Forecast + headroom | Wrong assumptions → outages or waste |
| Resource/connection/DB pooling | Reuse expensive resources | Pool exhaustion; size = f(DB max connections ÷ instances); PgBouncer/proxy |
| Replication | Copies for read scale/availability | Lag, consistency |
| Partitioning/sharding | Split data/load | Cross-shard ops |
| Caching | Part 5.2 | Staleness |
| Batching | Group operations | Throughput↑ latency↑ |
| Async processing | Defer work via queues | Eventual completion, ops of queues |
| Queue-based load leveling | Queue absorbs bursts for steady consumers | Backlog latency |
| Backpressure | Slow producers when consumers saturated | Needs protocol support |
| Rate limiting | Cap requests/client (token bucket, leaky bucket, fixed/sliding window) | Distributed counters, fairness |
| Throttling | Slow instead of reject | Client latency |
| Admission control / load shedding | Reject low-priority work to protect capacity | Needs priority classification |
| Read/write separation, CQRS | Different paths | Lag |
| Content delivery | CDN edge | Invalidation |
| Data locality | Compute near data / users | Placement complexity |
| Request coalescing (single-flight) | Merge identical in-flight requests | Prevents stampede |
| Hedged requests | Send duplicate after p95 delay, take first | Extra load (~5%), idempotent only |
| Speculative execution | Re-run straggler tasks | Wasted compute |
| Work stealing | Idle workers take tasks | Used in ForkJoin pools, schedulers |
| Fan-out / fan-in / scatter-gather | Parallel calls then aggregate | Tail latency = max of n; partial results |
| MapReduce | Map shards → shuffle → reduce (Google 2004) | Disk-heavy, batch; → Spark (in-memory), Flink |
| Hotspot mitigation | Salting, splitting, caching, replication of hot keys | Complexity |
| Noisy neighbor / multi-tenancy / isolation | Shared infra among tenants | Quotas, cgroups, cell-based/bulkhead architecture, per-tenant limits; isolation vs cost |

### Formulas
- Little's Law: `L = λ · W` (concurrent requests = arrival rate × latency).
- Throughput per instance ≈ `cores / avg CPU time per request`.
- Amdahl: `speedup = 1 / ((1-p) + p/n)`; Universal Scalability Law adds contention and coherency costs.
- Tail at scale: if one call is slow 1% of time, a fan-out of 100 calls is slow `1-0.99^100 ≈ 63%` of the time.
- Utilization vs queueing: wait time grows ~`ρ/(1-ρ)`; keep utilization ≲ 60–70% for latency-sensitive services.
- Pool size ≈ `QPS × avg DB time (s)` (+ headroom).

## 5.2 Caching
**Why:** locality + cost asymmetry between layers (Part 1). **Why hard:** a cache is a second copy of truth; invalidation requires knowing when truth changed, across failures and races.

| Layer | What | Notes |
|---|---|---|
| CPU cache | L1–L3 lines | Hardware coherence |
| OS page/filesystem cache | Hot blocks | DBs may bypass (O_DIRECT) |
| DB buffer pool | Pages in RAM | Size ≈ working set |
| Application/local cache | In-process (Caffeine, Guava) | Fast; per-instance inconsistency |
| Distributed cache | Redis/Memcached cluster | Network hop; shared; cluster failure risk |
| CDN | Edge | Cache-Control, surrogate keys |
| Browser | HTTP caching, ETag/Last-Modified | Hard to invalidate (use versioned URLs) |
| DNS | TTL | Stale failover |
| Multi-level | L1 local + L2 distributed + DB | Invalidation fan-out |

### Write/read strategies
| Strategy | Flow | Pros | Cons |
|---|---|---|---|
| Cache-aside | App checks cache, on miss reads DB & fills | Simple, resilient | Stale on races, miss penalty |
| Read-through | Cache loads on miss | Cleaner code | Provider coupling |
| Write-through | Write cache+DB synchronously | Consistent reads | Write latency |
| Write-back/behind | Write cache, flush later | Fast writes | Data loss on crash |
| Refresh-ahead | Reload before expiry | No miss spikes | Wasted refresh |
| Warming | Preload | Avoid cold start | Needs knowledge |

### Eviction & invalidation
TTL (+ jitter), LRU, LFU (TinyLFU), FIFO, random, size/cost-aware. Invalidate by: TTL, explicit delete on write, event/CDC-driven, versioned keys, tags. Race: *read old → write DB → delete cache → stale refill*; mitigate with delayed double-delete, versioning, short TTL, leases.

### Failure patterns
| Problem | Cause | Fix |
|---|---|---|
| Stampede/thundering herd | Hot key expires, many hit DB | Single-flight/mutex, stale-while-revalidate, jittered TTL, refresh-ahead |
| Penetration | Requests for nonexistent keys bypass cache | Negative caching, Bloom filter, validation |
| Avalanche | Many keys expire together / cache dies | TTL jitter, replicas, circuit breaker, DB protection |
| Hot key | One key overloaded node | Local cache, key replication/splitting |
| Distributed cache failure | Node loss reshuffles keys | Consistent hashing, replicas, graceful DB fallback |
| Inconsistency | Race/invalidation lag | Accept bounded staleness, or don't cache that data |

**Redis-like** (rich types: strings, hashes, lists, sets, sorted sets, streams, HyperLogLog, pub/sub, persistence, clustering, single-threaded commands) vs **Memcached-like** (simple strings, multi-threaded, no persistence). 

**Correctness effect:** caching converts strong consistency into bounded staleness; don't cache money-critical read-before-write decisions without versioning.

