# Deployment Guide

## CI/CD Automatic Deployment

Every push to `main` branch triggers the GitHub Actions pipeline automatically:

1. Builds Docker images for all 4 services
2. Pushes images to AWS ECR
3. Deploys to EC2 via AWS SSM

## Manual Deployment

### 1. Build and push images

```bash
# Login to ECR
$token = aws ecr get-login-password --region eu-west-1
docker login --username AWS --password $token 442940292574.dkr.ecr.eu-west-1.amazonaws.com

# Build and push each service
docker build -t user-service:latest ./user-service
docker tag user-service:latest 442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project:user-service
docker push 442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project:user-service
```

### 2. Deploy to EC2

```bash
ssh -i infrastructure/terraform/cloud-project-key ec2-user@<EC2_IP>

docker pull 442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project:user-service
docker rm -f user-service
docker run -d --name user-service --network microservices-network -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e DB_HOST=<RDS_ENDPOINT> \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=<password> \
  442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project:user-service
```

## Service Endpoints

| Service | Port | Health Check |
|---|---|---|
| api-gateway | 8080 | /actuator/health |
| user-service | 8081 | /actuator/health |
| product-service | 8082 | /actuator/health |
| order-service | 8083 | /actuator/health |

## AWS Resources

- **EC2:** 108.131.130.201
- **RDS:** cloud-final-project-db.ctci8kww4g6j.eu-west-1.rds.amazonaws.com:3306
- **SQS:** https://sqs.eu-west-1.amazonaws.com/442940292574/cloud-final-project-orders
- **ECR:** 442940292574.dkr.ecr.eu-west-1.amazonaws.com/cloud-final-project