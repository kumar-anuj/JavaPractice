# Part 14 – Deep Dive A: Foundations and Networking (with examples and diagrams)

## 14.1 Why the memory hierarchy drives every design
**Idea:** fast storage is small and expensive; big storage is slow. Systems keep hot data close.

```
 Speed/Cost  ^                         Capacity
   fastest   |  Registers      (bytes)
             |  L1/L2/L3       (KB-MB)
             |  RAM            (GB)
             |  SSD            (TB)
             |  HDD / Object   (PB)
   slowest   |  Network/Remote (unbounded)
```
**Worked example – why Redis in front of Postgres helps:** a Postgres indexed read costs ~1 ms (buffer-pool hit) up to ~10 ms (disk). A Redis read over the LAN costs ~0.3 ms. At 95% hit ratio: `0.95×0.3 + 0.05×10 = 0.785 ms` vs 10 ms uncached – a 12× gain, *and* the DB sees 20× fewer queries.

**Sequential vs random I/O example:** an HDD does ~100 random IOPS (~0.4 MB/s with 4 KB reads) but ~150 MB/s sequential. This is why Kafka appends to a log, databases write a WAL first, and LSM trees batch writes into sorted files.

```
Random write (B-tree, in place):    Sequential write (LSM / WAL):
 page 17 -> disk seek                 [rec1][rec2][rec3][rec4] -> append only
 page 902 -> disk seek                 no seeks, high throughput
```

## 14.2 Processes, threads, locks – a runnable Java example
```java
// Race condition and fix
class Counter {
    private int unsafe = 0;                       // not thread-safe
    private final java.util.concurrent.atomic.AtomicInteger safe =
        new java.util.concurrent.atomic.AtomicInteger();   // CAS-based

    void incUnsafe() { unsafe++; }                // read-modify-write: 3 steps, interleavable
    void incSafe()   { safe.incrementAndGet(); }  // atomic compare-and-swap loop
}
// Two threads each doing 1,000,000 incUnsafe() typically end < 2,000,000.
```
```
Thread A: read 5 ----------- write 6
Thread B:      read 5 -- write 6          <- lost update (should be 7)
```
**Deadlock example:** T1 locks A then B; T2 locks B then A.
```
T1: lock(A) ---- wait(B)
T2: lock(B) ---- wait(A)      => cycle => deadlock
Fix: always acquire locks in global order (A then B), or use tryLock(timeout).
```
**Context-switch cost → design:** 10,000 threads blocked on I/O waste memory (~1 MB stack each) and CPU switching. Response: event loops (Netty/Node), async I/O (epoll), Java virtual threads, or actors.

## 14.3 Filesystem journaling example
```
Without journal: write data block -> CRASH -> metadata not updated -> corrupted file
With journal:    1. write "intent" to journal  2. commit marker
                 3. apply to real location       4. clear journal
On reboot: replay committed entries, discard incomplete ones.
```
The same *log-first* idea appears in database WAL, Raft logs, and Kafka.

## 14.4 Serialization comparison (same record `{id:1, name:"Ann"}`)
| Format | Size | Human-readable | Schema evolution | Typical use |
|---|---|---|---|---|
| JSON | ~24 B | Yes | Loose, fragile | Public APIs |
| Protobuf | ~8 B | No | Field numbers; add optional fields safely | gRPC/internal |
| Avro | ~7 B | No | Writer/reader schema resolution | Kafka/data lakes |
Rule: never reuse/renumber Protobuf field numbers; add fields, never repurpose.

