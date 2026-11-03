# Part 2 – Networking

Pressure: once software left a single machine, **every call became slow, lossy, reorderable and attackable.** Networking concepts define what distributed systems can assume.

## 2.1 Foundations
| Concept | Definition | Architectural impact |
|---|---|---|
| Topologies | Bus, star, ring, mesh, tree | Star (switched) dominates LAN; mesh in data-center fabrics (Clos/leaf-spine) |
| LAN / WAN | Local vs wide-area | WAN: latency, loss, cost → CDNs, geo-replication |
| Circuit switching | Dedicated path (telephony) | Predictable but wasteful → **packet switching** |
| Packet switching | Data split into independently routed packets (ARPANET 1969) | Resilient, shared; reordering, loss, jitter → transport protocols |
| OSI (7) / TCP-IP (4) | L1 physical, L2 link, L3 network, L4 transport, L5-7 app | "L4 vs L7" load balancing; debugging by layer |
| Ethernet / switching | Frames by MAC; switches learn MAC tables | Broadcast domains, VLANs |
| ARP | IP→MAC resolution | ARP spoofing; L2 limits |
| IPv4 / IPv6 | 32-bit vs 128-bit addresses | IPv4 exhaustion → NAT, CGNAT → IPv6 (reaction to scale) |
| Subnetting/CIDR | Address partitioning | VPC design, security zones, avoid overlapping CIDRs when peering |
| Routing / BGP | Interior (OSPF) & inter-domain (BGP) path selection | Internet outages from BGP leaks; Anycast relies on it |
| DHCP | Dynamic IP assignment | Ephemeral IPs → service discovery needed |
| NAT | Many private IPs → one public | Breaks end-to-end, hurts P2P (STUN/TURN), port exhaustion |
| DNS | Hierarchical name→IP; TTL caching | Cheapest global load-balancing/failover; TTL staleness; DNS is a classic SPOF/outage |

## 2.2 Transport
| Protocol | Properties | Trade-off | Successor/alternative |
|---|---|---|---|
| TCP | Reliable, ordered, connection-oriented, 3-way handshake, flow control (window), congestion control (slow start, CUBIC, BBR) | Handshake RTT, head-of-line blocking, slow start hurts short flows | QUIC (partial successor) |
| UDP | Unreliable datagrams | No ordering/retransmit; app must handle | Used for DNS, VoIP, games, QUIC |
| QUIC | UDP-based, TLS 1.3 built-in, multiplexed streams, 0/1-RTT, connection migration | User-space CPU cost, middlebox blocking | Basis of HTTP/3 |
| Sockets/ports | OS endpoint abstraction (IP:port) | Ephemeral port exhaustion, TIME_WAIT | Connection pooling |
| Flow control | Receiver limits sender (window) | Protects receiver | App-level **backpressure** mirrors it |
| Congestion control | Sender infers network capacity | Fairness vs throughput | BBR for lossy/long links |
| Keep-alive / pooling | Reuse connections | Saves handshakes; stale connections, LB idle timeouts | HTTP/2 multiplexing |
| Timeouts / retries | Bound wait; reattempt | Retry storms, duplicates → idempotency, backoff+jitter (Part 6) |

## 2.3 Application protocols
| Version | Key change | Problem solved | New problem |
|---|---|---|---|
| HTTP/1.0 | One request per connection | — | Handshake per request |
| HTTP/1.1 | Persistent connections, pipelining, Host header | Cost of connections | App-level head-of-line blocking; 6 conns/host hacks (sharding, sprites) |
| HTTP/2 | Binary, multiplexed streams, HPACK, server push | HOL at HTTP layer | TCP-level HOL on loss; push rarely useful |
| HTTP/3 | HTTP over QUIC | TCP HOL, faster handshake, mobility | UDP blocking, CPU cost, observability tooling |
| SSL → TLS | SSL 2/3 (1990s, broken) → TLS 1.0–1.3 | Confidentiality, integrity, server auth | Cert management/expiry, handshake latency (TLS 1.3 1-RTT, resumption) |

## 2.4 Intermediaries
| Component | What | Notes |
|---|---|---|
| Forward proxy | Client-side egress proxy | Filtering, caching, anonymity |
| Reverse proxy | Server-side front (Nginx, Envoy, HAProxy) | TLS termination, caching, compression, routing |
| L4 LB | Balances TCP/UDP by IP:port; very fast; connection-level | No content awareness; sticky by hash |
| L7 LB | Understands HTTP; routing by path/header; retries; auth | More CPU; can become bottleneck |
| LB algorithms | Round-robin, weighted, least-connections, least-latency, power-of-two-choices, consistent hash, IP hash | Stateful/sticky vs stateless |
| CDN | Geo-distributed caches near users | Latency/bandwidth/DDoS absorption; invalidation, cost |
| Anycast | Same IP announced from many sites via BGP | Nearest-site routing, DDoS dilution; flaps break TCP flows |
| Firewall / WAF | Packet filter / L7 attack filter | Perimeter defence; false positives; not enough alone (zero trust) |
| VPN | Encrypted tunnel (IPsec, WireGuard) | Remote access, site-to-site; perimeter trust model weaknesses |
| Service discovery | DNS, registry (Consul, etcd, Eureka), K8s Services | Needed because IPs are ephemeral |

## 2.5 Network realities
| Concept | Meaning | Design response |
|---|---|---|
| Latency | Delay (propagation ≥ distance/ ~200,000 km/s in fiber) | Geo placement, caching, fewer round trips, batching |
| Bandwidth | Throughput capacity | Compression, CDN, bulk transfer paths; bandwidth-delay product |
| Jitter | Latency variance | Buffers (media), tail-latency mitigation (hedging) |
| Packet loss | Drops | Retransmit, FEC, idempotent ops |
| Head-of-line blocking | One stalled item blocks others behind it | HTTP/2 → QUIC; per-partition queues |
| Partition | Segment unreachable | CAP decisions (Part 4/6) |
| Backpressure | Slow consumer signals producer | Bounded queues, reactive streams, credits |
| Network security | Segmentation, ACLs, TLS/mTLS, DDoS protection, zero trust | See Part 7 |

## 2.6 Fallacies of distributed computing (Deutsch/Gosling)
The network is reliable · latency is zero · bandwidth is infinite · the network is secure · topology doesn't change · there is one administrator · transport cost is zero · the network is homogeneous.

### ★ Example – What happens on `GET https://shop.com/`
```
Client -> DNS (cache -> resolver -> authoritative; maybe GeoDNS)
       -> TCP/QUIC handshake -> TLS handshake (SNI)
       -> Anycast edge / CDN (cache hit? return)
       -> L4 LB -> L7 LB / API gateway (auth, rate limit)
       -> Service -> cache -> DB
       <- response (compressed, cacheable headers)
```
Each hop adds latency, a failure mode, a timeout, and a security boundary.

