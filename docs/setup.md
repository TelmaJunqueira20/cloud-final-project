# Setup Guide

## Prerequisites

- AWS CLI configured with valid credentials
- Terraform >= 1.0
- Docker Desktop
- Git
- Java 21

## AWS Configuration

```bash
aws configure
# AWS Access Key ID: <your-key>
# AWS Secret Access Key: <your-secret>
# Default region: eu-west-1
# Default output format: json
```

## Infrastructure Setup

```bash
cd infrastructure/terraform
terraform init
terraform plan -var="db_password=<your-password>"
terraform apply -var="db_password=<your-password>"
```

## Local Development

```bash
# Run all services locally
docker-compose up -d
```