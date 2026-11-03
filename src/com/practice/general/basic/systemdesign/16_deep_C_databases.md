# Part 16 – Deep Dive C: Databases and Transactions (with examples and diagrams)

## 16.1 Indexes and storage engines
**Without an index** `SELECT * FROM orders WHERE user_id=42` scans every row (O(n)). With a B+ tree index on `user_id` it is O(log n).
```
B+ tree (fan-out ~100-500):            Root:      [ 30 | 60 ]
 3-4 levels hold billions of rows      Internal:  [10|20] [40|50] [70|80]
 leaves are linked -> range scans      Leaves: [..]->[..]->[..]->[..]   (linked list; hold row pointers/rows)
```
- **Composite index** `(user_id, created_at)` serves `WHERE user_id=? ORDER BY created_at`; leftmost-prefix rule: it does *not* help `WHERE created_at=?` alone.
- **Covering index** includes all selected columns → no table lookup.
- **Cost:** each index slows writes and uses space. Index selectivity matters (an index on a boolean is rarely useful).
- Check plans: `EXPLAIN ANALYZE SELECT ...`.

**LSM tree write path**
```
write -> WAL (durable) -> Memtable (sorted, in RAM)
          memtable full -> flush -> SSTable L0 -> compaction merges into L1, L2 ...
read  -> memtable -> bloom filter -> SSTables newest->oldest
```
Trade-off: writes sequential and fast; reads touch several files (bloom filters help); compaction causes write amplification and latency spikes.

| | B-tree | LSM |
|---|---|---|
| Writes | Random, in-place | Sequential, batched |
| Reads | 1 path | Multiple levels |
| Space amp | Low-ish | Higher until compaction |
| Fit | Read-heavy OLTP | Write-heavy ingestion |

**Bloom filter example:** m bits, k hashes. Query "key X?" → if any bit is 0 → *definitely absent* (skip disk); if all 1 → *maybe present*. 10 bits/key, 7 hashes ≈ 1% false positives.

## 16.2 Modeling example – normalization vs denormalization
Normalized:
```
users(id, name)   orders(id, user_id FK, total)   order_items(order_id, product_id, qty, price)
```
Report "orders with user name" = join. Denormalized order document in a document DB:
```json
{ "orderId": 9, "user": {"id":42,"name":"Ann"}, "items":[{"sku":"A1","qty":2,"price":10}], "total":20 }
```
Fast single read, but if Ann changes name, old orders keep the old copy (often *desired* for invoices – a snapshot). Rule: normalize for integrity, denormalize deliberately for read paths.

**Primary key choice:** auto-increment (compact, sequential – hot right-edge in sharded DBs, guessable) vs UUIDv4 (random → index fragmentation) vs UUIDv7/Snowflake (time-ordered, unique without coordination).

## 16.3 Transactions and isolation – concrete anomalies
Bank: A=100, B=100.
```
Dirty read:           T1 updates A=50 (uncommitted); T2 reads A=50; T1 rolls back -> T2 saw phantom value
Non-repeatable read:  T1 reads A=100; T2 commits A=50; T1 reads A=50
Phantom:              T1 counts rows where x>10 =5; T2 inserts a row; T1 counts again =6
Lost update:          T1 and T2 both read 100, both write back +10 -> 110 instead of 120
Write skew:           Doctors on call rule "at least 1": T1 and T2 each see 2 on call, each takes self off -> 0 on call
                      (both pass under snapshot isolation; needs Serializable or explicit locking)
```
| Isolation level | Dirty | Non-rep. | Phantom | Write skew |
|---|---|---|---|---|
| Read Uncommitted | possible | possible | possible | possible |
| Read Committed (Postgres default) | no | possible | possible | possible |
| Repeatable Read / Snapshot | no | no | mostly no* | possible |
| Serializable | no | no | no | no |
(*implementation-specific)

**Fixing lost update**
```sql
-- pessimistic: lock row
BEGIN; SELECT balance FROM acct WHERE id=1 FOR UPDATE; UPDATE acct SET balance=balance+10 WHERE id=1; COMMIT;
-- atomic update (best): UPDATE acct SET balance = balance + 10 WHERE id=1;
-- optimistic: UPDATE ... SET balance=?, version=version+1 WHERE id=1 AND version=?  (retry if 0 rows)
```
**MVCC picture**
```
row key=1: [v1 val=100 xmin=5] -> [v2 val=90 xmin=9]
Reader with snapshot @7 sees v1 ; reader @10 sees v2 ; writers don't block readers
Vacuum/GC removes versions no snapshot can see.
```
**WAL/recovery:**
```
UPDATE -> write log record (LSN 120: page 7, old=100, new=90) -> fsync log -> COMMIT ack
Page flushed later. Crash: ANALYSIS (what was active) -> REDO (replay committed) -> UNDO (roll back losers)
Checkpoint bounds how much log must be replayed.
```

