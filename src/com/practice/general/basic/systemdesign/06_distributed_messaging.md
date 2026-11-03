# Part 6 – Distributed Systems, Messaging and Streaming

## 6.1 Distributed systems
**Definition:** independent computers coordinating via message passing that appear as one system. **Why hard:** *partial failure*, unbounded latency, no shared clock, no global state, and you can't distinguish a slow node from a dead one.

### Failure handling
| Concept | Meaning | Caveat |
|---|---|---|
| Failure detection / heartbeats / health checks | Periodic liveness (phi-accrual detectors) | Only probabilistic |
| Timeouts | Bound waiting | Too short → false failure; too long → resource pile-up; propagate deadlines |
| Retries | Re-attempt | Need idempotency; amplify load (retry budget) |
| Exponential backoff + jitter | Spread retries | Avoid retry storms |
| Circuit breaker | Stop calling failing dependency (closed/open/half-open) | Tuning; fallbacks |
| Bulkhead | Isolate pools per dependency/tenant | Lower utilization |
| Rate limit / load shedding / graceful degradation | Protect core function | Define priorities |
| Leases | Time-bound ownership | Clock/pauses (GC) can expire silently |
| Fencing tokens | Monotonic token so stale lock holders are rejected by the resource | Required for safe distributed locks |
| Split-brain | Two leaders | Quorum, fencing, STONITH |

### Time & ordering
| Concept | Detail |
|---|---|
| Physical clocks / skew | NTP error ms–100s ms; leap seconds; VM pauses |
| Lamport clock | Counter giving happened-before partial→total order; no concurrency detection |
| Vector clock | Per-node counters detect concurrent writes (Dynamo/Riak); size grows |
| Hybrid logical clock | Physical + logical counter (CockroachDB) |
| TrueTime | GPS/atomic clocks with uncertainty interval; commit-wait (Spanner) |
| Causality | A→B ordering preserved (causal consistency) |

### Agreement & coordination
| Concept | Notes |
|---|---|
| Consensus | Agreement on value despite crashes; FLP: impossible in async model with 1 fault without timing assumptions |
| Paxos (1989/98) | Correct, hard to implement/understand; Multi-Paxos |
| Raft (2014) | Understandable: leader election, log replication, safety; etcd, Consul, CockroachDB |
| ZAB | ZooKeeper atomic broadcast |
| BFT (PBFT, blockchain) | Tolerates malicious nodes; 3f+1 nodes; costly |
| Leader election | Via consensus/lease (ZooKeeper, etcd) |
| Coordination services | ZooKeeper, etcd, Consul: config, locks, membership |
| Distributed locking | Redlock debated; prefer consensus + fencing; avoid when possible |
| Membership & gossip | SWIM/epidemic dissemination; eventual convergence, scalable |
| Quorums | Majority `N/2+1`; Dynamo `R+W>N` |
| Consistent hashing | Partition placement |
| Service discovery | Registry/DNS/mesh |
| Distributed scheduling | Borg/K8s scheduler, bin packing, preemption |
| Distributed GC | Reference counting/leases for remote objects |
| Distributed FS | GFS/HDFS (master + chunks, replication 3×), Ceph, Lustre |
| Geo-replication / multi-region | Latency vs consistency; active-active conflicts; data residency |

### Delivery semantics
| Semantic | How | Cost |
|---|---|---|
| At-most-once | Send, no retry | Loss possible |
| At-least-once | Retry until ack | Duplicates → idempotent consumers/dedup |
| Exactly-once | At-least-once + dedup/transactional commit (Kafka EOS, within a system boundary) | Not truly achievable end-to-end without idempotent side effects |

### What not to assume
The fallacies (Part 2) + clocks are synchronized, a timeout means failure, a message is processed once, order is preserved across partitions, a lock holder is still alive, your replica is up-to-date, and one failure at a time.

## 6.2 Messaging & streaming
**Evolution:** direct request-response → message passing/IPC → message queues (IBM MQ, MSMQ, 80s–90s) → JMS/AMQP brokers (RabbitMQ, ActiveMQ) → distributed log (Kafka 2011, Pulsar, Kinesis) → stream processing (Storm → Spark Streaming → Flink, Kafka Streams) → unified batch/stream, streaming databases.

