# Part 4 – Databases, Transactions, Consistency, Recovery

## 4.1 Evolution of data storage
```
Flat files -> Hierarchical (IMS, 1960s) -> Network/CODASYL -> Relational (Codd 1970, SQL, System R/Oracle)
 -> OO databases (90s, niche) -> ORMs
 -> Web scale: Key-value (Dynamo), Wide-column (Bigtable/Cassandra), Document (Mongo), Graph (Neo4j)
 -> Search (Lucene/Elasticsearch), Time-series, In-memory (Redis)
 -> Analytics: Data warehouse -> Data lake -> Lakehouse
 -> NewSQL / Distributed SQL (Spanner, CockroachDB), HTAP
 -> Vector DBs (embeddings, AI)
```
| Type | Pressure that created it | Strengths | Weaknesses | Next/alternative |
|---|---|---|---|---|
| Flat files | Simplest persistence | Trivial | No concurrency, queries, integrity | DBMS |
| Hierarchical | Navigational access for mainframes | Fast for known paths | Rigid, many-to-many awkward | Network → relational |
| Network (CODASYL) | Many-to-many | Flexible links | Programmer navigates pointers; schema change pain | Relational (declarative) |
| ★ Relational | Data independence, declarative SQL | ACID, joins, ad-hoc queries, mature | Vertical scaling limits, impedance mismatch, rigid schema | NoSQL (scale), ORMs |
| Object-oriented | Impedance mismatch | Natural object storage | No standard, poor tooling | ORM + relational; document DBs |
| Key-value | Massive scale, simple access (Dynamo 2007) | Horizontal scale, low latency | No rich queries/joins | Document, wide-column |
| Document | Flexible schema, nested data | Schema agility, locality | Weak joins, duplicated data | Relational JSON support |
| Wide-column | Huge write throughput (Bigtable 2006) | Linear scale, LSM | Query-model design upfront, limited transactions | Distributed SQL |
| Graph | Relationship-heavy queries | Multi-hop traversals | Hard to shard | Relational recursive CTE for small cases |
| Time-series | Metrics/IoT | Compression, downsampling, retention | Narrow use | Columnar OLAP |
| Search index | Full-text/relevance | Inverted index, faceting | Not source of truth, eventual | Vector search hybrid |
| In-memory | Cheap RAM | µs latency | Cost, durability (AOF/RDB) | Tiered/persistent memory |
| NewSQL/Distributed SQL | Want SQL+ACID at scale (Spanner 2012) | Global consistency, SQL | Commit latency (consensus), cost, clock dependence | Cell-based sharded SQL |
| Vector DB | Semantic search for embeddings | ANN (HNSW, IVF, PQ) similarity | Approximate recall, memory heavy, filtering complexity | pgvector, hybrid search |
| Data warehouse | OLAP separate from OLTP | Columnar, fast aggregates | Schema-on-write, cost, ETL | Data lake |
| Data lake | Cheap raw storage of all data | Flexibility, any format | "Data swamp", no ACID | Lakehouse (Delta/Iceberg/Hudi) |
| Lakehouse | Warehouse features on lake | ACID tables on object storage | Engine maturity, small-file issues | — |
| HTAP | Analytics on live data | Fresh analytics | Resource contention | CDC → separate OLAP |

**OLTP vs OLAP:** OLTP = many small row-oriented transactions; OLAP = few large scans, columnar, aggregates.

## 4.2 Modeling
- **ER modeling:** entities, relationships, cardinality.
- **Normalization (1NF–BCNF):** removes redundancy/update anomalies. **Denormalization:** duplicate for read speed, at the cost of write complexity/inconsistency.
- **Keys:** primary (identity; choose monotonic vs random: UUIDv4 fragments B-tree, UUIDv7/ULID/snowflake are time-ordered), foreign (integrity; costly across shards).
- **Stored procedures:** logic near data (fast, hard to version/test). **Materialized views:** precomputed query results (refresh cost/staleness).
- **Schema evolution & migrations:** expand-and-contract; backward/forward compatibility; online DDL tools.
- **Polyglot persistence:** right store per workload; cost is operational variety and cross-store consistency (use CDC/outbox).

## 4.3 Storage engines & indexes
| Structure | Idea | Reads | Writes | Used by |
|---|---|---|---|---|
| B-tree / B+ tree | Balanced page tree, leaves linked | O(log n), range scans excellent | In-place updates, random I/O, page splits | Postgres, MySQL InnoDB, Oracle |
| Hash index | Hash→location | O(1) point lookups | Resizing | Memory DBs, Bitcask |
| LSM tree | Memtable → immutable SSTables → compaction | Read amplification (bloom filters, indexes mitigate) | Sequential, very high throughput; write amplification via compaction | Cassandra, RocksDB, LevelDB, HBase |
| Bloom filter | Probabilistic set membership (no false negatives) | Skip SSTables | — | LSM, caches, penetration defence |
| Column store | Store by column | Compress + scan fast | Slow single-row writes | Redshift, ClickHouse, Parquet |
| Inverted index | Term→postings | Full-text | Segment merges | Lucene/Elasticsearch |
| Secondary index | Extra access path | Faster queries | Write cost; global vs local in distributed DBs | all |

