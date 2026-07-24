# infra/terraform/

Cloud infrastructure provisioning (AWS/GCP/Azure). Root module wires
together the reusable modules in `modules/`, parameterized per environment
via `environments/*.tfvars`.

## What belongs here

- `main.tf` — root module, declares providers and includes child modules
- `variables.tf` — root-level input variables
- `environments/` — one `.tfvars` file per environment (dev, staging, prod)
- `modules/` — reusable, composable infrastructure units

## Environment variables

For local CLI use only (CI uses repo secrets — see
`.github/workflows/cd-infra.yml`), source the **shared root `.env`**
before running Terraform by hand:
```bash
source ../../.env
terraform apply -var-file=environments/dev.tfvars
```
Relevant vars: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`,
`AWS_REGION`, `TF_VAR_environment`.

## Sample: what's already in this folder

`main.tf` (excerpt):
```hcl
module "fhir_db" {
  source      = "./modules/fhir-db"
  environment = var.environment
}

module "compute_cluster" {
  source      = "./modules/compute-cluster"
  environment = var.environment
}
```

Apply for a given environment:
```bash
terraform init
terraform apply -var-file=environments/dev.tfvars
```

## Adding infrastructure

New infra needs its own module under `modules/`, referenced from `main.tf`,
with any new variables added to both `variables.tf` and every file in
`environments/`.
