# Part 10 – System Designs Mapped to Concepts

Each entry: **Core requirements → Key design → Concepts used → Main risks.** Estimates are illustrative.

| # | System | Key design | Concepts | Risks |
|---|---|---|---|---|
| 1 | URL shortener | Base62 of unique id (counter/Snowflake/KGS), KV store, 301/302 redirect, cache hot links; read:write ≈100:1 | KV DB, sharding by key, cache-aside, CDN, rate limit | Key collisions, hot links, abuse |
| 2 | Pastebin | Blob in object store, metadata in DB, TTL expiry | Object storage, lifecycle policies, CDN | Abuse/malware, size limits |
| 3 | File storage (Dropbox) | Chunking (4 MB), dedup by hash, metadata DB, sync via change log, resumable upload | Content-addressed storage, CDC, delta sync, conflict resolution | Conflicts, consistency of metadata vs blobs |
| 4 | Object storage (S3) | Metadata/index layer + data nodes, erasure coding (e.g., 10+4), 11-nines durability | Replication vs EC, consistent hashing, scrubbing | Repair traffic, hot buckets |
| 5 | Distributed FS (GFS/HDFS) | Master (namespace) + chunkservers, 3× replication, large sequential I/O | WAL, leases, rack-aware placement | Master SPOF → HA/federation, small files |
| 6 | Key-value store (Dynamo) | Consistent hashing w/ vnodes, N/R/W quorums, vector clocks/LWW, hinted handoff, gossip, Merkle trees, LSM | Leaderless replication, anti-entropy | Conflicts, tuning |
| 7 | Distributed cache | Sharded Redis/Memcached, consistent hashing, replication, eviction | Cache patterns, hot keys | Stampede, failover |
| 8 | Search engine | Crawl → index (inverted, sharded) → rank (TF-IDF/BM25/PageRank/ML) → serve (scatter-gather) | Lucene segments, tiered index, caching | Freshness vs cost, tail latency |
| 9 | Web crawler | URL frontier (priority + politeness queues per host), dedup (Bloom/simhash), robots.txt, DNS cache | Queues, partitioning by host, rate limit | Traps, duplicates |
| 10 | Notification system | Event → preference/dedupe → per-channel queues (push/SMS/email) → provider → status | Pub/sub, DLQ, idempotency, rate limits, priority queues | Provider outages, duplicates, spam |
| 11 | Email system | SMTP ingest, queue, spam filter, mailbox store (per-user), IMAP/index | Queues, search index, reputation/SPF/DKIM/DMARC | Deliverability |
| 12 | Chat | WebSocket gateways, session registry (user→gateway), message log per conversation, offline store+push, ordering via per-chat sequence | Stateful connections, partition by conversation, fan-out, presence TTL | Reconnect storms, ordering, group fan-out |
| 13/14 | Social/news feed | Fan-out-on-write (precompute timelines) vs on-read; hybrid for celebrities; ranking | Caches, queues, sharding, hot keys | Celebrity problem, ranking freshness |
| 15 | Video streaming | Upload→transcode DAG (multiple bitrates/codecs)→segments (HLS/DASH)→CDN, adaptive bitrate | Object storage, queues, CDN/edge, DRM | Egress cost, popular-content spikes |
| 16 | Music streaming | Similar + licensing, offline sync, recommendations | CDN, caches, recommender | Rights/regions |
| 17 | Ride-sharing | Geo-index (geohash/S2/H3), location stream (ingest every ~4 s), matching, ETA, surge pricing, trip state machine | Streams, in-memory geo store, sagas | Hot cities, GPS noise |
| 18 | Food delivery | Orders state machine, restaurant/courier matching, ETA, payments | Sagas, event-driven, geo | Multi-party consistency |
| 19 | Payment system | Idempotency keys, double-entry ledger, state machine, reconciliation, PSP integration, async webhooks | ACID DB, outbox, audit, PCI tokenization | Double charge, unknown outcomes (timeouts) |
| 20 | Banking | Ledger with immutable entries, strong consistency, batch settlement, regulatory audit | Serializable txns, event sourcing, DR with RPO≈0 | Compliance |
| 21 | E-commerce | Catalog (search+cache), cart (KV), inventory (reservation), orders (saga), payments | Microservices, CQRS, search index | Overselling, flash sales |
| 22/23 | Ticket/hotel booking | Seat/room hold with TTL, optimistic locking/`SELECT FOR UPDATE`, waiting room under spikes | Concurrency control, queues, admission control | Double booking |
| 24 | Calendar | Events with recurrence rules (RRULE), time zones, free/busy, invites, sync (CalDAV) | Interval indexing, notifications | Time-zone/DST bugs |
| 25 | Online judge | Submission queue → sandboxed workers (containers/seccomp/cgroups) → verdict | Queues, isolation, resource limits | Malicious code |
| 26 | Metrics platform | Agents → ingest (Kafka) → TSDB (compression, downsampling) → query/alert | Streams, high cardinality, retention tiers | Cardinality explosion |
| 27 | Logging platform | Agents → buffer → index/object store → query; sampling, retention | Backpressure, hot/warm/cold tiers | Cost, PII |
| 28 | Analytics platform | Events → stream/batch → lakehouse → OLAP/BI | ETL/ELT, columnar, partitioning | Data quality |
| 29 | Recommendation | Candidate generation (ANN/CF) → ranking (ML) → re-ranking; feature store; offline+online | Vector search, caches, A/B tests | Feedback loops, cold start |
| 30 | Ad serving | Auction (<100 ms), targeting, budget pacing, click logging, attribution | Low latency, in-memory, streaming aggregation | Fraud, budget over-delivery |
| 31 | Fraud detection | Streaming features + rules + ML scoring within ms, case management | Stream processing, feature store, human review loop | False positives |
| 32 | Real-time bidding | ~100 ms end-to-end, massive QPS, fan-out to bidders, timeouts | Hedging, load shedding, in-memory | Tail latency |
| 33 | IoT platform | MQTT broker, device registry/shadow, OTA, time-series, rules engine | Edge, store-and-forward, cert-based auth | Device fleet security |
| 34 | Collaboration (Docs) | OT or CRDT, WebSocket, presence, version history | Causal ordering, snapshots+ops | Merge semantics |
| 35 | Workflow engine | Durable state machine, timers, retries, history | Durable execution, event sourcing | Determinism, versioning |
| 36 | Job scheduler | Cron/delay queue, leader election, leasing, exactly-once-ish | Leases, fencing, DB-based queues | Duplicate runs, clock skew |
| 37 | Distributed task queue | Brokers, workers, visibility timeout, retries, DLQ | Competing consumers, idempotency | Poison tasks |
| 38 | API platform | Gateway, authN/Z, quotas, billing, developer portal, versioning, SDKs | Rate limiting, observability | Breaking changes |
| 39 | Multi-tenant SaaS | Pool vs silo vs bridge; tenant id everywhere; per-tenant quotas | Isolation, noisy neighbor, cell architecture | Data leaks across tenants |
| 40 | CMS | Content store, versioning, workflow, CDN, search, templating | Caching/invalidation, RBAC | Cache purge |
| 41 | Learning platform | Content delivery, progress tracking, assessments, video | CDN, event tracking | Exam-time spikes |
| 42 | AI inference platform | Request router, batching (continuous batching), GPU pools/autoscale, model registry, KV cache, quantization, canary models, token streaming (SSE) | Queues, admission control, cost control | GPU scarcity, cold starts, tail latency |
| 43 | ML feature store | Offline (lake) + online (KV) stores, point-in-time correct joins, consistent definitions | Streaming + batch, TTL | Training/serving skew |
| 44 | Vector search | Embeddings → ANN index (HNSW/IVF-PQ) sharded, hybrid with keyword (BM25), metadata filters | Sharding, memory planning, re-indexing | Recall vs latency, embedding model changes |
| 45 | RAG system | Ingest → chunk → embed → index; query → rewrite → retrieve (hybrid) → rerank → prompt → LLM → cite/guardrails; eval loop | Vector DB, caches, ACLs per document, observability, cost | Hallucination, stale docs, prompt injection, data leakage |

### ★ Full example: URL shortener
```
Client -> CDN/LB -> API (create/redirect) -> Cache (Redis) -> KV/SQL (sharded by hash(code))
                         \-> ID service (range-allocated counters) -> Base62
                         \-> Analytics events -> Kafka -> OLAP
```
- 100 M new URLs/month ≈ 40 writes/s; 100:1 reads ≈ 4,000 reads/s avg, ×5 peak = 20 K/s.
- 7-char base62 = 62^7 ≈ 3.5 T codes. 500 B/record × 100 M × 12 × 5 yrs ≈ 3 TB.
- Cache top 20% → ~80% hit rate; redirects 302 if analytics needed, 301 if cacheable forever.

