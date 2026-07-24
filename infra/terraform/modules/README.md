# infra/terraform/modules/

Reusable, composable Terraform modules. Each module is a self-contained
unit of infrastructure with its own inputs/outputs, referenced from the
root `main.tf`.

## What belongs here

One directory per infrastructure concern. Each module directory should
contain at minimum a `main.tf` declaring resources, inputs, and outputs.

## Sample: what's already in this folder

```
modules/
├── fhir-db/            # Managed Postgres/database clustering for HAPI FHIR
│   └── main.tf
└── compute-cluster/    # ECS/EKS/GKE configuration for runtime containers
    └── main.tf
```

## Adding a module

```hcl
# modules/<new-module>/main.tf
variable "environment" {
  type = string
}

resource "..." "..." {
  # ...
}

output "..." {
  value = ...
}
```

Then reference it from `infra/terraform/main.tf`:
```hcl
module "new_module" {
  source      = "./modules/<new-module>"
  environment = var.environment
}
```
