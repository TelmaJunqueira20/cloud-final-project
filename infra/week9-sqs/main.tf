provider "aws" {
  region = var.aws_region
}

# Filas Standard (existentes)
resource "aws_sqs_queue" "dlq" {
  name                      = "${var.prefix}-product-events-dlq"
  message_retention_seconds = 345600
}

resource "aws_sqs_queue" "product_events" {
  name                       = "${var.prefix}-product-events"
  visibility_timeout_seconds = 60
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.dlq.arn
    maxReceiveCount     = 5
  })
}

# Filas FIFO (novas)
resource "aws_sqs_queue" "product_events_fifo_dlq" {
  name                        = "${var.prefix}-product-events-dlq.fifo"
  fifo_queue                  = true
  content_based_deduplication = true
  message_retention_seconds   = 345600
}

resource "aws_sqs_queue" "product_events_fifo" {
  name                        = "${var.prefix}-product-events.fifo"
  fifo_queue                  = true
  content_based_deduplication = true
  visibility_timeout_seconds  = 60
  message_retention_seconds   = 345600
  receive_wait_time_seconds   = 20

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.product_events_fifo_dlq.arn
    maxReceiveCount     = 5
  })
}

output "product_events_queue_url" {
  value = aws_sqs_queue.product_events.url
}

output "dlq_queue_url" {
  value = aws_sqs_queue.dlq.url
}

output "product_events_fifo_queue_url" {
  value = aws_sqs_queue.product_events_fifo.url
}