## 4.4 Transactions & concurrency
- **ACID:** Atomicity, Consistency (app invariants), Isolation, Durability. **BASE:** Basically Available, Soft state, Eventually consistent — reaction to scale/availability.
- **Isolation levels:** Read Uncommitted → Read Committed → Repeatable Read → Snapshot → Serializable. Anomalies: dirty read, non-repeatable read, phantom, lost update, write skew.
- **Serializability** (transactions behave as serial order) vs **linearizability** (single-object real-time recency) — different guarantees; *strict serializability* = both.
- **2PL (pessimistic):** shared/exclusive locks until commit; deadlocks → detection/timeouts. **OCC (optimistic):** validate at commit; retries under contention. **MVCC:** versions per row, readers don't block writers (Postgres, Oracle, InnoDB). **SSI:** serializable snapshot isolation.
- **Deadlocks:** lock ordering, short txns, timeouts.

## 4.5 Durability & recovery
| Concept | Role |
|---|---|
| WAL | Log change before page write; sequential; basis of recovery and replication |
| Redo/Undo log | Redo re-applies committed; undo rolls back uncommitted (ARIES) |
| Checkpoint | Bound recovery time |
| Crash recovery | Analysis → redo → undo |
| Backup (full/incremental/differential) | Protection from logical corruption/human error (replication does **not** protect from `DROP TABLE`) |
| PITR | Base backup + WAL archive → restore to timestamp |
| DR | Cross-region copy + runbook; defined by RPO/RTO |
| Archival / retention / expiration | Cost + compliance (TTL, lifecycle policies, legal hold) |
| Data lineage / governance | Track origin/transforms; ownership, quality, access, classification, catalog |
| CDC | Stream DB changes from the log (Debezium) → search, cache, analytics, outbox |

## 4.6 Scaling databases
Order of escalation (cheapest first): **index/query tuning → cache → vertical scale → read replicas → partition tables → shard → distributed SQL/NoSQL.**

| Technique | Detail | Trade-off |
|---|---|---|
| Replication: leader-follower | Writes to leader, async/sync to followers | Replica lag, failover data loss (async), read-your-writes issues |
| Multi-leader | Writes in multiple regions | Conflict resolution (LWW, CRDTs, app merge) |
| Leaderless (Dynamo) | Quorum R+W>N, read repair, hinted handoff, anti-entropy | Tunable consistency, sloppy quorums, conflicting versions |
| Sync vs async replication | Durability vs latency/availability | Semi-sync compromise |
| Read replicas / write replicas | Read scale; write scale needs sharding | Staleness |
| Partitioning | Splitting data (vertical/horizontal) | — |
| Sharding | Horizontal partition across nodes | Cross-shard joins/txns, resharding, hot shards |
| Range partitioning | Ordered keys; range scans | Hot spots on monotonic keys |
| Hash partitioning | Uniform spread | Range scans lost |
| Consistent hashing | Add/remove nodes moves ~1/N keys; virtual nodes | Needs vnode tuning; hot keys remain |
| Hot partition | One key/shard overloaded | Key salting, split, cache, adaptive partitioning |
| Rebalancing | Move partitions on growth | Bandwidth, throttling, avoid stampede |

## 4.7 Consistency models & theory
- **CAP:** during a **P**artition choose **C** (reject) or **A** (serve possibly stale). It's not "pick 2 of 3 always".
- **PACELC:** if Partition → A vs C; Else → Latency vs Consistency (Dynamo: PA/EL; Spanner: PC/EC).
- **Spectrum (strong→weak):** strict serializable → linearizable → sequential → causal → read-your-writes / monotonic reads / monotonic writes / session → eventual.
- **Consensus:** Paxos/Raft/ZAB to agree on a log → linearizable replicated state machines.
- **Distributed transactions:** 2PC (blocking on coordinator failure), 3PC (non-blocking in theory, impractical), Saga (compensations, eventual), TCC (try-confirm-cancel), Percolator/Spanner (timestamps + consensus).
- **Outbox:** write event in same local txn as state; relay publishes (solves dual-write). **Inbox:** consumer stores processed message ids (dedup). **Idempotency:** same request repeated has same effect (idempotency keys, upserts, version checks).

## 4.8 ★ Which database for which workload
| Workload | Choice | Why | Watch out |
|---|---|---|---|
| Transactions, relations, reporting | PostgreSQL/MySQL | ACID, SQL, mature | Scale ceiling → replicas/shard |
| Global strongly consistent OLTP | Spanner / CockroachDB / YugabyteDB | SQL + horizontal | Latency, cost |
| Massive writes, known access paths | Cassandra / DynamoDB / ScyllaDB | LSM, linear scale | Model queries up front |
| Session/cache/leaderboards/rate limit | Redis/Memcached | µs latency, data structures | Memory cost, durability |
| Flexible documents, content | MongoDB/Couchbase/DocumentDB | Schema agility | Cross-doc txn, duplication |
| Relationship traversal | Neo4j / Neptune | Multi-hop | Sharding |
| Metrics/IoT | InfluxDB/TimescaleDB/Prometheus | Compression, retention | Cardinality |
| Full-text/log search | Elasticsearch/OpenSearch/Solr | Inverted index | Not primary store |
| Analytics | BigQuery/Snowflake/Redshift/ClickHouse | Columnar | Not for OLTP |
| Raw/ML data | S3 + Iceberg/Delta | Cheap, open | Governance |
| Semantic/RAG | pgvector, Pinecone, Milvus, Weaviate | ANN | Recall vs latency, re-embedding |
| Blobs | Object storage (S3/GCS) | Durable, cheap | High per-request latency |

