# Limitations & Future Improvements

## Current Limitations

### Infrastructure
- **Single EC2 instance** — no high availability or auto-scaling
- **t3.small** — limited resources, not suitable for production load
- **No HTTPS** — services exposed via HTTP only
- **Single AZ deployment** — RDS and EC2 in same AZ

### Application
- **Kafka removed** — replaced by SQS due to EC2 memory constraints
- **No service discovery** — service URLs hardcoded via environment variables