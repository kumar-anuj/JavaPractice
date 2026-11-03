# System Design Encyclopedia

A historical, causal guide: **requirement → limitation → architectural response → new trade-off → later response.**

## How to read this guide
- **Relationship labels** used between concepts: `direct successor`, `partial successor`, `alternative`, `parallel evolution`, `optimization`, `specialization`, `reaction to failure`, `reaction to scale`, `reaction to cost`, `reaction to hardware`, `reaction to business requirements`.
- **Full template** (22 headings from the prompt) is applied in full to the pivotal concepts (marked ★). Other concepts use a **condensed row**: *Definition · Pressure (why it appeared) · How it works · Pros · Cons · Failure modes · Next/alternative · Still correct when*.
- "Best practice" is never used without its condition.

## Table of Contents
| Part | File | Content (prompt sections) |
|---|---|---|
| 1 | `01_foundations.md` | A–B: Hardware, OS, concurrency, storage, filesystems, virtualization (§4) |
| 2 | `02_networking.md` | Networking end to end (§5) |
| 3 | `03_app_architecture.md` | B–D, H–J: Application architectures & API styles (§6) |
| 4 | `04_databases.md` | E: Databases, transactions, consistency, recovery (§7) |
| 5 | `05_scalability_caching.md` | Scalability & caching (§8, §9) |
| 6 | `06_distributed_messaging.md` | G, M: Distributed systems, messaging, streaming (§10, §11) |
| 7 | `07_reliability_security_observability.md` | Reliability, security, observability (§12–14) |
| 8 | `08_deployment_cloud.md` | K–L, N–O: Infrastructure, cloud, containers, serverless, edge (§15) |
| 9 | `09_design_patterns.md` | Design patterns at every level (§16) |
| 10 | `10_system_types.md` | 45 system designs mapped to concepts (§17) |
| 11 | `11_tradeoffs_confusions_numbers.md` | Trade-offs, confused concepts, numeric reasoning (§18, §19, §22) |
| 12 | `12_decisions_failures_failed_ideas.md` | Decision trees, failed ideas, failure cases (§23–25) |
| 13 | `13_data_ai_emerging_evolution_map.md` | P–Q: data/AI, emerging, final evolution map & audit (§26–27) |

## Master evolution chain (summary)
```
Bare hardware -> batch/mainframe -> time-sharing -> desktop/single process
 -> client-server (2-tier) -> 3-tier/web -> relational DB + ACID
 -> internet scale: caching, LB, replication, sharding
 -> distributed systems: consensus, quorums, eventual consistency
 -> SOA -> microservices -> cloud (IaaS/PaaS) -> containers/K8s
 -> event-driven/streaming -> serverless/managed -> edge/multi-region
 -> data-intensive + AI/ML platforms -> platform engineering
```
Parallel branches (not linear): NoSQL families vs NewSQL; REST vs RPC vs GraphQL; actor model vs threads; P2P vs client-server; Lambda vs Kappa; VMs vs containers vs unikernels/microVMs.

