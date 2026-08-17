variable "mysql_root_password" {
  type      = string
  sensitive = true
}

variable "user_service_db_username" {
  type      = string
  sensitive = true
}

variable "user_service_db_password" {
  type      = string
  sensitive = true
}

variable "legacy_shop_db_password" {
  type      = string
  sensitive = true
}

variable "order_service_db_username" {
  type      = string
  sensitive = true
}

variable "order_service_db_password" {
  type      = string
  sensitive = true
}

variable "payment_service_db_username" {
  type      = string
  sensitive = true
}

variable "payment_service_db_password" {
  type      = string
  sensitive = true
}

variable "toss_secret_key" {
  type      = string
  sensitive = true
}

variable "jwt_secret" {
  type      = string
  sensitive = true
}

variable "mongo_root_username" {
  type      = string
  sensitive = true
}

variable "mongo_root_password" {
  type      = string
  sensitive = true
}

variable "grafana_admin_password" {
  type      = string
  sensitive = true
}

variable "rabbitmq_user" {
  type      = string
  sensitive = true
}
variable "rabbitmq_password" {
  type      = string
  sensitive = true
}

locals {
  parameters = {
    MYSQL_ROOT_PASSWORD          = var.mysql_root_password
    USER_SERVICE_DB_USERNAME     = var.user_service_db_username
    USER_SERVICE_DB_PASSWORD     = var.user_service_db_password
    LEGACY_SHOP_DB_PASSWORD      = var.legacy_shop_db_password
    ORDER_SERVICE_DB_USERNAME    = var.order_service_db_username
    ORDER_SERVICE_DB_PASSWORD    = var.order_service_db_password
    PAYMENT_SERVICE_DB_USERNAME  = var.payment_service_db_username
    PAYMENT_SERVICE_DB_PASSWORD  = var.payment_service_db_password
    TOSS_SECRET_KEY              = var.toss_secret_key
    JWT_SECRET                   = var.jwt_secret
    MONGO_ROOT_USERNAME          = var.mongo_root_username
    MONGO_ROOT_PASSWORD          = var.mongo_root_password
    GRAFANA_ADMIN_PASSWORD = var.grafana_admin_password
    RABBITMQ_USER     = var.rabbitmq_user
    RABBITMQ_PASSWORD = var.rabbitmq_password
  }
}

resource "aws_ssm_parameter" "app_secrets" {
  for_each = local.parameters

  name  = "/e-commerce/prod/${each.key}"
  type  = "SecureString"
  value = each.value
}