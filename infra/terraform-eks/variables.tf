variable "aws_region" {
  type    = string
  default = "ap-northeast-2"
}

variable "rds_master_password" {
  type      = string
  sensitive = true
}