data "aws_caller_identity" "current" {}

locals {
  common_tags = {
    Project     = var.project_name
    Environment = var.environment
  }
}

# ---------------------------------------------------------------------------
# DynamoDB tables
# ---------------------------------------------------------------------------

resource "aws_dynamodb_table" "usuarios" {
  name         = var.usuarios_table_name
  billing_mode = var.dynamodb_billing_mode
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "email"
    type = "S"
  }

  global_secondary_index {
    name            = "email-index"
    hash_key        = "email"
    projection_type = "ALL"
  }

  tags = local.common_tags
}

resource "aws_dynamodb_table" "avaliacoes" {
  name         = var.avaliacoes_table_name
  billing_mode = var.dynamodb_billing_mode
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "alunoEmail"
    type = "S"
  }

  global_secondary_index {
    name            = "alunoEmail-index"
    hash_key        = "alunoEmail"
    projection_type = "ALL"
  }

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# SNS topic for critical feedback notifications
# ---------------------------------------------------------------------------

resource "aws_sns_topic" "critical_notifications" {
  name = "${var.project_name}-critical-notifications"
  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# auth-service Lambda (HTTP, behind API Gateway)
# ---------------------------------------------------------------------------

module "auth_service" {
  source = "./modules/lambda"

  function_name = "${var.project_name}-auth-service"
  artifact_path = var.auth_service_jar_path
  handler       = "com.feedhub.auth.StreamLambdaHandler::handleRequest"
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout

  log_retention_in_days = var.log_retention_in_days
  tags                  = local.common_tags

  environment_variables = {
    JWT_SECRET        = var.jwt_secret
    USUARIOS_TABLE    = aws_dynamodb_table.usuarios.name
    DYNAMODB_ENDPOINT = ""
    ADMIN_EMAIL       = var.admin_email
    ADMIN_PASSWORD    = var.admin_password
    ADMIN_NOME        = var.admin_nome
    AWS_REGION_NAME   = var.aws_region
  }

  extra_policy_statements = [
    {
      Effect = "Allow"
      Action = [
        "dynamodb:GetItem",
        "dynamodb:PutItem",
        "dynamodb:UpdateItem",
        "dynamodb:Query",
        "dynamodb:Scan",
      ]
      Resource = [
        aws_dynamodb_table.usuarios.arn,
        "${aws_dynamodb_table.usuarios.arn}/index/*",
      ]
    }
  ]
}

# ---------------------------------------------------------------------------
# feedback-service Lambda (HTTP, behind API Gateway)
# ---------------------------------------------------------------------------

module "feedback_service" {
  source = "./modules/lambda"

  function_name = "${var.project_name}-feedback-service"
  artifact_path = var.feedback_service_jar_path
  handler       = "com.feedhub.feedback.StreamLambdaHandler::handleRequest"
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout

  log_retention_in_days = var.log_retention_in_days
  tags                  = local.common_tags

  environment_variables = {
    JWT_SECRET             = var.jwt_secret
    AVALIACOES_TABLE       = aws_dynamodb_table.avaliacoes.name
    DYNAMODB_ENDPOINT      = ""
    SNS_CRITICAL_TOPIC_ARN = aws_sns_topic.critical_notifications.arn
    AWS_REGION_NAME        = var.aws_region
  }

  extra_policy_statements = [
    {
      Effect = "Allow"
      Action = [
        "dynamodb:GetItem",
        "dynamodb:PutItem",
        "dynamodb:UpdateItem",
        "dynamodb:Query",
        "dynamodb:Scan",
      ]
      Resource = [
        aws_dynamodb_table.avaliacoes.arn,
        "${aws_dynamodb_table.avaliacoes.arn}/index/*",
      ]
    },
    {
      Effect   = "Allow"
      Action   = ["sns:Publish"]
      Resource = [aws_sns_topic.critical_notifications.arn]
    }
  ]
}

# ---------------------------------------------------------------------------
# notification-service Lambda (triggered by SNS)
# ---------------------------------------------------------------------------

module "notification_service" {
  source = "./modules/lambda"

  function_name = "${var.project_name}-notification-service"
  artifact_path = var.notification_service_jar_path
  handler       = "com.feedhub.notification.presentation.NotificationHandler::handleRequest"
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout

  log_retention_in_days = var.log_retention_in_days
  tags                  = local.common_tags

  environment_variables = {
    SES_SENDER_EMAIL             = var.ses_sender_email
    NOTIFICATION_RECIPIENT_EMAIL = var.notification_recipient_email
    AWS_REGION_NAME              = var.aws_region
  }

  extra_policy_statements = [
    {
      Effect   = "Allow"
      Action   = ["ses:SendEmail", "ses:SendRawEmail"]
      Resource = ["*"]
    }
  ]
}

resource "aws_sns_topic_subscription" "notification_service" {
  topic_arn = aws_sns_topic.critical_notifications.arn
  protocol  = "lambda"
  endpoint  = module.notification_service.function_arn
}

resource "aws_lambda_permission" "sns_invoke_notification_service" {
  statement_id  = "AllowExecutionFromSNS"
  action        = "lambda:InvokeFunction"
  function_name = module.notification_service.function_name
  principal     = "sns.amazonaws.com"
  source_arn    = aws_sns_topic.critical_notifications.arn
}

# ---------------------------------------------------------------------------
# report-service Lambda (triggered weekly by EventBridge)
# ---------------------------------------------------------------------------

module "report_service" {
  source = "./modules/lambda"

  function_name = "${var.project_name}-report-service"
  artifact_path = var.report_service_jar_path
  handler       = "com.feedhub.report.presentation.ReportHandler::handleRequest"
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout

  log_retention_in_days = var.log_retention_in_days
  tags                  = local.common_tags

  environment_variables = {
    AVALIACOES_TABLE       = aws_dynamodb_table.avaliacoes.name
    DYNAMODB_ENDPOINT      = ""
    SES_SENDER_EMAIL       = var.ses_sender_email
    REPORT_RECIPIENT_EMAIL = var.report_recipient_email
    AWS_REGION_NAME        = var.aws_region
  }

  extra_policy_statements = [
    {
      Effect = "Allow"
      Action = [
        "dynamodb:Query",
        "dynamodb:Scan",
      ]
      Resource = [
        aws_dynamodb_table.avaliacoes.arn,
        "${aws_dynamodb_table.avaliacoes.arn}/index/*",
      ]
    },
    {
      Effect   = "Allow"
      Action   = ["ses:SendEmail", "ses:SendRawEmail"]
      Resource = ["*"]
    }
  ]
}

resource "aws_cloudwatch_event_rule" "weekly_report" {
  name                = "${var.project_name}-weekly-report"
  description         = "Triggers the FeedHub weekly report Lambda."
  schedule_expression = var.report_schedule_expression
  tags                = local.common_tags
}

resource "aws_cloudwatch_event_target" "weekly_report" {
  rule = aws_cloudwatch_event_rule.weekly_report.name
  arn  = module.report_service.function_arn
}

resource "aws_lambda_permission" "eventbridge_invoke_report_service" {
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = module.report_service.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.weekly_report.arn
}

# ---------------------------------------------------------------------------
# API Gateway (HTTP API) fronting auth-service and feedback-service
# ---------------------------------------------------------------------------

resource "aws_apigatewayv2_api" "http" {
  name          = var.api_gateway_name
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http.id
  name        = "$default"
  auto_deploy = true
}

resource "aws_apigatewayv2_integration" "auth_service" {
  api_id                 = aws_apigatewayv2_api.http.id
  integration_type       = "AWS_PROXY"
  integration_uri        = module.auth_service.invoke_arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "auth_service" {
  api_id    = aws_apigatewayv2_api.http.id
  route_key = "ANY /auth/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.auth_service.id}"
}

resource "aws_lambda_permission" "api_gateway_auth_service" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = module.auth_service.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.http.execution_arn}/*/*"
}

resource "aws_apigatewayv2_integration" "feedback_service" {
  api_id                 = aws_apigatewayv2_api.http.id
  integration_type       = "AWS_PROXY"
  integration_uri        = module.feedback_service.invoke_arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "feedback_service" {
  api_id    = aws_apigatewayv2_api.http.id
  route_key = "ANY /avaliacoes/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.feedback_service.id}"
}

resource "aws_apigatewayv2_route" "feedback_service_root" {
  api_id    = aws_apigatewayv2_api.http.id
  route_key = "ANY /avaliacoes"
  target    = "integrations/${aws_apigatewayv2_integration.feedback_service.id}"
}

resource "aws_lambda_permission" "api_gateway_feedback_service" {
  statement_id  = "AllowExecutionFromAPIGatewayFeedback"
  action        = "lambda:InvokeFunction"
  function_name = module.feedback_service.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.http.execution_arn}/*/*"
}

