# Customer Storage Marketplace Service

## Overview

The Customer Storage Marketplace Service is a B2C microservice that enables customers to discover, compare, and book storage facilities for personal and business use. This service provides a comprehensive marketplace platform for storage solutions.

## Features

### Core Functionality
- **Storage Facility Discovery**: Search and browse available storage facilities
- **Location-based Search**: Find facilities within specified radius using GPS coordinates
- **Advanced Filtering**: Filter by price, amenities, facility type, ratings, and availability
- **Facility Comparison**: Compare multiple facilities side-by-side
- **Real-time Availability**: Check unit availability in real-time
- **Pricing Information**: Transparent pricing with no hidden fees

### Search Capabilities
- Text-based search across facility names and descriptions
- Geographic search with radius filtering
- Multi-criteria filtering (price range, amenities, ratings)
- Sorting by relevance, price, rating, and distance
- Pagination for large result sets

### Business Features
- Customer reviews and ratings system
- Facility image galleries
- Detailed facility and unit specifications
- Operating hours and contact information
- Amenity and security feature listings

## Technical Architecture

### Technology Stack
- **Framework**: Spring Boot 3.1.5
- **Database**: PostgreSQL
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Mockito, TestContainers

### API Endpoints

#### Facility Search
```
GET /api/v1/marketplace/facilities/search
- Search facilities with multiple filters
- Supports pagination and sorting

GET /api/v1/marketplace/facilities/nearby
- Find facilities within geographic radius
- Requires latitude/longitude coordinates

GET /api/v1/marketplace/facilities/{facilityId}
- Get detailed facility information
- Includes units, amenities, and reviews

GET /api/v1/marketplace/facilities/code/{facilityCode}
- Retrieve facility by unique code
- Alternative facility lookup method
```

#### Location-based Queries
```
GET /api/v1/marketplace/facilities/location
- Find facilities by city and state
- Includes pagination support

GET /api/v1/marketplace/facilities/zip/{zipCode}
- Find facilities by ZIP code
- Returns all facilities in ZIP area
```

#### Specialized Searches
```
GET /api/v1/marketplace/facilities/top-rated
- Get highest-rated facilities
- Configurable minimum review threshold

GET /api/v1/marketplace/facilities/price-range
- Find facilities within price range
- Supports min/max price filtering

GET /api/v1/marketplace/facilities/amenities
- Find facilities with specific amenities
- Supports multiple amenity filtering
```

## Data Model

### Core Entities

#### StorageFacility
- Facility information (name, location, contact)
- Operating details (hours, capacity, availability)
- Pricing and business model
- Ratings and review aggregation
- Amenities and security features

#### StorageUnit
- Individual unit specifications (size, features)
- Location within facility
- Pricing and availability
- Maintenance and condition tracking

#### UnitReservation
- Customer booking information
- Reservation status and payment tracking
- Access credentials and special requirements

#### Reviews and Images
- Customer reviews with ratings
- Facility and unit image galleries
- Verification and moderation status

## Configuration

### Environment Variables
```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=customer_storage_marketplace
DB_USERNAME=postgres
DB_PASSWORD=password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka Configuration
KAFKA_SERVERS=localhost:9092

# Service Discovery
EUREKA_URL=http://localhost:8761/eureka/

# Security
JWT_SECRET=your-secret-key
CORS_ORIGINS=http://localhost:3000,http://localhost:3001
```

### Application Properties
```yaml
server:
  port: 8083
  servlet:
    context-path: /customer-storage-marketplace

app:
  marketplace:
    search:
      max-radius-km: 100
      default-radius-km: 25
      max-results-per-page: 100
```

## Development

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+
- Docker (optional)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd customer-storage-marketplace-service
   ```

2. **Configure database**
   ```bash
   createdb customer_storage_marketplace
   ```

3. **Start supporting services**
   ```bash
   # Start Redis
   redis-server
   
   # Start Kafka (if using local setup)
   kafka-server-start.sh config/server.properties
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access API documentation**
   - Swagger UI: http://localhost:8083/customer-storage-marketplace/swagger-ui.html
   - API Docs: http://localhost:8083/customer-storage-marketplace/api-docs

### Testing

#### Unit Tests
```bash
mvn test
```

#### Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

#### Test Coverage
```bash
mvn jacoco:report
```

### Docker Deployment

#### Build Docker Image
```bash
mvn clean package
docker build -t customer-storage-marketplace-service .
```

#### Run Container
```bash
docker run -p 8083:8083 \
  -e DB_HOST=host.docker.internal \
  -e REDIS_HOST=host.docker.internal \
  customer-storage-marketplace-service
```

#### Docker Compose
```yaml
version: '3.8'
services:
  marketplace-service:
    build: .
    ports:
      - "8083:8083"
    environment:
      - DB_HOST=postgres
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
```

## API Usage Examples

### Search Facilities
```bash
# Basic search
curl "http://localhost:8083/customer-storage-marketplace/api/v1/marketplace/facilities/search?city=Atlanta&state=GA"

# Advanced search with filters
curl "http://localhost:8083/customer-storage-marketplace/api/v1/marketplace/facilities/search?city=Atlanta&minPrice=50&maxPrice=150&hasAvailableUnits=true"

# Location-based search
curl "http://localhost:8083/customer-storage-marketplace/api/v1/marketplace/facilities/nearby?latitude=33.7490&longitude=-84.3880&radiusKm=25"
```

### Get Facility Details
```bash
# By facility ID
curl "http://localhost:8083/customer-storage-marketplace/api/v1/marketplace/facilities/123"

# By facility code
curl "http://localhost:8083/customer-storage-marketplace/api/v1/marketplace/facilities/code/FAC001"
```

## Monitoring and Health

### Health Checks
- Application health: `/actuator/health`
- Database connectivity: `/actuator/health/db`
- Redis connectivity: `/actuator/health/redis`

### Metrics
- Prometheus metrics: `/actuator/prometheus`
- Application metrics: `/actuator/metrics`

### Logging
- Structured JSON logging
- Configurable log levels
- Request/response tracing

## Security

### Authentication
- JWT-based authentication
- Integration with shared authentication service
- Role-based access control

### Data Protection
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CORS configuration

## Performance

### Caching Strategy
- Facility details cache (5 minutes TTL)
- Search results cache (3 minutes TTL)
- Popular facilities cache (10 minutes TTL)

### Database Optimization
- Indexed location columns for geographic queries
- Composite indexes for common filter combinations
- Connection pooling and query optimization

### Scalability
- Stateless service design
- Horizontal scaling support
- Load balancer ready

## Contributing

### Code Style
- Follow Java coding conventions
- Use Lombok for boilerplate reduction
- Comprehensive unit test coverage
- Clear documentation and comments

### Development Workflow
1. Create feature branch
2. Implement changes with tests
3. Run full test suite
4. Submit pull request
5. Code review and merge

## Support

### Documentation
- API documentation via Swagger UI
- Architecture decision records
- Deployment guides

### Troubleshooting
- Check application logs
- Verify database connectivity
- Confirm service registration in Eureka
- Review cache performance

## License

Proprietary - Gogidix Application Limited