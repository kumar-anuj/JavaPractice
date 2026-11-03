# Part 7 – Reliability, Availability, Security, Observability

## 7.1 Reliability & availability
| Term | Meaning |
|---|---|
| Reliability | Correct operation over time (MTBF) |
| Availability | Fraction of time usable = `MTBF / (MTBF + MTTR)` |
| Durability | Data not lost (S3: 11 nines) — independent of availability |
| Resilience | Ability to recover/adapt from failure |
| Fault tolerance | Continue despite faults (fault ≠ failure) |
| Maintainability / recoverability | Ease of repair; speed of restoring |
| Redundancy | Extra components (N+1, N+2, 2N) |
| Failover | Switch to standby; detection time + data loss risk |
| Active-active | All serve; capacity & conflict complexity |
| Active-passive | Standby idle; hot (ready), warm (scaled down), cold (provision on demand) |
| Fault domains / AZ / region | Rack → AZ → region blast-radius boundaries |
| RTO / RPO | Max downtime / max data loss tolerated |
| SLI / SLO / SLA | Measurement / internal target / contractual with penalties |
| Error budget | `1 − SLO`; spend on releases; freeze when exhausted |
| Chaos engineering / game days / failure injection | Prove resilience (Chaos Monkey, Gremlin, FIS) |
| DR testing / backup validation | An untested backup is not a backup |
| Business continuity | Org-level plan beyond tech |
| Human error / operational risk | Leading cause of outages → guardrails, reviews, staged rollout, blast-radius limits |
| Data corruption | Checksums, scrubbing, immutable backups, versioning |

### Numbers
| Availability | Downtime / year | / month |
|---|---|---|
| 99% | 3.65 d | 7.2 h |
| 99.9% | 8.76 h | 43.8 m |
| 99.99% | 52.6 m | 4.38 m |
| 99.999% | 5.26 m | 26 s |

- Serial dependencies multiply: `0.999 × 0.999 × 0.999 ≈ 99.7%`.
- Parallel redundancy: `1 − (1−a)^n` (two 99% replicas → 99.99%, if independent and failover perfect).
- Correlated failures (same AZ, same bug, same config push) break independence assumptions.
- DR tiers: backup/restore (RTO hours, RPO hours) → pilot light → warm standby → multi-site active-active (RTO≈0).

## 7.2 Security
| Concept | Risk mitigated | Trade-off |
|---|---|---|
| Threat modeling (STRIDE), attack surface | Unknown risks | Time |
| CIA triad | Frames goals | — |
| AuthN / AuthZ / Accounting | Identity / permissions / audit | Friction |
| RBAC / ABAC / ReBAC (Zanzibar) | Access policy scales | Role explosion / policy complexity |
| Least privilege, defense in depth, zero trust | Breach blast radius | Operational overhead |
| Secrets/key management (Vault, KMS, HSM), rotation | Leaked credentials | Availability dependency on KMS |
| Encryption at rest/in transit/in use (confidential computing, homomorphic) | Disk theft, sniffing, memory inspection | CPU, key mgmt |
| Symmetric/asymmetric, hashing (bcrypt/argon2 for passwords, salted), signatures, certificates, PKI, TLS | Confidentiality/integrity/identity | Expiry/rotation outages |
| OAuth 2.0 (delegated authorization), OIDC (authentication layer), JWT (stateless signed token) | Password sharing, SSO | JWT revocation difficulty, token theft; use short TTL + refresh |
| Sessions/cookies (HttpOnly, Secure, SameSite) | State over stateless HTTP | Session store scaling |
| CSRF / CORS / XSS | Browser attacks | CSP, tokens, output encoding |
| SQL injection | Query tampering | Parameterized queries |
| SSRF / RCE | Internal access / code exec | Egress filtering, input validation, sandboxing, metadata-service hardening |
| DDoS | Availability attack | CDN/anycast/scrubbing, rate limits; cost |
| WAF, API security (authN, schema validation, quotas, BOLA checks) | App-layer attacks | False positives |
| Segmentation, mTLS, service identity (SPIFFE) | Lateral movement | Cert lifecycle |
| Supply chain (SBOM, signing, pinned deps, SLSA), dependency & container security (minimal images, scanning, non-root), runtime security (seccomp, Falco) | Compromised components | Build friction |
| Audit logging, compliance (SOC2, ISO27001, PCI, HIPAA, GDPR) | Accountability | Cost, retention vs privacy tension |
| Privacy: PII, masking, tokenization, residency, retention, secure deletion (crypto-shredding) | Exposure, legal | Complexity in backups/event logs |

## 7.3 Observability & operations
**Monitoring** = known questions answered with predefined metrics/alerts ("is CPU > 80%?"). **Observability** = ability to ask new questions about unknown failure from telemetry (logs + metrics + traces + profiles with high-cardinality context). **Logging** = event detail; monitoring = watch known signals.

| Concept | Notes |
|---|---|
| Logging | Structured (JSON), levels, centralized (ELK/Loki), no PII/secrets |
| Metrics | Counter, gauge, histogram, summary; percentiles (p50/p95/p99) — never average latency; RED (rate/errors/duration) & USE (utilization/saturation/errors), Four Golden Signals |
| Tracing | Spans, trace/correlation IDs, context propagation (W3C traceparent), OpenTelemetry standard; sampling (head/tail) |
| Profiling | CPU/heap/continuous profiling (pprof, async-profiler, JFR) |
| Alerting | Alert on symptoms/SLO burn rate, not causes; reduce noise |
| Dashboards | Per-service + business KPIs |
| Health checks | Liveness (restart me), readiness (send traffic), startup |
| Synthetic / RUM | Scripted probes vs real-user measurements |
| High-cardinality / cost | Label explosion kills TSDBs; sample, aggregate, retention tiers |
| Telemetry privacy | Scrub PII |
| Incident response | Detect → triage → mitigate → communicate → resolve → postmortem |
| On-call, runbooks, blameless postmortems, SRE | Sustainable ops, learning culture |
| Deployment tracking / auditability | Correlate changes with incidents (most outages follow change) |
| Debugging distributed systems | Trace → logs by ID → metrics → hypothesis; reproduce with replay |

