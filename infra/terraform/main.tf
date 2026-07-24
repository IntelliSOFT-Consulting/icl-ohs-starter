terraform {
  required_version = ">= 1.7"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # TODO: configure a remote backend (S3 + DynamoDB lock table, GCS, etc.)
  # backend "s3" {}
}

provider "aws" {
  region = var.aws_region
}

module "fhir_db" {
  source      = "./modules/fhir-db"
  environment = var.environment
}

module "compute_cluster" {
  source      = "./modules/compute-cluster"
  environment = var.environment
}
