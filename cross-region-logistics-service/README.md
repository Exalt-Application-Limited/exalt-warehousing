# Cross-Region Logistics Service

## Overview
The Cross-Region Logistics Service manages warehouse-to-warehouse transfers, shipping route optimization, and inter-warehouse logistics coordination across the warehousing ecosystem.

## Features
- Cross-warehouse transfer management
- Shipping route optimization
- Transfer workflow coordination
- Real-time transfer tracking
- Transfer analytics and reporting
- Integration with inventory and warehouse services

## Technology Stack
- Java 17
- Spring Boot 3.1.5
- PostgreSQL
- Redis (for caching)
- Apache Kafka (for event streaming)
- Spring Cloud (for microservices integration)

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL 15+
- Redis 7+

### Running Locally

1. **Clone the repository**
   ```bash
   cd warehousing/cross-region-logistics-service
   ```

2. **Start dependencies using Docker Compose**
   ```bash
   docker-compose up -d logistics-db logistics-redis
   ```

3. **Run the service**
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8087 by default.

### API Documentation
Once the service is running, access the API documentation at:
- Swagger UI: http://localhost:8087/swagger-ui.html
- OpenAPI Spec: http://localhost:8087/v3/api-docs

## API Endpoints

### Transfer Management
- `POST /api/v1/transfers` - Create transfer request
- `GET /api/v1/transfers/{id}` - Get transfer details
- `PUT /api/v1/transfers/{id}/approve` - Approve transfer
- `PUT /api/v1/transfers/{id}/reject` - Reject transfer
- `PUT /api/v1/transfers/{id}/status` - Update transfer status
- `GET /api/v1/transfers` - List transfers with filters

### Shipping Routes
- `GET /api/v1/shipping/routes` - Get available routes
- `POST /api/v1/shipping/routes/optimize` - Optimize shipping route
- `GET /api/v1/shipping/routes/estimate` - Estimate shipping time and cost

### Transfer Analytics
- `GET /api/v1/analytics/transfers/summary` - Transfer summary
- `GET /api/v1/analytics/transfers/by-warehouse` - Transfers by warehouse
- `GET /api/v1/analytics/transfers/performance` - Transfer performance metrics

### Workflow Management
- `POST /api/v1/workflow/transfers/{id}/initiate` - Initiate transfer workflow
- `GET /api/v1/workflow/transfers/{id}/status` - Get workflow status
- `POST /api/v1/workflow/transfers/{id}/complete` - Complete transfer

## Events Published
- `transfer.created` - When transfer is created
- `transfer.approved` - When transfer is approved
- `transfer.rejected` - When transfer is rejected
- `transfer.initiated` - When transfer shipment starts
- `transfer.in-transit` - When transfer is in transit
- `transfer.completed` - When transfer is completed
- `transfer.cancelled` - When transfer is cancelled

## Events Consumed
- `inventory.updated` - To track inventory changes
- `warehouse.capacity.changed` - To validate warehouse capacity

## Integration Points
- **Inventory Service** - For validating available inventory
- **Warehouse Management Service** - For warehouse capacity and location info
- **Shipping Carriers** - For freight shipping services

## Configuration
Key configuration properties:
- `logistics.transfer.approval.auto-approve-threshold` - Auto-approval threshold
- `logistics.shipping.route.optimization.algorithm` - Route optimization algorithm
- `logistics.transfer.validation.*` - Validation settings

## Building for Production
```bash
mvn clean package
docker build -t cross-region-logistics-service:latest .
```

## Testing
```bash
mvn test
```
