# Part 13 – Data/AI Systems, Emerging Approaches, Final Evolution Map, Audit

## 13.1 Data-intensive & AI-enabled systems (P)
| Concept | Pressure → response | Trade-offs |
|---|---|---|
| Big data batch (Hadoop/MapReduce → Spark) | Data beyond one machine → move compute to data | Latency, ops |
| Stream processing (Flink/Kafka Streams) | Need seconds-level insight | State mgmt, late data |
| Lakehouse (Iceberg/Delta/Hudi) | Warehouse + lake silos | Maintenance (compaction) |
| Data mesh / data products | Central data team bottleneck | Governance federation |
| Feature store | Training/serving skew | Dual stores |
| ML pipeline (train → validate → registry → deploy → monitor), MLOps | Reproducibility | Drift monitoring, retraining cost |
| Model serving | Online (low-latency, autoscale GPU), batch, streaming; dynamic/continuous batching; caching; quantization; model sharding (tensor/pipeline parallel) | GPU cost/latency |
| Vector DB / embeddings | Semantic similarity | Approximate recall |
| RAG | LLM knowledge cutoff/hallucination → retrieval grounding | Retrieval quality, ACL leakage, prompt injection |
| LLM gateway/agents | Provider routing, caching, guardrails, tool use, evals, token budgets | Non-determinism, cost, observability of prompts |
| Training infra | Distributed data/model parallel, checkpointing, fault tolerance, high-bandwidth interconnects | Cost, failures at scale |
| Data governance for AI | Lineage, consent, PII filtering | Compliance overhead |

## 13.2 Emerging / current approaches (Q)
Platform engineering & IDPs · cell-based architecture · WebAssembly at edge/server · eBPF observability/networking · ambient service mesh · durable execution · local-first/CRDT apps · streaming databases (Materialize, RisingWave) · tiered/disaggregated storage (compute-storage separation) · confidential computing · serverless containers · multi-agent LLM systems · policy-as-code (OPA) · FinOps/GreenOps · supply-chain security (SLSA, sigstore) · zero-trust by default.

## 13.3 A. Timeline
| Era | Milestones |
|---|---|
| 1950s–60s | Batch, mainframes, time-sharing, IMS, packet switching idea |
| 1970s | Unix, Relational model (1970), TCP/IP, Ethernet, SQL, pipes & filters |
| 1980s | PCs, client-server, RPC, Erlang, NFS, DNS (1983) |
| 1990s | WWW, HTTP/1.0–1.1, 3-tier, CORBA, Java, SSL, GoF patterns (1994), CDNs, MQs |
| 2000s | REST, SOA/SOAP, Google GFS/MapReduce/Bigtable, Dynamo, AWS (2006), Hadoop, virtualization, CAP |
| 2010s | NoSQL wave, Kafka, Docker, Kubernetes, microservices, Spanner, Raft, serverless, GraphQL, gRPC, SRE, DevOps, HTTP/2 |
| 2020s | HTTP/3/QUIC, service mesh maturity, lakehouse, platform engineering, edge compute, vector DBs, LLM/RAG infrastructure, eBPF, WASM |

## 13.4 B. Dependency graph (condensed)
```
Hardware -> OS -> Processes/Threads -> Concurrency control
Hardware(disk/RAM) -> Storage engines -> DBs -> Transactions -> Replication -> Consensus
Networking -> RPC/HTTP -> Client-server -> Web -> Load balancing -> Caching/CDN
Replication + Partitioning + Network unreliability -> Distributed systems -> CAP/consistency models
Distributed systems -> Messaging/Streaming -> EDA -> CQRS/ES -> Sagas
Virtualization -> Cloud -> Containers -> Kubernetes -> Mesh / Serverless -> Platform engineering
All layers -> Security, Observability, Reliability (cross-cutting)
```

## 13.5 C. Problem → solution → new problem
| Problem | Solution | New problem |
|---|---|---|
| CPU idle on I/O | Multiprogramming/threads | Races, deadlocks |
| Disk slow | Caches, WAL, LSM | Invalidation, compaction |
| Shared data across users | Client-server DB | Server bottleneck |
| Fat client deployment | Web/3-tier | Server load |
| Single DB limit | Replicas, cache, shard | Lag, staleness, cross-shard ops |
| Single node failure | Replication/failover | Split-brain, consistency |
| Coordination across nodes | Consensus | Latency, complexity |
| Monolith coupling | SOA/microservices | Distributed complexity |
| Cross-service atomicity | Sagas/outbox | Eventual consistency |
| Slow server provisioning | Cloud/IaaS | Cost sprawl |
| Env drift | Containers | Orchestration |
| Cross-cutting network logic | Mesh/sidecar | Overhead |
| Idle capacity cost | Serverless | Cold start, lock-in |
| Global latency | CDN/edge/multi-region | Consistency, cost |
| Batch too slow | Streaming | Late data, state |
| Keyword search limits | Vector search/RAG | Recall, hallucination controls |

## 13.6 D. Architecture comparison matrix
| | Monolith | Modular mono | SOA | Microservices | Serverless | Event-driven | Actor | P2P |
|---|---|---|---|---|---|---|---|---|
| Deploy simplicity | ★★★ | ★★★ | ★ | ★ | ★★ | ★★ | ★★ | ★ |
| Independent scaling | ★ | ★ | ★★ | ★★★ | ★★★ | ★★★ | ★★★ | ★★★ |
| Consistency ease | ★★★ | ★★★ | ★★ | ★ | ★ | ★ | ★★ | ★ |
| Ops burden (higher worse) | low | low | high | high | low–med | med–high | med | high |
| Team autonomy | ★ | ★★ | ★★ | ★★★ | ★★★ | ★★★ | ★★ | n/a |
| Debuggability | ★★★ | ★★★ | ★★ | ★ | ★ | ★ | ★ | ★ |