## 16.4 Replication in practice
```
Leader-follower:
 App --writes--> [Leader] --WAL stream--> [Follower1]  <-- reads (may be stale)
                                    \---> [Follower2]
```
- **Replica lag problem:** user posts a comment (to leader) then refreshes (reads follower) → comment missing. Fixes: read-your-writes (route that user to leader for N seconds, or session token with LSN), monotonic reads (stick user to one replica).
- **Failover risk:** async replica promoted; last few transactions lost (RPO>0). Old leader returns → must be fenced/demoted to avoid split-brain.
- **Multi-leader:** two regions accept writes → conflict e.g. both edit title. Resolve: last-write-wins (loses data), merge via CRDT, or app-level.
- **Leaderless quorum:** N=3, W=2, R=2 ⇒ R+W>N so reads overlap latest write.
```
write x=5 -> nodes A,B ack (W=2), C missed
read -> asks A,C (R=2): A has x=5, C has old -> return newest (by version/vector clock) + read repair C
```

## 16.5 Sharding example
Users 1 B rows, 1 KB each = 1 TB; 20 K writes/s too many for one node → shard.
```
shard = hash(user_id) % N   (simple, but changing N moves almost all keys)
consistent hashing ring:    hash ring with vnodes; add node -> only ~1/N keys move

 ring:  0 ---A---B---C---A---B---C--- 2^32   (vnodes spread load; key goes to next node clockwise)
```
- Cross-shard query ("all orders over $100") = scatter-gather. Cross-shard transaction = saga/2PC. Choose shard key to keep related data together (tenant_id, user_id).
- **Hot shard example:** celebrity user → key salting (`userId#0..9`) spreads writes; reads aggregate.
- **Range sharding hotspot:** timestamp key means all current writes hit last range; prefix with hash bucket.

## 16.6 CAP & PACELC with examples
```
Partition occurs between DC1 and DC2:
 CP choice: minority side rejects writes (etcd, Spanner, ZooKeeper)  -> consistent, partly unavailable
 AP choice: both accept writes, reconcile later (Cassandra, Dynamo)  -> available, possible conflicts
```
PACELC: even with no partition, strong consistency costs latency (must wait for replicas); Dynamo-style systems choose low latency (EL), Spanner chooses consistency (EC).

## 16.7 Distributed transactions – 2PC vs Saga example (order checkout)
```
2PC:  Coordinator -> prepare -> [Orders DB][Payments DB][Inventory DB] -> all vote yes -> commit
      Locks held during the protocol; coordinator crash = participants blocked.
Saga (orchestrated):
  1 Create order (PENDING) -> 2 Reserve stock -> 3 Charge card -> 4 Confirm order
  failure at 3: compensate: Release stock -> Cancel order
```
Compensations must be *idempotent* and business-meaningful (refund ≠ undo). Use the **outbox** so state change and event are atomic:
```
BEGIN; INSERT orders ...; INSERT outbox(event=OrderPlaced); COMMIT;
Relay/Debezium reads outbox -> publishes to Kafka (at-least-once) -> consumers dedupe via inbox table.
```

## 16.8 Backup / DR example
```
Sunday: full backup  | Mon-Sat: incremental | Continuous: WAL archive -> PITR to any second
Replica in another region (DR). Quarterly: restore into scratch env, run checksums, time it (= real RTO).
```
`DROP TABLE users;` replicates instantly to all replicas – only backups/PITR save you.

## 16.9 Choosing a store – worked scenarios
| Scenario | Pick | Reason |
|---|---|---|
| Banking ledger | PostgreSQL / Spanner | Serializable, audit |
| Shopping cart | DynamoDB/Redis | Key access, TTL, high availability |
| Product search | Elasticsearch fed by CDC | Full-text + facets |
| IoT metrics 1 M points/s | TimescaleDB/ClickHouse/Kafka+TSDB | Append + compression |
| Social graph "friends of friends" | Graph DB / adjacency in KV | Traversals |
| Semantic doc search | pgvector → dedicated vector DB at scale | ANN |

