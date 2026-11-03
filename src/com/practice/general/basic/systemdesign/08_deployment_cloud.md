# Part 8 – Deployment, Cloud, Containers, Serverless, Edge

## 8.1 Infrastructure evolution
```
Physical servers/bare metal -> Virtual machines (hypervisors) -> IaaS cloud (EC2, 2006)
 -> PaaS (Heroku, App Engine) -> Containers (Docker 2013) -> Orchestration (Kubernetes 2014)
 -> Service mesh -> Serverless/FaaS & managed services -> Edge / multi-region -> Platform engineering
```
| Stage | Pressure | Solves | New problem |
|---|---|---|---|
| Physical/bare metal | — | Max performance, control | Weeks to provision, low utilization, snowflake servers |
| VMs/hypervisor | Cost/utilization | Consolidation, isolation | Overhead, big images, slow boot |
| IaaS | Capex & lead time | Elastic, pay-as-you-go | Still manage OS/patching |
| PaaS | Ops burden | Push code | Constraints, lock-in |
| Containers (namespaces+cgroups, images, registries) | Env drift, density | Portable, fast, immutable packaging | Shared kernel (weaker isolation), orchestration needed; use microVMs (Firecracker/gVisor) when strong isolation required |
| Kubernetes | Managing many containers | Scheduling, self-healing, rolling updates, service discovery, ingress, autoscaling | Steep complexity, cost, YAML sprawl |
| Sidecar/service mesh (Istio, Linkerd; control plane + data plane) | Cross-cutting network concerns (mTLS, retries, telemetry) | Uniform policy w/o app code | Latency, resource overhead, debugging → ambient/eBPF meshes |
| Serverless | Idle cost, scaling ops | Scale to zero | Cold starts, limits, lock-in, hard local testing |
| Managed services | Undifferentiated heavy lifting | Less ops | Cost at scale, lock-in, opaque limits |

**Containers vs VMs:** share host kernel (fast, dense, weaker isolation) vs own kernel (strong isolation, heavier). **Kubernetes vs serverless:** control + portability + steady load vs zero-ops + spiky/low load.

## 8.2 Delivery & operations
| Concept | Notes |
|---|---|
| IaC (Terraform, CloudFormation, Pulumi) | Reproducible infra; state drift, secrets in state |
| Configuration management (Ansible, Chef, Puppet) | Mutable convergence → replaced by immutable infrastructure (replace, don't patch) |
| CI / continuous delivery / continuous deployment | Delivery = deployable always; deployment = auto to prod |
| Blue-green | Two environments, instant switch/rollback; double cost, DB compat |
| Rolling | Replace gradually; mixed versions co-exist |
| Canary | Small % first, auto-analysis; needs good metrics |
| A/B | Experimentation (business), not safety |
| Feature flags | Decouple deploy from release; flag debt |
| Shadow traffic | Mirror prod traffic to new version; side effects must be disabled |
| Rollbacks | Easy for stateless, hard with schema/data changes → forward-fix |
| DB deployment | Expand-and-contract: add (compatible) → dual write/backfill → switch reads → remove old |
| GitOps (Argo CD/Flux) | Git as source of truth; reconciliation |
| Platform engineering / IDP | Paved roads, self-service, reduce cognitive load; product mindset |
| IaaS/PaaS/SaaS/FaaS/CaaS | Responsibility ladder |
| Regions/AZs | Isolation units; cross-AZ traffic costs |
| Multi-cloud / hybrid | Resilience/regulation/negotiation vs lowest-common-denominator and complexity |
| Vendor lock-in | Weigh switching cost vs speed gained |
| FinOps / cost optimization | Tagging, rightsizing, reserved/spot, autoscaling, storage tiering, egress minimization |

## 8.3 Edge & global systems
- **CDN → edge compute** (Cloudflare Workers, Lambda@Edge): logic at POPs; limited runtime/state → edge KV, Durable Objects.
- **Global load balancing:** GeoDNS, anycast, latency routing.
- **Multi-region patterns:** active-passive (simple, RPO>0), active-active (needs conflict handling or region-pinned data, CRDTs, global DB), cell-based architecture (blast-radius isolation), data residency partitioning.
- **IoT/edge:** intermittent connectivity → local processing, MQTT, store-and-forward.

