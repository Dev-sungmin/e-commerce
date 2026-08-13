locals {
  ecr_repositories = [
    "user-service",
    "gateway",
    "php-legacy",
    "order-service",
    "payment-service",
    "cart-service",
    "review-service",
  ]
}

resource "aws_ecr_repository" "services" {
  for_each             = toset(local.ecr_repositories)
  name                 = "e-commerce-${each.value}"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

output "ecr_repository_urls" {
  value = { for k, v in aws_ecr_repository.services : k => v.repository_url }
}