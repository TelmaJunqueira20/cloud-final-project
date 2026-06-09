# Security

## IAM Roles

### EC2 Role (ec2-ecr-role)
Attached policies:
- `AmazonEC2ContainerRegistryReadOnly` — pull images from ECR
- `AmazonSQSFullAccess` — send/receive messages from SQS
- `AmazonSSMManagedInstanceCore` — allow SSM to manage the instance

### GitHub Actions Role (github-actions-role)
Attached policies:
- `AmazonEC2ContainerRegistryFullAccess` — build and push images to ECR
- `AmazonEC2FullAccess` — manage EC2 instances
- `AmazonSSMFullAccess` — deploy via SSM

Authentication uses **OIDC** — no static AWS credentials stored in GitHub.

## Network Security

- **RDS** is in a private subnet — not accessible from the internet
- **Security Group app** — only ports 8080-8083 and 22 open
- **Security Group db** — only port 3306 open, only from app security group

## Secrets Management

- Database credentials stored as **GitHub Secrets** (DB_PASSWORD, DB_USERNAME)
- No hardcoded credentials in code or configuration files
- ECR login uses temporary tokens (expire every 12 hours)

## Least Privilege Principle

Each IAM role has only the minimum permissions required for its function.