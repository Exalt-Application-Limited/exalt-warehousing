# Billing Service - Setup Guide

## Overview
The Billing Service is responsible for managing financial transactions, invoicing, and payment processing within the warehousing ecosystem. This guide provides comprehensive instructions for setting up the service in development, testing, and production environments.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Configuration](#configuration)
4. [Database Setup](#database-setup)
5. [Service Dependencies](#service-dependencies)
6. [Testing](#testing)
7. [Deployment](#deployment)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements
- **Operating System**: Linux/Unix, Windows 10+, or macOS 10.15+
- **Java**: JDK 17 or later
- **Maven**: 3.8.4 or later
- **Docker**: 20.10.8 or later
- **Docker Compose**: 1.29.2 or later
- **Database**: PostgreSQL 13 or MySQL 8.0

### Required Tools
- Git
- cURL or Postman for API testing
- jq for JSON processing (optional but recommended)

## Local Development Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/warehousing.git
cd warehousing/billing-service
```

### 2. Build the Application
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t billing-service:latest .
```

### 3. Run with Docker Compose
```bash
# Start the service with dependencies
docker-compose -f ../docker-compose.yml up -d billing-service

# Verify the service is running
curl http://localhost:8200/actuator/health | jq
```

## Configuration

### Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SERVER_PORT` | No | 8200 | Service port |
| `SPRING_DATASOURCE_URL` | Yes | - | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | Yes | - | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Yes | - | Database password |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | Yes | - | Eureka server URL |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Yes | - | Kafka bootstrap servers |
| `SPRING_REDIS_HOST` | Yes | - | Redis host |
| `SPRING_REDIS_PORT` | No | 6379 | Redis port |

### Configuration Files

1. **application.yml** - Main configuration file
2. **bootstrap.yml** - Spring Cloud configuration
3. **logback-spring.xml** - Logging configuration

## Database Setup

### Schema Initialization
```sql
-- Create database
CREATE DATABASE billing_service;

-- Create user and grant privileges
CREATE USER 'billing_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON billing_service.* TO 'billing_user'@'%';
FLUSH PRIVILEGES;
```

### Flyway Migrations
```bash
# Run migrations manually
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/billing_service \
  -Dflyway.user=db_user -Dflyway.password=db_password
```

## Service Dependencies

The billing service depends on the following services:

1. **Eureka Server** - For service discovery
2. **Config Server** - For centralized configuration
3. **Kafka** - For event streaming
4. **Redis** - For caching and rate limiting
5. **PostgreSQL/MySQL** - For data persistence

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify -Pintegration
```

### API Testing
```bash
# Get service health
curl http://localhost:8200/actuator/health | jq

# Get API documentation
curl http://localhost:8200/v3/api-docs | jq
```

## Deployment

### Docker Deployment
```bash
docker run -d --name billing-service \
  -p 8200:8200 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/billing_service \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  billing-service:latest
```

### Kubernetes Deployment
```yaml
# Example deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: billing-service
  labels:
    app: billing-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: billing-service
  template:
    metadata:
      labels:
        app: billing-service
    spec:
      containers:
      - name: billing-service
        image: your-registry/billing-service:latest
        ports:
        - containerPort: 8200
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: billing-db-secret
              key: url
        resources:
          limits:
            cpu: "1"
            memory: "1Gi"
          requests:
            cpu: "500m"
            memory: "512Mi"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8200
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8200
          initialDelaySeconds: 30
          periodSeconds: 10
```

## Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database connection
psql -h localhost -U postgres -d billing_service -c "SELECT 1"

# Check database logs
docker logs <postgres_container_id>
```

#### Service Not Starting
```bash
# Check application logs
docker logs <billing_service_container_id>

# Check for port conflicts
netstat -tuln | grep 8200
lsof -i :8200
```

#### Kafka Connection Issues
```bash
# Check Kafka status
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check Kafka logs
docker-compose logs kafka
```

### Logs

#### View Logs
```bash
# View application logs
docker-compose logs -f billing-service

# View logs with timestamps
docker-compose logs -f --tail=100 billing-service | while read line; do echo "$(date '+%Y-%m-%d %H:%M:%S') $line"; done
```

#### Log Levels
```bash
# Change log level at runtime
curl -X POST "http://localhost:8200/actuator/loggers/com.warehousing.billing" \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'
```

### Performance Tuning

#### JVM Options
```bash
# Recommended JVM options for production
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=70"
```

#### Database Tuning
```sql
-- PostgreSQL configuration
ALTER SYSTEM SET shared_buffers = '1GB';
ALTER SYSTEM SET effective_cache_size = '3GB';
ALTER SYSTEM SET maintenance_work_mem = '256MB';
ALTER SYSTEM SET work_mem = '32MB';
```

## Next Steps

1. [Configure Monitoring](./operations/MONITORING.md)
2. [Set Up Alerts](./operations/ALERTS.md)
3. [Review Security Guidelines](./operations/SECURITY.md)