| Concept | Definition | Notes |
|---|---|---|
| Request-response | Sync call | Temporal coupling |
| Message queue / work queue | Point-to-point; message removed on ack | Competing consumers |
| Pub/sub, topics, exchanges | Fan-out to subscribers; AMQP direct/topic/fanout/headers | Subscriber lag mgmt |
| Broker | Routes/stores messages | SPOF unless clustered |
| Log / event stream | Append-only, retained, replayable; consumers track offsets | Kafka: partitions give parallelism + per-partition order |
| Consumer group | Partitions divided among members; rebalancing | Max parallelism = partitions |
| Offset | Consumer position | Commit after processing → at-least-once |
| Ordering | Guaranteed within partition/key only | Choose partition key carefully |
| DLQ / retry queue / delayed queue / priority queue | Handle failures, backoff, scheduling, urgency | DLQ needs monitoring/redrive |
| Poison message | Always fails | Max-retry → DLQ |
| Backpressure/flow control | Prefetch limits, bounded buffers, pull model | Pull (Kafka) vs push (RabbitMQ) |
| Event notification | Thin event, consumer fetches data | Coupling via callback |
| Event-carried state transfer | Event includes data | Fewer calls; data duplication |
| Event sourcing | State = fold of events | See Part 3 |
| Idempotent consumer | Dedup via id / upsert | Needed with at-least-once |
| Outbox/CDC pipelines | Reliable event publication | Part 4 |
| Claim check | Store payload in blob, send reference | Large messages |

### Stream processing
| Concept | Explanation |
|---|---|
| Event time vs processing time | Order by when it happened vs arrived |
| Windows | Tumbling (fixed, non-overlapping), sliding/hopping (overlap), session (gap-based) |
| Watermarks | "No events older than T expected" → close windows; trade completeness vs latency |
| Late / out-of-order events | Allowed lateness, side outputs, retractions |
| Stateful processing | Local state (RocksDB) + checkpoints (Flink) |
| Stream joins | Stream-stream (windowed), stream-table, enrichment |
| Exactly-once semantics | Checkpoint + transactional sinks |
| Micro-batching | Spark Streaming: simpler, higher latency |
| Lambda architecture | Batch layer + speed layer + serving; duplicate logic |
| Kappa architecture | Single stream path with replay; needs long retention |
| ETL vs ELT | Transform before load (limited warehouses) vs after (cheap scalable warehouses) |
| Data pipelines / orchestration | Airflow, Dagster; DAGs, retries, backfills |
| Durable execution | Temporal/Step Functions/Cadence: workflow state persisted, resume after crash |

### ★ Queue vs log vs stream vs event bus
| | Queue (RabbitMQ/SQS) | Log (Kafka) | Stream processor | Event bus |
|---|---|---|---|---|
| Retention | Until consumed | Time/size based | Derived | Varies |
| Replay | No | Yes | Via log | Depends |
| Consumption | Competing | Consumer groups, independent | Operators | Pub/sub |
| Ordering | Per queue (mostly) | Per partition | Per key | Weak |
| Best for | Task distribution | Event history, high throughput | Computation | Routing/integration |

Event bus = logical pattern; broker = implementation (a broker can implement a bus).

### ★ Template deep dive: Kafka-style log
1. Definition: partitioned replicated commit log. 2. Context: LinkedIn 2011, many consumers of activity data. 3. Earlier: MQ brokers w/ delete-on-ack. 4. Problem: throughput, replay, multiple independent consumers. 5. How: producers append to partition leader; ISR replication; consumers pull by offset; sequential disk + page cache + zero-copy. 6. Components: broker, topic, partition, leader/ISR, controller (KRaft), producer, consumer group, offsets, compaction. 7. Diagram:
```
Producers -> [Topic: P0 P1 P2 (leader+replicas)] -> Consumer Group A (c1,c2,c3)
                                                 -> Consumer Group B
```
8. Use: activity tracking, CDC, event sourcing, ETL. 9. Pros: throughput, replay, decoupling. 10. Cons: ordering only per partition, rebalances, partition count hard to change, ops heavy, no per-message ack/priority. 11. Failures: consumer lag, unclean leader election data loss, rebalance storms, hot partitions. 12. Perf: MB/s–GB/s per broker; scale by partitions. 13. Consistency: acks=all + min.insync.replicas for durability. 14. Security: ACLs, TLS, SASL. 15. Ops: capacity, retention, lag monitoring. 16. Cost: disk, replication ×3, network. 17. Next: managed streaming, tiered storage, streaming DBs. 18. Still dominant. 19. Alternatives: Pulsar, Kinesis, Pub/Sub, RabbitMQ, NATS. 20. Interview: design notification pipeline, exactly-once. 21. Example: orders topic keyed by orderId. 22. Summary: high throughput + replay vs operational complexity.

