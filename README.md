# Cloud Final Project — Microserviços na AWS

Sistema de e-commerce baseado em microserviços, deployado na AWS com Infraestrutura como Código, Docker, CI/CD e arquitetura orientada a eventos.

---

## Índice

- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Serviços](#serviços)
- [Comunicação entre Serviços](#comunicação-entre-serviços)
- [Infraestrutura](#infraestrutura)
- [Pipeline CI/CD](#pipeline-cicd)
- [Endpoints da API](#endpoints-da-api)
- [Health Checks](#health-checks)
- [Execução Local](#execução-local)
- [Recursos AWS](#recursos-aws)
- [Equipa](#equipa)

---

## Arquitetura

```
Internet
   │
   ▼
EC2 t3.small (subnet pública) — 108.131.130.201
   ├── api-gateway     :8080  (ponto de entrada único)
   ├── user-service    :8081  (gestão de utilizadores)
   ├── product-service :8082  (catálogo de produtos)
   └── order-service   :8083  (gestão de encomendas)
            │
            ├── RDS MySQL (subnet privada)
            └── SQS Queue (mensagens assíncronas)

GitHub Actions (CI/CD)
   └── OIDC → AWS
         ├── ECR (push de imagens Docker)
         └── SSM (deploy remoto na EC2)
```

### Rede

- **VPC** com subnets públicas e privadas em `eu-west-1`
- **EC2** na subnet pública — exposta à internet
- **RDS** na subnet privada — acessível apenas pela EC2
- **Security Groups** controlam o tráfego entre camadas

---

## Tecnologias

| Tecnologia | Função |
|---|---|
| AWS EC2 (t3.small) | Hospedagem da aplicação |
| AWS RDS MySQL 8.0 | Base de dados persistente |
| AWS SQS | Mensagens assíncronas entre serviços |
| AWS ECR | Registo de imagens Docker |
| AWS SSM | Deploy remoto sem SSH |
| Terraform | Infraestrutura como Código |
| Docker | Containerização dos serviços |
| GitHub Actions | Pipeline CI/CD |
| Ansible | Gestão de configuração |
| Spring Boot 3.4 | Framework dos microserviços |
| Spring Cloud Gateway | Routing no API Gateway |
| OpenFeign | Chamadas síncronas entre serviços |

---

## Serviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| api-gateway | 8080 | Ponto de entrada único, routing de pedidos |
| user-service | 8081 | Gestão de utilizadores |
| product-service | 8082 | Catálogo de produtos, consome eventos SQS |
| order-service | 8083 | Encomendas, publica eventos SQS |

---

## Comunicação entre Serviços

### Síncrona (OpenFeign)

```
order-service → user-service    (validar utilizador)
order-service → product-service (validar produto)
```

### Assíncrona (AWS SQS)

```
order-service → SQS → product-service (atualizar stock)
```

---

## Infraestrutura

```
infrastructure/terraform/
├── main.tf         # Configuração do backend (S3)
├── variables.tf    # Variáveis de entrada
├── outputs.tf      # Valores de output (IP, endpoint RDS, etc.)
├── networking.tf   # VPC, subnets, security groups
├── compute.tf      # EC2, RDS, IAM roles, key pair
└── sqs.tf          # Filas SQS e Dead Letter Queue
```

### Deploy da Infraestrutura

```bash
cd infrastructure/terraform
terraform init
terraform apply -var="db_password=<password>"
```

O estado do Terraform é guardado remotamente num bucket S3 (`telma-terraform-state`), garantindo consistência entre a equipa.

### Gestão de Configuração

```bash
cd ansible
ansible-playbook -i inventory.ini playbook.yml
```

---

## Pipeline CI/CD

O pipeline está definido em `.github/workflows/deploy.yml` e tem 3 jobs:

```
Push para main  →  Build and Test  →  Build and Push  →  Deploy to EC2
PR aberto       →  Build and Test  (sem deploy)
```

### Autenticação com a AWS

O pipeline usa **OIDC** (OpenID Connect) para autenticar na AWS sem guardar chaves de acesso. O GitHub assume um IAM Role com permissões limitadas ao ECR e SSM.

### Segredos

As credenciais sensíveis (passwords da base de dados) são guardadas em **GitHub Secrets** e nunca expostas no código.

### Jobs

| Job | Quando corre | O que faz |
|---|---|---|
| Build and Test | PRs e push para main | Compila os 4 serviços com Maven |
| Build and Push | Só push para main | Build e push das imagens Docker para o ECR |
| Deploy to EC2 | Só push para main | Deploy via SSM — pull das imagens e restart dos containers |

---

## Endpoints da API

Todos os pedidos passam pelo API Gateway na porta `8080`:

### Utilizadores
```bash
GET    /api/users
POST   /api/users        {"name":"João","email":"joao@exemplo.com"}
GET    /api/users/{id}
```

### Produtos
```bash
GET    /api/products
POST   /api/products     {"name":"Laptop","description":"...","price":999.99,"stockQuantity":10}
GET    /api/products/{id}
```

### Encomendas
```bash
GET    /api/orders
POST   /api/orders       {"userId":1,"items":[{"productId":1,"quantity":2}]}
GET    /api/orders/{id}
GET    /api/orders/user/{userId}
PUT    /api/orders/{id}/status?status=CONFIRMED
```

---

## Health Checks

```bash
curl http://108.131.130.201:8080/actuator/health  # api-gateway
curl http://108.131.130.201:8081/actuator/health  # user-service
curl http://108.131.130.201:8082/actuator/health  # product-service
curl http://108.131.130.201:8083/actuator/health  # order-service
```

---

## Execução Local

### Pré-requisitos

- Docker Desktop instalado
- Java 21
- AWS CLI configurado

### Correr localmente

```bash
docker-compose up -d
```

---

## Recursos AWS

| Recurso | Valor |
|---|---|
| EC2 IP Público | `108.131.130.201` |
| RDS Endpoint | `cloud-final-project-db.ctci8kww4g6j.eu-west-1.rds.amazonaws.com` |
| SQS Queue | `cloud-final-project-orders` |
| ECR Registry | `442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project` |
| Região AWS | `eu-west-1` (Irlanda) |

---

## Equipa

- **Telma Junqueira**
- **Lasislau Hilário**