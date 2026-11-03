# Part 12 – Decision Frameworks, Failed/Oversold Ideas, Failure Cases

## 12.1 Decision trees (condensed)
```
Monolith or microservices?
 team < ~20 and domain unclear?            -> Modular monolith
 independent scaling/deploy/team autonomy
   needed AND platform/observability ready? -> Microservices (start from bounded contexts)
 else                                       -> Modular monolith, extract hot spots later

SQL or NoSQL?
 relations, ad-hoc queries, transactions?   -> SQL
 extreme write scale, known key access?     -> Wide-column/KV
 flexible nested docs?                      -> Document (or SQL JSONB)
 traversals?                                -> Graph ; text? -> Search ; embeddings? -> Vector
 default when unsure                        -> PostgreSQL

Strong vs eventual consistency?
 money/inventory/uniqueness invariants?     -> Strong (single-leader/consensus)
 stale reads tolerable, high availability?  -> Eventual (+ read-your-writes where needed)

Queue or stream?
 distribute tasks, delete after done        -> Queue
 many consumers, replay, ordering by key    -> Log/stream

Sync or async?
 caller needs result now & dependency fast  -> Sync (with timeout/circuit breaker)
 long work / spiky / fan-out                -> Async

Cache or database?  Derived, recomputable, read-heavy? -> Cache; source of truth? -> DB
Replication or sharding? Read-bound -> replicas; write/data-size-bound -> shard
REST/GraphQL/gRPC? public/simple -> REST; many varied clients -> GraphQL; internal/perf -> gRPC
Containers or VMs? density/portability -> containers; hard isolation/legacy OS -> VMs (or microVMs)
Kubernetes or serverless? steady/complex/portable -> K8s; spiky/small team/event -> serverless
Single or multi-region? global users or RTO/RPO beyond single-region -> multi (start active-passive)
Active-active or active-passive? need ~0 RTO and can resolve conflicts -> active-active
Batch or real-time? latency requirement in seconds & value decays fast -> streaming
Build or buy? differentiating core -> build; commodity -> buy
Managed or self-hosted? small team -> managed; scale/cost/compliance justify ops -> self-host
Transaction or saga? same DB -> transaction; across services -> saga/outbox
Centralized or distributed? Distribute only when scale/availability/org/latency forces it.
```

## 12.2 Failed, oversold, or niche ideas
| Idea | Attempted to solve | Why attractive | Why faded | Replaced by | Still valid? |
|---|---|---|---|---|---|
| CORBA/DCOM | Language-neutral remote objects | Location transparency | Complexity, interop, firewalls | REST/SOAP/gRPC | Rare |
| EJB 2 / heavy J2EE | Enterprise components | Standardization | Boilerplate | Spring, POJOs | Legacy |
| SOAP/WS-* + ESB | Enterprise integration | Standards, governance | Verbosity, central bottleneck | REST, brokers, microservices | B2B legacy |
| Object-oriented DBs | Impedance mismatch | Natural model | No ecosystem | ORM, document DBs | Niche |
| Early "NoSQL replaces SQL" | Scale | Horizontal scale | Lost joins/transactions | SQL returned (NewSQL, JSON in SQL) | Both coexist |
| MapReduce (Hadoop) | Batch at scale | Simple model | Disk-heavy, slow iteration | Spark/Flink | Legacy |
| 3PC | Non-blocking commit | Avoid 2PC block | Assumes synchronous network | Consensus-based commit | Academic |
| Distributed objects/transparency | Hide the network | Developer ease | Violates fallacies | Explicit async APIs | No |
| Blockchain-for-everything | Trustless data | Hype | Throughput, cost; DB suffices without adversaries | Databases | Niche (multi-party trust) |
| Microservices-first for small teams | Scale/agility | Fashion | Ops overhead | Modular monolith | Misapplied |
| Nano-services | Max granularity | Reuse | Latency, cost | Merge services | No |
| Shared database between services | Easy integration | Simplicity | Coupling | DB per service/APIs | Short-term |
| Distributed transactions everywhere (XA) | Atomicity | Familiar | Blocking, throughput | Sagas/outbox | Limited |
| Server push in HTTP/2 | Faster loads | Fewer RTTs | Cache complexity | 103 Early Hints/preload | Removed from Chrome |
| Pure lambda architecture | Batch+real-time | Accuracy | Duplicate code | Kappa/unified engines | Some cases |
| Unmanaged self-built service meshes | Uniform networking | Features | Resource/ops cost | Ambient/eBPF, library approaches | Selective |
Popularity ≠ superiority: judge by constraints.

