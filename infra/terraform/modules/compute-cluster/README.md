# infra/terraform/modules/compute-cluster/

Compute cluster module (ECS/EKS/GKE) that runs the containers built from
`apps/core-platform/gateway-node` and `apps/core-platform/service-fhir-java`.

## What belongs here

- `main.tf` — the cluster resource, its inputs, and its outputs

## Sample: what's already in this folder

```hcl
resource "aws_ecs_cluster" "main" {
  name = "ohs-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

output "cluster_id" {
  value = aws_ecs_cluster.main.id
}
```

## Instantiating for a real project

- Swap `aws_ecs_cluster` for your target orchestrator (EKS, GKE, etc.) if
  not using ECS.
- Add task definitions / services once container images are published by
  `.github/workflows/cd-core.yml`.
