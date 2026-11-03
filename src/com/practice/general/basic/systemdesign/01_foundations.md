# Part 1 – Computing Foundations (A) and Single-Machine Software (B)

## 1.1 Data representation
| Concept | Definition | Pressure / why it matters | Design impact |
|---|---|---|---|
| Bits & bytes | Bit = 0/1; byte = 8 bits, smallest addressable unit | Hardware is binary; need uniform units | Capacity math (KB/MB/GB, 1024 vs 1000), endianness in wire formats |
| Encoding | Mapping text/data to bytes (ASCII→Unicode/UTF-8) | Multi-language systems broke on 7-bit ASCII | Always declare charset; UTF-8 default; byte length ≠ char length |
| Serialization | Object → bytes (JSON, XML, Protobuf, Avro, MessagePack, Java serialization) | Need to store/send structures | JSON: readable, big, slow; Protobuf/Avro: compact, schema-evolution rules; Java native serialization: insecure/fragile |
| Compression | Reduce size (gzip, zstd, snappy, lz4) | Bandwidth/storage cost | CPU↔size trade-off; snappy/lz4 for speed, zstd/gzip for ratio; compressed data resists range-reads |
| Hashing | Fixed-size digest (MD5/SHA-2, murmur, xxHash) | Fast lookup, integrity, partitioning | Hash tables, sharding, dedup; crypto vs non-crypto hashes differ; collisions |
| Checksums | CRC/Adler to detect corruption | Disks/networks flip bits | Used in TCP, filesystems (ZFS), storage engines, Kafka records |
| Encryption basics | Symmetric (AES) vs asymmetric (RSA/ECC), key exchange | Confidentiality over shared networks | Key management is the real problem; CPU cost (AES-NI) small |

## 1.2 CPU, memory, storage hierarchy
```
Registers (~0.3 ns) -> L1 (~1 ns) -> L2 (~4 ns) -> L3 (~15 ns) -> RAM (~100 ns)
 -> NVMe SSD (~100 us) -> SATA SSD (~200 us) -> HDD (~5-10 ms)
 -> Same-DC network RTT (~0.5 ms) -> Cross-region RTT (~50-150 ms)
```
| Concept | Definition | Design consequence |
|---|---|---|
| CPU & instruction cycle | Fetch→decode→execute→write-back, pipelined, superscalar | Branch mispredictions & cache misses dominate; clock speed plateaued ~2005 → multicore → concurrency became mandatory |
| Registers | Fastest storage inside CPU | Compilers manage; atomics operate here/cache lines |
| CPU caches | L1/L2/L3 SRAM, cache lines (64 B) | Locality matters; false sharing between threads; data-oriented layouts |
| Locality | Temporal & spatial locality | Basis of every cache in the stack (CPU, OS, DB buffer pool, CDN) |
| RAM | Volatile, byte-addressable | 100–1000× faster than disk; in-memory DBs (Redis) are a reaction to cheap RAM |
| Virtual memory | Per-process address space, paging, TLB | Isolation; swapping kills latency; mmap-based DBs; OOM killer |
| NUMA | Memory attached to specific CPU sockets | Remote memory access slower; pin threads/memory for DBs and JVMs |
| HDD | Spinning platters; seek ~5–10 ms; ~150 MB/s sequential | Random I/O is ~100× slower than sequential → **B-trees w/ big pages, WAL (sequential), LSM trees** |
| SSD/NVMe | Flash; no seek; write amplification, erase blocks, wear | Random reads cheap; random small writes cause WA; LSM tuned for SSD; TRIM |
| Sequential vs random | Access pattern | Append-only logs (Kafka, WAL) exploit sequential speed |
| I/O, interrupts, DMA | Devices signal CPU via interrupts; DMA moves data w/o CPU | Zero-copy (sendfile), io_uring, epoll; kernel bypass (DPDK) for extreme throughput |
| Buses | PCIe, memory bus | Bandwidth ceilings for NICs/GPUs/NVMe |
| Time & clocks | Wall clock (NTP-synced, can jump) vs monotonic; drift | Never measure durations with wall clock; basis for clock-skew problems (Part 6) |

