# Part 3 – Application Architectures and API Styles

## 3.1 Evolution chain
```
Single process -> Mainframe/terminal -> 2-tier client-server (fat client + DB)
 -> 3-tier web (browser / app server / DB) -> N-tier
 -> Layered / MVC family -> Component/plugin -> Modular monolith
 -> SOA (ESB) -> Microservices -> Serverless (FaaS/BaaS)
 parallel: Event-driven, CQRS/ES, Actors, P2P, Space-based, Pipeline
```

## 3.2 Tiered architectures (C–D)
| Architecture | Emerged | Replaced/supplemented | Pros | Cons | Next |
|---|---|---|---|---|---|
| Client-server | 1980s | Mainframe/desktop silos | Shared data, central control | Server SPOF, LAN assumptions | n-tier |
| Thick client (2-tier) | Late 80s–90s (VB/PowerBuilder + DB) | Terminals | Rich UI, fast dev | Business logic on every client, DB connections per client, painful deployment | 3-tier |
| Thin client / web app | Mid 90s | Fat client | Zero install, central deploy | Page reloads, server-heavy | AJAX/SPA, mobile |
| 3-tier | 90s | 2-tier | Logic centralized, DB pooled, independently scalable tiers | App tier becomes a monolith | N-tier / SOA |
| N-tier | 2000s | 3-tier | Cache/queue/search tiers added | Latency per hop, ops complexity | Microservices |

## 3.3 UI/code-structure patterns
| Pattern | Idea | Trade-off | Relation |
|---|---|---|---|
| MVC (1979 Smalltalk) | Model/View/Controller | View↔model coupling variants | Web frameworks (Spring MVC, Rails) |
| MVP | Presenter mediates, passive view | More boilerplate, testable | Partial successor of MVC (desktop) |
| MVVM | Data-binding ViewModel | Binding magic, debugging | WPF/Angular/SwiftUI |
| Layered | Presentation/Service/Domain/Data | Layers become pass-throughs; DB-centric | Default enterprise style |
| Hexagonal / Ports & Adapters | Domain core, adapters for I/O | Indirection | Reaction to DB-centric layering |
| Onion / Clean | Dependencies point inward | Ceremony for CRUD apps | Specialization of hexagonal |
| Component / Plugin / Microkernel | Core + extensions (Eclipse, IDEs, OS kernels) | Versioning/API stability | Used for extensible products |
| Modular monolith | One deployable, strict module boundaries | Needs discipline (enforce w/ tooling) | Often the right step before microservices |

## 3.4 Distributed architectures
| Architecture | Emerged | Pressure | Pros | Cons | Alternative/successor |
|---|---|---|---|---|---|
| ★ Monolith | Always | Simplicity | Simple deploy, local calls, ACID txns, easy debugging | Scale whole thing, coupling, long builds, risky deploys, tech lock | Modular monolith / SOA |
| ★ SOA | ~1998–2005 (SOAP/WSDL, ESB) | Integrate enterprise systems | Reuse, standard contracts | Heavy XML, central ESB bottleneck/"smart pipes", governance | Microservices (reaction to ESB weight) |
| ★ Microservices | ~2011+ (Netflix, Amazon) | Org scale, independent deploy, independent scale | Team autonomy, polyglot, fault isolation, scale by service | Distributed-system problems: latency, partial failure, data consistency, observability, versioning, cost, Conway's law | Service mesh, sagas, platform eng.; or back to modular monolith |
| Event-driven (EDA) | 2000s+ | Decoupling, async | Loose coupling, extensibility | Hard tracing, eventual consistency, ordering | Event sourcing, workflow engines |
| DDD | 2003 (Evans) | Complex domains | Bounded contexts → service boundaries | Learning curve, over-modeling | Underpins microservices boundaries |
| CQRS | 2010 | Read/write needs differ | Independent scaling & models | Sync lag, complexity | Often with event sourcing (not required) |
| Event sourcing | ~2005+ | Audit, temporal queries | Full history, rebuildable views | Schema evolution of events, snapshots, rebuild time, GDPR deletion | Plain audit tables/CDC |
| Actor systems (Erlang 1986, Akka, Orleans) | Parallel branch | Concurrency without shared state | Isolation, supervision, location transparency | Debugging, mailbox overflow, ordering across actors | Threads+locks, CSP |
| Pipeline / Pipes & Filters | Unix 1970s | Composable processing | Reuse, parallelism | Latency, schema coupling | Stream processing |
| Space-based (tuple space/IMDG) | 2000s | Extreme scalability of stateful web | No DB bottleneck | Complex data consistency | Distributed caches, Kafka+KV |
| Peer-to-peer | 1999 Napster, BitTorrent, blockchain | No central cost/control | Scales with users, resilient | Discovery, security, NAT, consistency | Client-server + CDN |
| Broker architecture | CORBA 90s, MQ | Decouple clients from servers | Location transparency | Broker SPOF/overhead | Service discovery/mesh |
| Blackboard | AI 1980s | Non-deterministic problem solving | Collaborative knowledge sources | Control complexity | Rule engines, multi-agent LLM orchestration (modern echo) |
| Serverless: FaaS | 2014 AWS Lambda | Idle cost, ops burden | Scale-to-zero, pay-per-use | Cold starts, limits, vendor lock-in, observability, statelessness | Containers-on-demand (Cloud Run/Fargate) |
| BaaS | 2010s (Firebase) | Skip backend | Fast MVP | Lock-in, limited logic | Custom backend later |
| BFF | 2015 | One API doesn't suit all clients | Tailored payloads | N backends to maintain | GraphQL gateway |
| API gateway | 2010s | Cross-cutting concerns | Auth, rate limit, routing | Bottleneck/SPOF, logic creep | Service mesh for east-west |

