# Fila principal
resource "aws_sqs_queue" "orders" {
  name                      = "${var.project_name}-orders"
  delay_seconds             = 0
  max_message_size          = 262144
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.orders_dlq.arn
    maxReceiveCount     = 3
  })

  tags = {
    Project = var.project_name
  }
}

# Dead Letter Queue
resource "aws_sqs_queue" "orders_dlq" {
  name = "${var.project_name}-orders-dlq"

  tags = {
    Project = var.project_name
  }
}

# Output do URL da fila
output "sqs_queue_url" {
  value = aws_sqs_queue.orders.url
}

output "sqs_queue_arn" {
  value = aws_sqs_queue.orders.arn
}
