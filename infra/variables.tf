variable "aws_region" {
  description = "AWS region where the FeedHub resources will be deployed."
  type        = string
  default     = "sa-east-1"
}

variable "environment" {
  description = "Environment name (dev, homol, prod)."
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Project name used as a prefix for resource naming."
  type        = string
  default     = "feedhub"
}

variable "log_retention_in_days" {
  description = "Number of days to retain CloudWatch logs for all Lambdas."
  type        = number
  default     = 14
}

# --- JWT ---
variable "jwt_secret" {
  description = "Secret key used to sign JWT tokens."
  type        = string
  sensitive   = true
}

# --- Admin seed user ---
variable "admin_email" {
  description = "Default admin user email seeded on auth-service startup."
  type        = string
  default     = "admin@feedhub.com"
}

variable "admin_password" {
  description = "Default admin user password seeded on auth-service startup."
  type        = string
  default     = "Admin@123"
  sensitive   = true
}

variable "admin_nome" {
  description = "Default admin user display name."
  type        = string
  default     = "Administrador FeedHub"
}

# --- DynamoDB table names ---
variable "usuarios_table_name" {
  description = "Name of the DynamoDB table storing users."
  type        = string
  default     = "usuarios"
}

variable "avaliacoes_table_name" {
  description = "Name of the DynamoDB table storing feedback (avaliacoes)."
  type        = string
  default     = "avaliacoes"
}

variable "dynamodb_billing_mode" {
  description = "Billing mode for the DynamoDB tables."
  type        = string
  default     = "PAY_PER_REQUEST"
}

# --- SES ---
variable "ses_sender_email" {
  description = "Verified SES sender email address used to send notification/report emails."
  type        = string
  default     = "no-reply@feedhub.com"
}

variable "notification_recipient_email" {
  description = "Recipient email address for critical feedback notifications."
  type        = string
  default     = "admin@feedhub.com"
}

variable "report_recipient_email" {
  description = "Recipient email address for the weekly report."
  type        = string
  default     = "admin@feedhub.com"
}

# --- Lambda artifacts ---
variable "auth_service_jar_path" {
  description = "Path to the auth-service shaded JAR artifact."
  type        = string
  default     = "../src/auth-service/target/auth-service.jar"
}

variable "feedback_service_jar_path" {
  description = "Path to the feedback-service shaded JAR artifact."
  type        = string
  default     = "../src/feedback-service/target/feedback-service.jar"
}

variable "notification_service_jar_path" {
  description = "Path to the notification-service shaded JAR artifact."
  type        = string
  default     = "../src/notification-service/target/notification-service.jar"
}

variable "report_service_jar_path" {
  description = "Path to the report-service shaded JAR artifact."
  type        = string
  default     = "../src/report-service/target/report-service.jar"
}

variable "lambda_memory_size" {
  description = "Default memory size (MB) for the Lambda functions."
  type        = number
  default     = 512
}

variable "lambda_timeout" {
  description = "Default timeout (seconds) for the Lambda functions."
  type        = number
  default     = 30
}

variable "api_gateway_name" {
  description = "Name for the API Gateway HTTP API."
  type        = string
  default     = "feedhub-api"
}

# --- Weekly report schedule ---
variable "report_schedule_expression" {
  description = "EventBridge schedule expression that triggers the weekly report Lambda."
  type        = string
  default     = "rate(7 days)"
}
