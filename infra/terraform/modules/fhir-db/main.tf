variable "environment" {
  type = string
}

# TODO: replace with a real managed Postgres (RDS/Cloud SQL/Azure DB)
# clustering config for HAPI FHIR.
resource "aws_db_instance" "fhir" {
  identifier        = "fhir-${var.environment}"
  engine            = "postgres"
  engine_version    = "16"
  instance_class    = "db.t3.medium"
  allocated_storage = 50

  storage_encrypted = true # HIPAA data-at-rest requirement

  # Fill in real credentials via secrets manager, not plaintext.
  username = "fhir_admin"

  tags = {
    Environment = var.environment
    Compliance  = "hipaa"
  }
}

output "endpoint" {
  value = aws_db_instance.fhir.endpoint
}
