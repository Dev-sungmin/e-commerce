terraform {
  backend "s3" {
    bucket = "e-commerce-terraform-state-sungmin2026"
    key = "ec2-app-server/terraform.tfstate"
    region  = "ap-northeast-2"
    encrypt = true
  }
}