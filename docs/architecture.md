# Architecture

## Overview
Microservices-based system deployed on AWS using Infrastructure as Code
(Terraform), Ansible, and GitHub Actions CI/CD.

## Services

| Service         | Port | Responsibility                          |
|-----------------|------|-----------------------------------------|
| api-gateway     | 8080 | Single entry point, request routing     |
| user-service    | 8081 | User management                         |
| product-service | 8082 | Product catalogue, publishes to Kafka   |
| order-service   | 8083 | Orders, consumes Kafka/SQS events       |

## Communication
- **Synchronous (HTTP):** api-gateway → services, order-service → user/product
- **Asynchronous:** product-service produz eventos, order-service consome via SQS

## AWS Infrastructure
- **VPC** com subnets públicas e privadas em 2 AZs
- **EC2** na subnet pública para os serviços
- **RDS** na subnet privada para a base de dados
- **SQS** para comunicação assíncrona
- **ECR** para armazenar as imagens Docker

## Security
- Security Groups com least-privilege
- RDS sem acesso público
- Credenciais via AWS Secrets Manager / SSM
- OIDC entre GitHub Actions e AWS (sem chaves estáticas)