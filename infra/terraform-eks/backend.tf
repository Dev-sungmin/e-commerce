terraform {
  backend "s3" {
    bucket  = "e-commerce-terraform-state-sungmin2026"
    key     = "e-commerce-eks/terraform.tfstate"
    region  = "ap-northeast-2"
    encrypt = true
  }
}