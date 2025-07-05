# Moving and Delivery Coordination

## Overview
Moving and Delivery Coordination - Part of the Gogidix Customer Marketplace Services.

## Features
- Production-ready Spring Boot microservice
- Full observability with Actuator endpoints
- Docker and Kubernetes ready
- Integration with shared infrastructure services

## API Documentation
- Swagger UI: http://localhost:8088/moving-logistics/swagger-ui.html
- API Docs: http://localhost:8088/moving-logistics/api-docs

## Development
```bash
mvn spring-boot:run
```

## Docker
```bash
mvn clean package
docker build -t moving-logistics-service .
docker run -p 8088:8088 moving-logistics-service
```