## 13.7 E. Concept checklist (tick when you can explain, trade-offs, and failure modes)
Foundations ☐ OS/concurrency ☐ Networking ☐ App architectures ☐ APIs ☐ Databases/indexes ☐ Transactions/isolation ☐ Replication/sharding ☐ Consistency/CAP/PACELC ☐ Consensus ☐ Caching ☐ Messaging/streams ☐ Reliability/SLOs ☐ Security/AuthN/Z ☐ Observability ☐ CI/CD/IaC ☐ Containers/K8s ☐ Serverless/edge ☐ Data engineering ☐ ML/RAG systems ☐ Cost/FinOps ☐ Governance/privacy ☐ DR.

## 13.8 F. Commonly omitted by tutorials
Storage-engine internals (compaction, write amplification) · fsync/lying disks · clock skew & fencing tokens · idempotency design · retry budgets · tail latency math · schema evolution/expand-contract · backup restore testing · connection-pool sizing · cache invalidation races · TLS/cert lifecycle · cardinality costs · operational/human factors · multi-tenancy isolation · GDPR deletion in logs/backups · egress costs · capacity N+1 under AZ loss · training/serving skew · prompt-injection in RAG.

## 13.9 G. Glossary (selected; full terms are defined in each part's tables)
**ACID** atomic, consistent, isolated, durable txns · **Backpressure** consumer slows producer · **Bloom filter** probabilistic set · **CAP** consistency/availability/partition tolerance · **CDC** change data capture · **CRDT** conflict-free replicated data type · **DLQ** dead-letter queue · **ISR** in-sync replicas · **LSM** log-structured merge tree · **MVCC** multi-version concurrency control · **Quorum** majority agreement · **RPO/RTO** data-loss/time-to-recover targets · **Saga** compensating transaction chain · **SLI/SLO/SLA** indicator/objective/agreement · **Idempotency** repeat-safe operation · **Watermark** event-time progress marker · **mTLS** mutual TLS · **ANN** approximate nearest neighbor · **HNSW** graph ANN index.

## 13.10 H–I. Learning path & milestones
1. **Beginner:** OS/CPU/memory, networking (DNS/HTTP/TCP), SQL, REST, one language deeply, Git, basic cloud. *Milestone:* build 3-tier CRUD app with auth and deploy it.
2. **Intermediate:** indexing, transactions, caching, queues, LB, Docker, CI/CD, observability basics. *Milestone:* scale an app to replicas + cache + async worker with dashboards.
3. **Advanced:** replication/sharding, consensus, consistency models, Kafka, K8s, sagas, SLOs, security architecture. *Milestone:* design/operate multi-service system with failure drills.
4. **Expert:** multi-region, storage-engine design, capacity/cost modeling, org/architecture strategy, AI platform design. *Milestone:* lead a migration (strangler) with measurable SLOs and rollback.

## 13.11 J. Practice questions
1. Why does adding a read replica not fix a write-bound DB? 2. When can a cache make correctness worse? 3. Compute availability of 3 serial 99.95% services. 4. Why is exactly-once delivery impossible end-to-end? 5. Design idempotent payment API. 6. Explain write skew and how snapshot isolation allows it. 7. Pick a partition key for chat and justify. 8. How does Raft handle a partitioned leader? 9. Why prefer p99 over average? 10. Compare 2PC vs saga for order checkout.

## 13.12 K. Design exercises
URL shortener · news feed · chat · Dropbox · rate limiter · payment ledger · ticket booking · Kafka-like log · distributed lock service · web crawler · metrics system · RAG over enterprise docs · multi-region active-active shopping cart.

## 13.13 L. Implementation projects
1. Key-value store with WAL + LSM + bloom filter. 2. Raft implementation (leader election + log replication). 3. Consistent-hash cache cluster. 4. Token-bucket distributed rate limiter. 5. Mini message broker with consumer groups and offsets. 6. Saga orchestrator with outbox. 7. Load balancer with health checks. 8. Tiny search engine (inverted index + BM25). 9. Observability stack (OpenTelemetry → Prometheus/Grafana/Jaeger). 10. RAG service with hybrid retrieval and evals. *(Java options in this repo: extend `threads/`, `datastructures/custom/`, `designpatterns/`.)*

## 13.14 M. Final completeness audit
| Prompt category | Covered in | Status |
|---|---|---|
| Foundations/hardware/OS/concurrency/filesystems | Part 1 | ✔ |
| Networking | Part 2 | ✔ |
| App architectures, APIs | Part 3 | ✔ |
| Databases, transactions, consistency, recovery, governance | Part 4 | ✔ |
| Scalability, caching | Part 5 | ✔ |
| Distributed systems, messaging, streaming | Part 6 | ✔ |
| Reliability, security, observability | Part 7 | ✔ |
| DevOps, cloud, containers, serverless, edge, FinOps | Part 8 | ✔ |
| Design patterns (GoF + enterprise + distributed) | Part 9 | ✔ |
| 45 system types | Part 10 | ✔ |
| Trade-offs, confused concepts, numbers | Part 11 | ✔ |
| Decision trees, failed ideas, failure cases | Part 12 | ✔ |
| Data/AI/emerging, evolution map, learning path | Part 13 | ✔ |

**Honest limitation:** the full 22-heading template is applied in depth to pivotal ★ concepts (microservices, Kafka-style logs, etc.); remaining concepts use condensed table rows covering definition, pressure, trade-offs, failure modes and successors. Any concept can be expanded to the full template on request (ask: "expand <concept> using the 22-section template").