## 1.3 Operating systems & concurrency
**Evolution:** batch (1950s, one job at a time, CPU idle during I/O) → multiprogramming (overlap I/O) → time-sharing (1960s, interactivity; Unix) → personal computers → multicore/preemptive multitasking.

| Concept | Definition | Pressure → trade-off |
|---|---|---|
| Process | Isolated address space + resources | Isolation (fault, security) at cost of heavy creation & IPC |
| Thread | Execution unit sharing process memory | Cheaper, shared state → races, deadlocks |
| Context switch | Save/restore CPU state | Thousands of threads → switch overhead & cache thrash → **event loops / async I/O / green threads / virtual threads** |
| Concurrency vs parallelism | Concurrency = structure (interleaving); parallelism = simultaneous execution | Concurrency helps I/O-bound; parallelism helps CPU-bound |
| Scheduling | Round-robin, priority, CFS, work stealing | Fairness vs latency vs throughput; priority inversion |
| Synchronization | Mutex/lock, semaphore, monitor, condition variable, read-write lock, spinlock, barrier | Correctness vs contention; lock convoy |
| Atomic ops / CAS / memory model | Hardware-guaranteed indivisible ops; happens-before | Lock-free structures; ABA problem; memory ordering subtleties |
| Race condition | Outcome depends on timing | Needs locks, immutability, or confinement |
| Deadlock | Cyclic wait (4 Coffman conditions) | Lock ordering, timeouts, try-lock |
| Starvation / livelock | Thread never gets resource / keeps retrying | Fair locks, backoff |
| IPC | Pipes, sockets, shared memory, message queues, signals | Shared memory fastest but unsafe; sockets generalize to network → **distribution** |
| Models beyond threads | Actor model, CSP (channels), STM, async/await, reactor/proactor | *Parallel evolution* answers to "shared mutable state is hard" |

**Java connection (this repo):** `threads/` package demos — deadlock, latch/barrier, semaphore, ForkJoin, CompletableFuture, locks, wait/notify.

## 1.4 Files, filesystems, journaling, virtualization
| Concept | Definition | Pressure → trade-off |
|---|---|---|
| File/filesystem | Named byte streams; inode, directory, block allocation (ext4, NTFS, XFS, ZFS) | Persistence abstraction; fragmentation |
| Journaling / WAL | Log intent before applying (ext4, NTFS) | Crash consistency after power loss; write amplification. **Same idea reappears as DB WAL, Raft log, Kafka** |
| Copy-on-write FS | ZFS/Btrfs, snapshots | Cheap snapshots/checksums; fragmentation |
| fsync & durability | Force data to stable storage | Durability vs latency; lying disks/controllers |
| Virtualization | Hypervisor (Type 1/2) emulates hardware; VMs | Server consolidation, isolation (reaction to cost: 10% CPU utilization of bare metal) → overhead, boot time, image size → **containers** (Part 8) |

## 1.5 Single-machine software (B)
- **Single-process app:** all logic, state, storage in one process. Simple, fast, no network. Limits: one machine's CPU/RAM/disk; one crash = total outage; single user.
- **Batch processing:** run job over bounded input (payroll). High throughput, no interactivity → *interactive/time-sharing*.
- **Mainframe & time-sharing:** centralized compute, dumb terminals. Strong reliability, expensive, vendor lock-in → *PC / client-server* (reaction to cost).
- **Desktop apps:** local compute, local files. Data siloed, no sharing → *client-server* (reaction to business requirements: shared data).

### ★ Why foundations shape design (summary)
1. Disk vs RAM vs network latency ratios decide *where* data lives (cache, replicate, shard).
2. Sequential I/O superiority → logs, LSM, append-only everything.
3. Multicore → concurrency control → every shared-state bug class.
4. Clocks unreliable → logical time, fencing tokens (Part 6).
5. Crash consistency (journaling) → WAL/consensus logs across the stack.

