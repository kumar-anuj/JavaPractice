# Part 18 – Deep Dive E: Reliability, Security, Observability, Deployment

## 18.1 Availability math – worked examples
```
Serial chain: LB(99.99) -> App(99.95) -> DB(99.95)
  A = 0.9999 × 0.9995 × 0.9995 = 99.89%   (~9.6 h downtime/year)
Redundant DB pair (independent, 99.95% each): 1-(0.0005)^2 = 99.99975%
But failover isn't instant and failures correlate (same AZ/bug/config) -> real figure lower.
```
Error budget: SLO 99.9%/30 days → budget 43.2 min. Burn-rate alert: consuming 2% of budget in 1 hour (14.4× burn) pages someone; slow burn opens a ticket.

**DR strategies (cost ↑, RTO/RPO ↓)**
```
Backup & restore : RTO hours, RPO hours       cheapest
Pilot light      : core DB replicated, app off; RTO ~tens of minutes
Warm standby     : scaled-down full stack;      RTO minutes
Active-active    : multi-region both serve;     RTO ~0, RPO ~0 (hard: conflicts, cost)
```
**Multi-AZ example**
```
Region
 ├─ AZ-a: LB node, App x2, DB primary
 ├─ AZ-b: LB node, App x2, DB sync standby   (auto failover ~60 s)
 └─ AZ-c: App x2, quorum witness
Lose an AZ -> remaining capacity must carry 100% (run each AZ ≤ 66%).
```
**Chaos experiment template:** hypothesis ("checkout stays above 99.5% success if one cache node dies") → inject (kill node) → observe SLI → limit blast radius → fix gap.

## 18.2 Security – flows and examples
**OAuth 2.0 Authorization Code + PKCE (login with Google)**
```
User -> App: "Login"
App -> Browser: redirect to IdP /authorize?client_id&code_challenge&scope=openid
User authenticates at IdP, consents
IdP -> Browser -> App: redirect with ?code=abc
App -> IdP /token: code + code_verifier  -> {access_token, id_token (OIDC), refresh_token}
App -> API: Authorization: Bearer <access_token>
```
OAuth = *delegated authorization*; OIDC adds the *identity* layer (`id_token`).

**JWT anatomy & pitfalls:** `header.payload.signature` (base64url). Validate signature, `exp`, `aud`, `iss`; reject `alg:none`; keep short-lived (5–15 min); revocation requires deny-list or short TTL + refresh rotation. Don't store secrets in payload (it's only encoded).

**Password storage:** `argon2id(password, unique salt)` – slow on purpose. Never SHA-256 alone; never reversible encryption.

**SQL injection example**
```java
// vulnerable
"SELECT * FROM users WHERE name='" + input + "'"      // input: ' OR '1'='1
// safe
PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE name=?"); ps.setString(1,input);
```
**XSS:** output-encode user content, set `Content-Security-Policy`, `HttpOnly` cookies. **CSRF:** `SameSite=Lax/Strict`, anti-CSRF token. **SSRF:** block requests to `169.254.169.254` / internal ranges, use allow-lists, IMDSv2.

**mTLS between services**
```
Svc A --(client cert signed by internal CA)--> Svc B verifies cert identity (SPIFFE ID) -> authorize "A may call /orders"
Short-lived certs auto-rotated by mesh/SPIRE; expiry = outage, so monitor.
```
**Envelope encryption**
```
Data --AES-256 with DEK--> ciphertext ;  DEK --encrypted by KEK in KMS/HSM--> stored beside data
Rotate KEK cheaply (re-wrap DEKs) without re-encrypting all data.
```
**Multi-tenant isolation:** every query includes `tenant_id` (enforce via row-level security), per-tenant encryption keys, per-tenant rate limits.
**Supply chain:** pin dependency versions + lockfiles, SBOM, signed images, scan CVEs (this repo can use the CVE tooling), minimal base images, non-root containers.

