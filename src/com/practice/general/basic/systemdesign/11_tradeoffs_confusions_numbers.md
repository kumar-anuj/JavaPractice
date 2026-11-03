# Part 11 – Trade-offs, Confused Concepts, Numerical Reasoning

## 11.1 Trade-off dimensions
Latency · Throughput · Scalability · Availability · Consistency · Durability · Fault tolerance · Cost · Complexity · Security · Operability · Maintainability · Developer productivity · Time to market · Vendor dependence · Data correctness · RTO · RPO · Geographic scope.

| Decision | Latency | Throughput | Availability | Consistency | Cost | Complexity |
|---|---|---|---|---|---|---|
| Add cache | ↓↓ | ↑ | ↑ (shields DB) | ↓ (stale) | + | + |
| Async queue | ↑ end-to-end | ↑ | ↑ | eventual | + | ++ |
| Sync replication | ↑ writes | ~ | ↑ durability | strong | + | + |
| Async replication | ~ | ~ | RPO>0 | eventual | + | + |
| Sharding | ~ | ↑↑ | partial-failure | cross-shard weak | ++ | +++ |
| Microservices | ↑ (hops) | ↑ per service | isolation ↑ | eventual | ++ | +++ |
| Multi-region active-active | ↓ local | ↑ | ↑↑ | conflicts/↑ latency for strong | +++ | ++++ |
| Serverless | cold starts | burst ↑ | managed | n/a | pay-per-use | lock-in |

## 11.2 Commonly confused concepts
| Pair | Distinction |
|---|---|
| Process vs thread | Isolated address space vs shared memory within process |
| Concurrency vs parallelism | Dealing with many things vs doing many at once |
| Sync vs async | Caller waits for completion vs continues/gets notified |
| Blocking vs non-blocking | Thread parks on call vs call returns immediately (can be sync+non-blocking polling) |
| Stateful vs stateless | Server remembers client context vs each request self-contained |
| Vertical vs horizontal | Bigger node vs more nodes |
| Replication vs partitioning | Copies of same data vs different subsets (usually combined) |
| Sharding vs partitioning | Sharding = partitioning across nodes; partitioning may be within one node |
| Queue vs stream | Consume-and-delete work distribution vs retained ordered replayable log |
| Event bus vs message broker | Pattern/channel concept vs server software implementing it |
| Cache vs database | Disposable derived copy for speed vs source of truth with durability |
| OLTP vs OLAP | Many small transactional ops vs few large analytic scans |
| Strong vs eventual consistency | All see latest write vs converge later |
| Availability vs durability | System answers vs data persists (can be down yet durable) |
| Reliability vs resilience | Consistently correct vs recovers from adversity |
| Latency vs throughput | Time per request vs requests per time (batching trades one for other) |
| REST vs RPC | Resources + uniform verbs vs procedure calls |
| REST vs GraphQL | Server-defined shapes vs client-defined queries |
| TCP vs UDP | Reliable ordered stream vs best-effort datagrams |
| HTTP/1.1 vs 2 vs 3 | Text/serial per connection → multiplexed over TCP → multiplexed over QUIC |
| Proxy vs reverse proxy | Acts for clients vs acts for servers |
| LB vs API gateway | Distributes traffic vs adds API management (auth, quota, transformation); often combined |
| Service mesh vs API gateway | East-west service-to-service vs north-south edge |
| Monolith vs modular monolith | Unstructured vs enforced module boundaries (single deploy) |
| Monolith vs microservices | Single deployable vs many; ACID vs eventual |
| SOA vs microservices | Enterprise-wide, ESB, coarse vs decentralized, fine-grained, smart endpoints/dumb pipes |
| Containers vs VMs | Process isolation sharing kernel vs full virtual hardware |
| Kubernetes vs serverless | You operate cluster vs provider abstracts it |
| Batch vs stream | Bounded vs unbounded data |
| ETL vs ELT | Transform before vs after load |
| Warehouse vs lake vs lakehouse | Structured/curated vs raw/any vs lake with ACID table format |
| AuthN vs AuthZ | Who are you vs what may you do |
| Encryption vs hashing | Reversible with key vs one-way |
| Logging vs monitoring vs observability | Records vs known-signal watching vs ability to explore unknowns |
| DR vs HA | Recover from site loss vs survive component failures without downtime |
| Backup vs replication | Point-in-time protection from corruption/deletion vs live copy that replicates mistakes |
| Exactly-once vs idempotent | Delivery guarantee (hard) vs effect-level safety under duplicates (practical) |
| 2PC vs Saga | Atomic, blocking, locks vs eventually consistent with compensations |
| CQRS vs event sourcing | Split read/write models vs store events as truth; independent, often combined |
| Event sourcing vs EDA | Persistence strategy vs integration/communication style |

## 11.3 Numerical reasoning cheat sheet
| Topic | Formula / example |
|---|---|
| QPS | `DAU × actions/day / 86,400`; 10 M DAU × 10 req = 1,157 QPS avg; peak ≈ 2–10× |
| Bandwidth | `QPS × payload`; 5,000 QPS × 200 KB = 1 GB/s (8 Gbps) |
| Storage | `records × size × retention × replication`; 1 B × 1 KB × 3 = 3 TB |
| Replication overhead | N× storage; erasure coding (k+m)/k (10+4 = 1.4×) |
| Cache hit ratio | `avg latency = h·Tc + (1−h)·Td`; h=0.95, Tc=1 ms, Td=20 ms → 1.95 ms |
| DB capacity | Single Postgres node ~ thousands–tens of thousands simple TPS; shard when write-bound/size-bound |
| Queue/lag | Backlog growth = `(in − out) × t`; drain time = backlog / (out − in); lag = latest offset − committed offset |
| Latency budget | Sum of hops; SLO 200 ms across 5 serial calls → ~40 ms each |
| Availability | See Part 7 |
| Probability of failure | `1 − (1−p)^n`; 1,000 disks at 2% AFR → ~20 failures/year |
| Recovery | RTO = detect + decide + failover + warm-up; RPO = replication lag / backup interval |
| Headroom | Provision for peak ×1.3–2 and N+1 (survive AZ loss: with 3 AZs run at ≤ 66%) |
| Traffic growth | `V·(1+g)^t`; 10%/month → ×3.1/year |
| Cost | `instances × $/h × 730`, plus egress ($/GB), storage tiers, requests, people |

