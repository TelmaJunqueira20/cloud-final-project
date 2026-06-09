# Cloud Final Project — Microservices on AWS

A microservices-based e-commerce system deployed on AWS using Infrastructure as Code, Docker, CI/CD, and event-driven architecture.

## Architecture
Internet
│
▼
EC2 t3.small (public subnet) — 108.131.130.201
├── api-gateway     :8080  (single entry point)
├── user-service    :8081  (user management)
├── product-service :8082  (product catalogue)
└── order-service   :8083  (order management)
│
├── RDS MySQL (private subnet)
└── SQS Queue (async messaging)
GitHub Actions (CI/CD)
└── OIDC → AWS
├── ECR (push images)
└── SSM (deploy to EC2)

## Technologies

| Technology | Purpose |
|---|---|
| AWS EC2 (t3.small) | Application hosting |
| AWS RDS MySQL 8.0 | Persistent database |
| AWS SQS | Async event messaging |
| AWS ECR | Container registry |
| AWS SSM | Remote deployment |
| Terraform | Infrastructure as Code |
| Docker | Containerization |
| GitHub Actions | CI/CD pipeline |
| Ansible | Configuration management |
| Spring Boot 3.4 | Microservices framework |
| Spring Cloud Gateway | API Gateway routing |
| OpenFeign | Synchronous inter-service calls |

## Services

| Service | Port | Responsibility |
|---|---|---|
| api-gateway | 8080 | Single entry point, request routing |
| user-service | 8081 | User management |
| product-service | 8082 | Product catalogue, consumes SQS events |
| order-service | 8083 | Orders, publishes SQS events |

## Inter-Service Communication

**Synchronous (OpenFeign):**
- order-service → user-service (validate user)
- order-service → product-service (validate product)

**Asynchronous (AWS SQS):**
- order-service → SQS → product-service (update stock)

## Quick Start

### Prerequisites
- AWS CLI configured
- Terraform >= 1.0
- Docker Desktop
- Java 21

### Deploy Infrastructure
```bash
cd infrastructure/terraform
terraform init
terraform apply -var="db_password=<password>"
```

### Run Locally
```bash
docker-compose up -d
```

## API Endpoints

All requests go through the API Gateway on port 8080:

```bash
# Users
GET    /api/users
POST   /api/users        {"name":"John","email":"john@example.com"}
GET    /api/users/{id}

# Products
GET    /api/products
POST   /api/products     {"name":"Laptop","description":"...","price":999.99,"stockQuantity":10}
GET    /api/products/{id}

# Orders
GET    /api/orders
POST   /api/orders       {"userId":1,"items":[{"productId":1,"quantity":2}]}
GET    /api/orders/{id}
GET    /api/orders/user/{userId}
PUT    /api/orders/{id}/status?status=CONFIRMED
```

## Health Checks

```bash
curl http://108.131.130.201:8080/actuator/health  # api-gateway
curl http://108.131.130.201:8081/actuator/health  # user-service
curl http://108.131.130.201:8082/actuator/health  # product-service
curl http://108.131.130.201:8083/actuator/health  # order-service
```

## CI/CD Pipeline

Every push to `main` triggers:
1. **Build and Test** — compiles all 4 services
2. **Build and Push** — Docker images pushed to ECR
3. **Deploy** — rolling update via AWS SSM

Pull Requests trigger only the Build and Test step.

## Infrastructure
infrastructure/terraform/
├── main.tf          # Backend config (S3 + DynamoDB)
├── variables.tf     # Input variables
├── outputs.tf       # Output values
├── networking.tf    # VPC, subnets, security groups
├── compute.tf       # EC2, RDS, key pair, IAM
└── sqs.tf           # SQS queues
## Configuration Management

```bash
# Run Ansible playbook (from EC2)
cd ansible
ansible-playbook -i inventory.ini playbook.yml
```

## Documentation

- [Architecture](docs/architecture.md)
- [Setup Guide](docs/setup.md)
- [Deployment Guide](docs/deployment.md)
- [Security](docs/security.md)
- [Limitations](docs/limitations.md)

## AWS Resources

| Resource | Value |
|---|---|
| EC2 IP | 108.131.130.201 |
| RDS Endpoint | cloud-final-project-db.ctci8kww4g6j.eu-west-1.rds.amazonaws.com |
| SQS Queue | cloud-final-project-orders |
| ECR Registry | 442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project |
| Region | eu-west-1 |

## Team

- Telma Junqueira
- Lasislau Hilario