## 12.3 Failure scenarios: detect · mitigate · recover · prevent
| Scenario | Detect | Mitigate | Recover | Prevent |
|---|---|---|---|---|
| Database overload | Latency/pool saturation, slow-query log | Shed load, kill queries, read replica, cache | Scale, index, restore capacity | Query review, pools, rate limits, load tests |
| Cache stampede | DB QPS spike after expiry | Single-flight, serve stale | Warm cache | TTL jitter, refresh-ahead |
| Hot partition | Skewed per-shard metrics | Split/salt, cache, throttle | Rebalance | Key design, adaptive sharding |
| Message duplication | Dup ids/metrics | Idempotent consumers | Dedup/repair | Idempotency keys, EOS |
| Message loss | Gaps in sequence, audits | Replay from log | Reprocess | acks=all, durable queues, outbox |
| Consumer lag | Lag metric/alert | Scale consumers, skip poison | Drain backlog | Capacity plan, partitions, DLQ |
| Network partition | Timeouts, quorum loss | Minority stops writes | Heal + reconcile | Quorums, tested CAP choice |
| Split-brain | Two leaders | Fencing, STONITH | Reconcile writes | Consensus-based election |
| Region outage | Synthetic probes, provider status | Fail over traffic | Restore/failback | Multi-region, DR drills |
| Clock skew | NTP offset alerts | Use monotonic/logical clocks | Fix time sync | Avoid wall-clock ordering, fencing tokens |
| Cascading failure | Error spread across tiers | Circuit breakers, shed load | Staged restore | Bulkheads, timeouts, capacity |
| Retry storm | Request multiplication | Disable retries, rate limit | Ramp traffic | Backoff+jitter, retry budgets |
| Thundering herd | Synchronous reconnects/expiry | Staggering | Gradual ramp | Jitter, queueing |
| Data corruption | Checksums, audits | Stop writes, isolate | PITR/backups | Checksums, immutable backups, validation |
| Schema incompatibility | Deploy errors | Rollback/feature flag | Forward-fix | Expand-contract, contract tests |
| Failed deployment | Canary metrics | Auto-rollback | Redeploy | Canary, flags, staging parity |
| Certificate expiration | Expiry monitors | Replace cert | Rotate | Auto-renewal (ACME), alerts |
| Credential leakage | Secret scanning, anomaly | Revoke/rotate | Forensics | Vault, short-lived creds, scanning |
| DDoS | Traffic anomaly | Scrubbing/CDN, rate limit | Block sources | Anycast, WAF, autoscale caps |
| Storage exhaustion | Disk alerts | Free/expand, retention | Restore service | Quotas, lifecycle |
| Memory leak | RSS growth, GC time | Restart/rolling | Fix leak | Profiling, limits |
| Deadlock | Thread dumps, hung txns | Kill victim | Restart | Lock ordering, timeouts |
| Queue backlog | Depth/age | Add workers, shed | Drain | Autoscale on lag |
| Incorrect failover | Data divergence | Freeze writes | Reconcile | Fencing, drills, automation limits |
| Partial rollback | Mixed versions | Complete forward/back | Reconcile data | Backward-compatible changes |
| Inconsistent replicas | Merkle/anti-entropy diff | Read repair | Resync | Quorums, checksums |
| Vendor outage | Dependency health | Fallback/degrade | Wait/failover | Multi-vendor critical paths, graceful degradation |

