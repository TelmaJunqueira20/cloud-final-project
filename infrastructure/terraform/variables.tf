variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-1"
}

variable "project_name" {
  description = "Project name used for tagging"
  type        = string
  default     = "cloud-final-project"
}

variable "ec2_ami" {
  description = "AMI ID for EC2 instance (Amazon Linux 2)"
  type        = string
  default     = "ami-076a3d6391b613606"
}