### ★ Monolith vs modular monolith vs microservices vs SOA
| Aspect | Monolith | Modular monolith | SOA | Microservices |
|---|---|---|---|---|
| Deploy unit | 1 | 1 | Few big services | Many small |
| Data | 1 shared DB | 1 DB, schema per module | Shared / per-service | DB per service |
| Comms | In-process | In-process via interfaces | ESB/SOAP | HTTP/gRPC/events |
| Consistency | ACID | ACID | Mixed | Eventual/sagas |
| Org fit | <~20 devs | 10–100 | Enterprise integration | Many autonomous teams |
| Main risk | Big ball of mud | Boundary erosion | ESB bottleneck | Distributed monolith |

## 3.5 Communication & API styles
| Style | Emerged | Pressure | Pros | Cons | Use when |
|---|---|---|---|---|---|
| RPC (Sun RPC, CORBA, RMI) | 80s–90s | Call remote like local | Simple model | Hides network failures (fallacy), tight coupling | → REST/SOAP reaction |
| SOAP/WSDL | 1998 | Interoperable enterprise contracts | WS-* security/transactions, strong contracts | Verbose, complex | Legacy B2B, banking |
| REST (2000 Fielding) | 2000s | Web-scale simple resources | Cacheable, uniform, stateless, tooling | Over/under-fetching, chatty, no standard schema (OpenAPI helps) | Public APIs |
| GraphQL (2015) | Facebook | Mobile clients needing flexible queries | One round trip, typed schema | Caching harder, N+1, query-cost attacks, authorization per field | Many clients, aggregated data |
| gRPC (2015) | Google | Efficient internal RPC | Protobuf, HTTP/2, streaming, codegen | Browser support (grpc-web), debuggability | Internal east-west |
| WebSockets | 2011 | Bidirectional realtime | Low overhead push | Stateful connections, scaling LBs, reconnect logic | Chat, trading |
| SSE | 2009 | Server→client stream over HTTP | Simple, auto-reconnect | One-way, connection limits | Feeds/notifications |
| Long polling | 2000s | Push emulation | Works anywhere | Held connections, latency | Fallback |
| Webhooks | 2000s | Server→server callbacks | Simple event push | Delivery guarantees, security (signing), retries | SaaS integration |

REST vs RPC: resource-oriented, cache-friendly vs action-oriented, efficient. REST vs GraphQL: multiple endpoints/HTTP caching vs single endpoint/client-shaped queries.

## 3.6 ★ Template-style deep dive: Microservices
1. **Definition** – small, independently deployable services around business capabilities, each owning its data.
2. **Context** – born from monolith pain at Amazon/Netflix; enabled by cloud, containers, CI/CD.
3. **Earlier** – monolith/SOA with ESB.
4. **Problem solved** – deploy coupling, scaling granularity, team blocking.
5. **How** – per-service repo/pipeline/DB; sync (REST/gRPC) + async (events); gateway at edge; discovery; config; observability.
6. **Components** – services, gateway, registry, config server, message broker, tracing, CI/CD, secrets.
7. **Diagram**
```
Client -> CDN -> LB -> API Gateway -> [Order Svc]--DB1
                                   -> [User Svc] --DB2
                                   -> [Pay Svc]  --DB3
              Order --events--> Broker --> Inventory, Notification
```
8. **Use cases** – large orgs, uneven scaling, polyglot needs.
9. **Pros** – independent deploy/scale/failure domains, team autonomy.
10. **Cons** – network latency, partial failure, no cross-service ACID, versioning, test environments, ops/cost, cognitive load.
11. **Failure modes** – cascading failure, retry storms, distributed monolith (lockstep deploys), chatty calls, schema drift.
12. **Perf/scale** – scale per service; p99 = compounded tail latency across fan-out.
13. **Consistency** – eventual; sagas, outbox, idempotency.
14. **Security** – more attack surface; mTLS, zero trust, per-service authz, secrets.
15. **Ops** – needs tracing, centralized logs, SLOs, platform team.
16. **Cost** – infra overhead (sidecars, duplicated runtimes) + people.
17. **Why next** – operational burden → service mesh, platform engineering, serverless, **or** modular monolith; consistency pain → sagas/event sourcing.
18. **Modern relevance** – dominant at scale, but "microservices-first" for small teams is often premature.
19. **Alternatives** – modular monolith, SOA, serverless.
20. **Interview** – decompose e-commerce; handle distributed transactions; service communication.
21. **Example** – Order service publishes `OrderPlaced` via outbox; Inventory reserves; Payment charges; saga compensates on failure.
22. **Summary** – autonomy ↑, complexity ↑↑, consistency ↓.

