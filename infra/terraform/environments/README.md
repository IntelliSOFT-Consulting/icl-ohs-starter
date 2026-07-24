# infra/terraform/environments/

Per-environment variable values, applied on top of `variables.tf` when
running Terraform for a specific environment.

## What belongs here

One `.tfvars` file per environment. Never put secrets here directly —
reference secret managers/remote state instead.

## Sample: what's already in this folder

`dev.tfvars`:
```hcl
aws_region  = "us-east-1"
environment = "dev"
```

`prod.tfvars`:
```hcl
aws_region  = "us-east-1"
environment = "prod"
```

Use with:
```bash
terraform apply -var-file=environments/prod.tfvars
```
