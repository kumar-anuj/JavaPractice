# Part 9 – Design Patterns (Problem · Solution · Consequences · Alternatives)

## 9.1 GoF object-oriented patterns
| Pattern | Problem | Solution | Consequences | Alternative |
|---|---|---|---|---|
| Singleton | Need exactly one instance | Private ctor + global access (enum/holder in Java) | Hidden global state, hard tests, per-JVM only | DI-managed single scope |
| Factory method | Creation varies by subclass | Subclass decides product | More classes | Static factory, DI |
| Abstract factory | Families of related objects | Factory of factories | Hard to add product kinds | DI container |
| Builder | Many optional ctor args | Step-wise construction | Verbosity | Named params, records |
| Prototype | Costly creation | Clone existing | Deep-copy pitfalls (see `cloning/`) | Copy constructors |
| Adapter | Incompatible interface | Wrap & translate | Extra layer | Anti-corruption layer |
| Bridge | Abstraction × implementation explosion | Separate hierarchies | Upfront design | Strategy |
| Composite | Tree of part/whole | Uniform component interface | Overly general | — |
| Decorator | Add behavior dynamically | Wrap same interface | Many small wrappers (Java I/O streams) | Inheritance, AOP |
| Facade | Complex subsystem | Simple entry API | Can hide needed features | API gateway (distributed analog) |
| Flyweight | Many similar objects | Share intrinsic state | Complexity | Interning, caching |
| Proxy | Control access (lazy, remote, security) | Surrogate object | Indirection | Sidecar (distributed analog) |
| Chain of responsibility | Multiple handlers | Pass along chain | Unhandled requests | Middleware pipelines/filters |
| Command | Encapsulate request | Object with execute/undo | Class proliferation | Lambdas; queues of commands |
| Interpreter | Grammar evaluation | AST evaluation | Slow, complex grammars | Parser generators |
| Iterator | Traverse w/o exposing structure | Iterator object | — | Streams |
| Mediator | N-to-N coupling | Central coordinator | God object | Event bus |
| Memento | Snapshot/undo | Opaque state capture | Memory | Event sourcing |
| Observer | One-to-many notification | Subscribe/notify | Leaks, ordering | Pub/sub, reactive streams |
| State | Behavior per state | State objects | More classes | State machines |
| Strategy | Swap algorithms | Interchangeable objects | Client chooses | Lambdas |
| Template method | Fixed skeleton, varying steps | Base class steps | Inheritance coupling | Strategy/composition |
| Visitor | New operations on stable hierarchy | Double dispatch | Hard to add types | Pattern matching/sealed types |

## 9.2 Enterprise / application patterns
| Pattern | Problem | Solution | Consequences / alternative |
|---|---|---|---|
| Repository | Domain coupled to persistence | Collection-like abstraction | Leaky for complex queries; Spring Data |
| Unit of work | Track changes & commit atomically | Collect changes, flush in txn | Session lifetime bugs |
| Data mapper vs Active record | Where mapping lives | Separate mapper (Hibernate) vs record knows DB (Rails) | Mapper: decoupled, heavier; AR: simple, coupled |
| Dependency injection | Hard-wired deps | Container supplies deps | Magic/config; constructor injection |
| Service layer / Domain model / Transaction script | Where logic lives | Orchestration layer; rich objects; procedural script | Script for simple CRUD, model for complex domains |
| Specification | Reusable business rules | Composable predicates | Verbosity |
| Front controller / MVC | Central request handling | Single entry + dispatch | — |
| API gateway / BFF | Many clients/services | Edge façade | SPOF, logic creep |
| Strangler fig | Migrate legacy incrementally | Route gradually to new system | Dual-run cost; vs big-bang rewrite |
| Anti-corruption layer | Legacy model leaks into new | Translation layer | Maintenance |
| Circuit breaker | Cascading failure | Trip after failures | Tuning; with fallback |
| Bulkhead | One failure exhausts all resources | Isolated pools | Underuse |
| Retry / Timeout / Retry with backoff | Transient faults | Re-attempt bounded with jitter | Storms; needs idempotency |
| Rate limiter / Throttling | Overload/abuse | Token bucket etc. | Distributed counting |
| Cache-aside | Read-heavy | Lazy load cache | Staleness |
| Sidecar / Ambassador / Adapter service | Cross-cutting/ outbound proxying / protocol translation | Co-located helper process | Resource overhead |
| Saga | Cross-service business txn | Local txns + compensations (orchestration vs choreography) | Eventual consistency, compensation design; alt: 2PC |
| TCC | Reservation-style distributed txn | Try/Confirm/Cancel | Business-level undo |
| Two-phase commit | Atomic multi-resource commit | Prepare then commit via coordinator | Blocking, latency; alt: saga |
| Outbox / Inbox / Transactional messaging | Dual-write problem / duplicates | Event in same txn; dedup table | Relay latency |
| Idempotency key | Retried writes | Client key stored w/ result | Storage/TTL |
| Competing consumers | Scale processing | Workers share queue | Ordering lost |
| Claim check | Large payloads | Store blob, pass ref | Extra store |
| Dead-letter queue / Priority queue | Poison/urgent messages | Side queue / ranked | Starvation of low priority |
| Fan-out / Fan-in / Scatter-gather | Parallelism | Broadcast then aggregate | Tail latency |
| Leader election | Single coordinator | Lease/consensus | Split-brain risk |
| Sharding / Consistent hashing | Data/load distribution | Partition keys | Resharding |
| Materialized view | Expensive reads | Precomputed view | Freshness |
| CQRS / Event sourcing / Event notification / Event-carried state transfer | See Parts 3 & 6 | | |
| Hedged requests | Tail latency | Duplicate after delay | Extra load |