---
## 14.5 What happens when you type a URL (end-to-end diagram)
```
 Browser
   | 1. DNS lookup: browser cache -> OS cache -> resolver -> root -> TLD -> authoritative
   v
 IP address (GeoDNS may return nearest region / anycast IP)
   | 2. TCP 3-way handshake (SYN, SYN-ACK, ACK)  [QUIC: 1 RTT incl. TLS]
   | 3. TLS 1.3 handshake (certificate check, key exchange)
   v
 CDN edge  --hit--> return cached response
   | miss
   v
 L4 Load Balancer --> L7 Load Balancer / API Gateway (authN, rate limit, routing)
   v
 App instance --> Redis cache --miss--> Database
   v
 Response: gzip/brotli, Cache-Control, ETag
```
**Latency budget example (user in Mumbai, origin in Virginia):** DNS 20 ms + TCP 180 ms + TLS 180 ms + request 180 ms ≈ 560 ms. Put a CDN edge in Mumbai (RTT 10 ms): ≈ 40 ms. That is why CDNs, keep-alive, TLS 1.3 and HTTP/3 exist.

## 14.6 TCP essentials with examples
- **Handshake:** `SYN → SYN-ACK → ACK` (1.5 RTT before data).
- **Flow control:** receiver advertises window; if window = 0 the sender pauses (cousin of application backpressure).
- **Congestion control:** slow start doubles the window each RTT until loss, then backs off (AIMD). Short flows never leave slow start → keep connections alive and pooled.
- **Head-of-line blocking:** one lost packet stalls all later data on that TCP stream, even for unrelated HTTP/2 requests → QUIC gives independent streams.

```
HTTP/1.1 : [req1][resp1] [req2][resp2] ...             (serial per connection; 6 parallel conns)
HTTP/2   : req1,req2,req3 interleaved on 1 TCP conn     (TCP HOL on loss)
HTTP/3   : req1,req2,req3 as independent QUIC streams   (loss affects only one stream)
```

## 14.7 DNS, NAT, subnetting examples
- **DNS TTL failover:** `api.shop.com A 10.0.0.5 TTL=300`. Region dies → update record → clients may still use old IP up to 5 min. Use low TTL + health-checked routing; remember resolvers/JVM may cache longer (Java `networkaddress.cache.ttl`).
- **CIDR:** `10.0.0.0/16` = 65,536 addresses; split into `/24` subnets (256 each): `10.0.1.0/24` public, `10.0.2.0/24` app, `10.0.3.0/24` DB.
```
VPC 10.0.0.0/16
 ├─ public subnet 10.0.1.0/24  (LB, NAT gateway)  -> Internet Gateway
 ├─ private subnet 10.0.2.0/24 (app servers)      -> egress via NAT
 └─ private subnet 10.0.3.0/24 (databases)        -> no internet route
```
- **NAT:** many private hosts share one public IP via port mapping; limits ~64K ports per destination → "port exhaustion" for chatty services (use connection pooling).

## 14.8 Load balancing examples
| Algorithm | Example behaviour | Best for |
|---|---|---|
| Round robin | 1,2,3,1,2,3 | Homogeneous stateless |
| Weighted RR | 3:1 to big:small box | Mixed hardware |
| Least connections | Send to fewest active | Long/variable requests |
| Power of two choices | Pick 2 random, choose less loaded | Large fleets, avoids herding |
| Consistent hash | `hash(userId)` → same node | Caches, stateful routing |
```
L4: Client --TCP--> LB (NAT/DSR) --> Server        (fast, no HTTP awareness)
L7: Client --HTTP--> LB --parse path/headers--> /api -> svcA, /img -> svcB, retries, TLS termination
```

## 14.9 Proxies, CDN, anycast
```
Forward proxy:  Clients ──> [Proxy] ──> Internet      (hides clients, filters egress)
Reverse proxy:  Internet ──> [Proxy] ──> Servers      (hides servers, TLS, cache)
Anycast:        same IP 1.2.3.4 announced from Tokyo, Paris, NYC -> BGP routes user to nearest
```
**CDN invalidation example:** deploy `app.js` with a content hash in the name (`app.3fa9c1.js`) and `Cache-Control: max-age=31536000, immutable`; HTML references the new name. No purge needed – *versioned URLs beat invalidation.*

## 14.10 Security in the network
```
Internet -> WAF/DDoS shield -> [DMZ: LB] -> [App subnet] -> [DB subnet]
              rate limit        TLS end      SG: only LB->App:8080   SG: only App->DB:5432
```
Zero trust adds: every hop authenticated (mTLS), not just the perimeter.

