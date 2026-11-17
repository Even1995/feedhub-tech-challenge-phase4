output "api_gateway_endpoint" {
  description = "Base invoke URL of the FeedHub HTTP API."
  value       = aws_apigatewayv2_stage.default.invoke_url
}

output "auth_service_function_name" {
  value = module.auth_service.function_name
}

output "feedback_service_function_name" {
  value = module.feedback_service.function_name
}

output "notification_service_function_name" {
  value = module.notification_service.function_name
}

output "report_service_function_name" {
  value = module.report_service.function_name
}

output "usuarios_table_name" {
  value = aws_dynamodb_table.usuarios.name
}

output "avaliacoes_table_name" {
  value = aws_dynamodb_table.avaliacoes.name
}

output "critical_notifications_topic_arn" {
  value = aws_sns_topic.critical_notifications.arn
}