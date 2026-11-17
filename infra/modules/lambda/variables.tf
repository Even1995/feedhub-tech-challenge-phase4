variable "function_name" {
  description = "Name of the Lambda function."
  type        = string
}

variable "artifact_path" {
  description = "Path (relative to the module caller) to the Lambda JAR artifact."
  type        = string
}

variable "handler" {
  description = "Lambda handler in package.Class::method format."
  type        = string
}

variable "runtime" {
  description = "Lambda runtime identifier."
  type        = string
  default     = "java21"
}

variable "memory_size" {
  description = "Memory allocated to the Lambda function in MB."
  type        = number
  default     = 512
}

variable "timeout" {
  description = "Timeout of the Lambda function in seconds."
  type        = number
  default     = 30
}

variable "environment_variables" {
  description = "Environment variables for the Lambda function."
  type        = map(string)
  default     = {}
}

variable "extra_policy_statements" {
  description = "Additional IAM policy statements to attach to the Lambda execution role."
  type        = list(any)
  default     = []
}

variable "log_retention_in_days" {
  description = "Number of days to retain CloudWatch logs."
  type        = number
  default     = 14
}

variable "tags" {
  description = "Tags to apply to resources."
  type        = map(string)
  default     = {}
}
