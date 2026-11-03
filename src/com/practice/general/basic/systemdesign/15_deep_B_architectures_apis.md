# Part 15 – Deep Dive B: Architectures and APIs (with examples and diagrams)

## 15.1 Evolution in pictures
```
[Mainframe]            [2-tier]               [3-tier]                [Microservices]
 terminals --> host     fat client --> DB      browser -> app -> DB    gateway -> many svcs -> own DBs
```

## 15.2 Monolith → modular monolith → microservices (same shop app)
**Monolith**
```
+--------------------------------------------+
|  Web | Catalog | Cart | Orders | Payments  |   one process, one DB, ACID
+------------------------+-------------------+
                         v
                      [ One DB ]
```
`orderService.place()` calls `paymentService.charge()` **in-process** inside one DB transaction. Easy, atomic. Pain: one bug can take everything down; to scale Catalog you scale all.

**Modular monolith** – same deployable but modules only talk through public interfaces:
```java
// module boundary: other modules may only use this interface
public interface PaymentApi { PaymentResult charge(OrderId id, Money amount); }
// internal classes are package-private; enforce with ArchUnit / Java modules
```
**Microservices** – `charge()` becomes a network call:
```
Orders --HTTP/gRPC--> Payments    (timeout? retry? duplicate charge? => idempotency key)
Orders --OrderPlaced event--> Broker --> Inventory, Email
```
**Migration (Strangler Fig):**
```
Phase 1: Client -> Facade -> [Monolith]
Phase 2: Client -> Facade -> /payments -> [New Payments svc]  ; rest -> Monolith
Phase 3: repeat until monolith is empty, then retire.
```

## 15.3 Layered, Hexagonal, Clean – in code shape
```
Layered:            Hexagonal (Ports & Adapters):
 Controller            [REST adapter] ->  (port) ->  DOMAIN  <- (port) <- [JPA adapter]
 Service                                              core    <- (port) <- [Kafka adapter]
 Repository -> DB      Domain depends on nothing; adapters depend on domain.
```
```java
interface OrderRepository { void save(Order o); }          // port (in domain)
class JpaOrderRepository implements OrderRepository { ... } // adapter (infrastructure)
class PlaceOrder { PlaceOrder(OrderRepository r){...} }     // use-case depends on port only
```
Benefit: test the domain with an in-memory fake. Cost: extra interfaces for trivial CRUD – don't use for a 3-table app.

## 15.4 MVC / MVP / MVVM
```
MVC : View -> Controller -> Model -> View (observes)
MVP : View <-> Presenter <-> Model       (Presenter updates a passive View)
MVVM: View <==binding==> ViewModel <-> Model
```
Example: Spring MVC controller returns a model + view name; Angular/WPF use two-way binding (MVVM).

## 15.5 REST vs RPC vs GraphQL – same query three ways
Need: user 42 with their last 3 orders.
```
REST     : GET /users/42 ; GET /users/42/orders?limit=3        (2 round trips, maybe over-fetch)
gRPC     : rpc GetUserWithOrders(GetReq{id:42,limit:3})         (1 call, typed, binary)
GraphQL  : query { user(id:42){ name orders(first:3){ id total } } }   (1 call, client picks fields)
```
| Concern | REST | gRPC | GraphQL |
|---|---|---|---|
| Caching | HTTP caching built-in | Manual | Hard (single POST) – persisted queries |
| Perf | Medium | High | Variable (N+1, deep queries) |
| Browser | Native | grpc-web | Native |
| Risk | Chatty | Debugging binary | Query-cost attacks – limit depth/complexity |

**REST design example**
```
POST   /orders            201 Created + Location: /orders/9      (Idempotency-Key header)
GET    /orders/9          200 ; ETag for caching
PATCH  /orders/9          partial update ; If-Match for optimistic concurrency
DELETE /orders/9          204
GET    /orders?cursor=abc&limit=50   (cursor pagination beats offset at scale)
```
Versioning: URL (`/v2/`) or header; never break existing fields – only add.

## 15.6 Real-time delivery options
```
Polling       : client asks every N s            (simple, wasteful, delayed)
Long polling  : server holds request until event (works anywhere, many open conns)
SSE           : one-way server->client stream over HTTP, auto-reconnect (live feed, LLM tokens)
WebSocket     : full duplex TCP channel           (chat, games, trading)
Webhook       : server->server HTTP callback      (payment succeeded)
```
Webhook receiver rules: verify HMAC signature, respond 2xx fast, process async, be idempotent (provider retries).

**WebSocket scaling diagram**
```
Clients <--ws--> [WS gateway 1] \
Clients <--ws--> [WS gateway 2]  >-- Pub/Sub (Redis/Kafka) -- [Chat service]
Clients <--ws--> [WS gateway 3] /      user->gateway map in Redis
```
To send to user U: look up gateway holding U → publish to it. Reconnect storms after deploy → jittered reconnect.

## 15.7 Event-driven, CQRS, Event Sourcing – example
Bank account:
```
Commands -> [Write model] --append--> Event Store: Deposited(100), Withdrawn(30), Deposited(5)
                                         |
                                  projector (async)
                                         v
                              [Read model: balance=75, statement view]  <- Queries
```
- State = replay of events (`100 − 30 + 5 = 75`). Snapshots every N events speed up rebuild.
- **Eventual consistency:** after a command, the read model lags by ms–s; UI can show the command result optimistically.
- **Schema evolution:** events are forever – version them (`Deposited_v2`) and write *upcasters*.
- **GDPR deletion:** encrypt personal data per-user and delete the key (crypto-shredding).

## 15.8 Actor model example
```
[Actor: Cart-42]  mailbox: AddItem, Checkout, ...  (processes one message at a time, no locks)
       | supervises
[Actor: Payment-42]  crash -> supervisor restarts it (let-it-crash)
```
Good for per-entity state (chat room, device, game session). Weak spot: cross-actor transactions, unbounded mailboxes (need bounded queues/backpressure).

## 15.9 Pipes & Filters, Microkernel, Space-based, P2P, Blackboard
```
Pipes&Filters:  cat log | grep ERROR | sort | uniq -c    (Unix) ; ETL stages ; Spring Integration
Microkernel  :  [Core] + plugins (IDE, browsers, OSes)    -> stable core API, extensions load dynamically
Space-based  :  processing units each with in-memory data grid; replication between units; DB asynchronous behind
P2P          :  peers discover via DHT (Kademlia); no central server (BitTorrent)
Blackboard   :  shared workspace; specialist agents read/write until solution emerges (speech recognition; modern multi-agent LLMs)
```

## 15.10 API gateway vs BFF vs service mesh
```
 Mobile --> [BFF-mobile] \
 Web    --> [BFF-web]     >-- API Gateway (authN, quotas, routing) --> services
 Partner-> [Public API]  /                                              | east-west traffic
                                                              [sidecar proxies = service mesh: mTLS, retries, metrics]
```
Gateway = north-south; mesh = east-west. Don't put business logic in the gateway.

## 15.11 Serverless example & caveats
```
Upload image -> S3 event -> Lambda(resize) -> S3 thumbnails -> DynamoDB metadata
```
Cold start (100 ms–seconds), 15-minute limit, concurrency limits, per-invocation billing. Cost crossover: steady 24×7 high-QPS load is usually cheaper on containers/VMs.