## 18.3 Observability – examples
**Three pillars wired together**
```
Request -> trace_id=abc123 (header traceparent)
 logs   : {"ts":..,"level":"ERROR","trace_id":"abc123","svc":"payments","msg":"timeout","order":9}
 metrics: http_request_duration_seconds_bucket{route="/pay",status="500"}
 traces : gateway(5ms) -> orders(40ms) -> payments(900ms!) -> bank-api(880ms)   => culprit found
```
**RED dashboard:** Rate (req/s), Errors (% 5xx), Duration (p50/p95/p99). **USE:** Utilization, Saturation, Errors for resources (CPU, queue depth).
**Percentile example:** 99 requests at 100 ms and 1 at 10 s → average 199 ms hides the 10 s; p99 = 100 ms, p99.9 reveals it. Use histograms, not averages.
**Cardinality trap:** metric label `user_id` with 10 M users = 10 M time series → TSDB melts. Put high-cardinality detail in logs/traces, not metric labels.
**Alert design:** page on *user-visible symptoms* (SLO burn), not on "CPU 80%".
**Incident flow:** detect → declare severity → incident commander → mitigate (rollback first, root-cause later) → communicate → blameless postmortem with action items.
**Health checks:** liveness (is process stuck? restart), readiness (can I serve? remove from LB during warm-up or dependency outage), startup probe for slow boots. Don't make liveness depend on the database – you'll restart-loop during a DB outage.

## 18.4 Deployment strategies – diagrams
```
Rolling   : v1 v1 v1 v1 -> v2 v1 v1 v1 -> v2 v2 v1 v1 -> ... (both versions live; must be compatible)
Blue-green: LB -> BLUE(v1)   ; deploy GREEN(v2), test ; flip LB -> GREEN ; keep BLUE for rollback
Canary    : 1% -> v2 ... watch error/latency SLIs ... 10% -> 50% -> 100% (auto-rollback on regression)
Shadow    : live traffic mirrored to v2, responses discarded (compare outputs; no side effects)
```
**Expand-and-contract migration – rename column `name` → `full_name`**
```
1 Expand  : ADD full_name (nullable); app writes both
2 Backfill: copy name -> full_name in batches
3 Switch  : app reads full_name
4 Contract: stop writing name; later DROP name
Every step is backward compatible, so rolling deploys and rollbacks are safe.
```
**Feature flag:** `if (flags.enabled("new-checkout", user)) newFlow(); else oldFlow();` – deploy dark, release gradually, kill switch; delete stale flags.

## 18.5 Docker & Kubernetes example
```dockerfile
FROM eclipse-temurin:21-jre
COPY app.jar /app.jar
USER 10001
ENTRYPOINT ["java","-jar","/app.jar"]
```
```yaml
apiVersion: apps/v1
kind: Deployment
metadata: { name: orders }
spec:
  replicas: 3
  strategy: { type: RollingUpdate, rollingUpdate: { maxUnavailable: 0, maxSurge: 1 } }
  selector: { matchLabels: { app: orders } }
  template:
    metadata: { labels: { app: orders } }
    spec:
      containers:
      - name: orders
        image: registry/orders:1.4.2
        resources: { requests: { cpu: "500m", memory: 512Mi }, limits: { memory: 1Gi } }
        readinessProbe: { httpGet: { path: /ready, port: 8080 } }
        livenessProbe:  { httpGet: { path: /live,  port: 8080 } }
```
```
kubectl apply -> API server -> etcd (desired state)
Controllers reconcile -> Scheduler picks node -> kubelet runs container -> Service/Endpoints route traffic
HPA scales replicas on CPU/custom metrics; Ingress exposes HTTP; ConfigMap/Secret inject config.
```
**Service mesh data/control plane**
```
Pod[ app <-> sidecar proxy ]  <--mTLS-->  Pod[ sidecar <-> app ]
            ^ policy/certs/telemetry config
        [ Control plane (Istiod) ]
```
**IaC example (Terraform):** `resource "aws_db_instance" "main" { engine="postgres" multi_az=true backup_retention_period=7 }` → `plan` shows diff, `apply` converges; keep state remote and locked; detect drift.
**GitOps:** merge PR → Argo CD reconciles cluster to Git; rollback = `git revert`.

## 18.6 Cost examples (FinOps)
- 100 instances × $0.10/h × 730 h ≈ $7,300/month; rightsizing to 60% saves ~$2,900. Spot/preemptible for stateless/batch (−60–90%); reserved/savings plans for steady base load.
- Cross-AZ/region egress ($0.01–0.09/GB) can exceed compute: keep chatty services in same AZ where safe, compress, cache at edge.
- Observability bills grow with log volume and metric cardinality – sample and set retention tiers.

