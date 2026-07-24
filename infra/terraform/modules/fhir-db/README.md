# infra/terraform/modules/fhir-db/

Managed database module for the HAPI FHIR service. Provisions the
Postgres instance that `apps/core-platform/service-fhir-java` connects to
in deployed environments.

## What belongs here

- `main.tf` — the database resource, its inputs, and its outputs

## Sample: what's already in this folder

```hcl
resource "aws_db_instance" "fhir" {
  identifier        = "fhir-${var.environment}"
  engine            = "postgres"
  engine_version    = "16"
  instance_class    = "db.t3.medium"
  allocated_storage = 50
  storage_encrypted = true # HIPAA data-at-rest requirement
  username          = "fhir_admin"
}

output "endpoint" {
  value = aws_db_instance.fhir.endpoint
}
```

## Instantiating for a real project

- Move credentials to a secrets manager (AWS Secrets Manager, SSM
  Parameter Store) instead of plaintext variables.
- Size `instance_class` / `allocated_storage` for real production load.
