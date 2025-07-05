# Storage Insurance and Protection Plans

## Overview
Storage Insurance and Protection Plans - Part of the Gogidix Customer Marketplace Services.

## Features
- Production-ready Spring Boot microservice
- Full observability with Actuator endpoints
- Docker and Kubernetes ready
- Integration with shared infrastructure services

## API Documentation
- Swagger UI: http://localhost:8087/insurance-protection/swagger-ui.html
- API Docs: http://localhost:8087/insurance-protection/api-docs

## Development
```bash
mvn spring-boot:run
```

## Docker
```bash
mvn clean package
docker build -t insurance-protection-service .
docker run -p 8087:8087 insurance-protection-service
```
