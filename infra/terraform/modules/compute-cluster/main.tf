variable "environment" {
  type = string
}

# TODO: replace with real ECS/EKS/GKE cluster configuration for
# gateway-node and service-fhir-java runtime containers.
resource "aws_ecs_cluster" "main" {
  name = "ohs-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Environment = var.environment
  }
}

output "cluster_id" {
  value = aws_ecs_cluster.main.id
}
