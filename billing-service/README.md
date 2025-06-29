# Warehousing Billing Service

A comprehensive billing microservice for warehousing operations, providing subscription management, invoice generation, payment processing, usage tracking, and financial reporting capabilities.

## Features

### Core Functionality
- **Billing Account Management**: Complete lifecycle management of warehouse partner billing accounts
- **Subscription Management**: Flexible subscription plans with multiple billing frequencies
- **Invoice Generation**: Automated invoice creation with support for various invoice types
- **Payment Processing**: Multi-method payment handling with auto-pay capabilities
- **Usage Tracking**: Detailed tracking of storage, transactions, and API usage
- **Multi-Currency Support**: Global operations with 50+ supported currencies
- **Tax Calculation**: Automated tax calculation and compliance frameworks

### Business Capabilities
- **Subscription Billing**: Monthly, quarterly, and annual billing cycles
- **Usage-Based Billing**: Storage, transaction, and API usage billing
- **Overage Management**: Automatic overage calculation and billing
- **Late Fee Processing**: Automated late fee application for overdue invoices
- **Credit Management**: Credit limits and outstanding balance tracking
- **Financial Reporting**: Comprehensive revenue and financial analytics

### Technical Features
- **RESTful API**: Complete REST API with OpenAPI documentation
- **Database Support**: PostgreSQL with Redis caching
- **Event-Driven Architecture**: Kafka integration for real-time events
- **Security**: OAuth2/JWT authentication and authorization
- **Monitoring**: Comprehensive metrics and health checks
- **Multi-Language**: Internationalization support for global operations

## Technology Stack

- **Java 17** with **Spring Boot 3.1**
- **PostgreSQL** (primary database)
- **Redis** (caching and sessions)
- **Apache Kafka** (event streaming)
- **Docker** & **Kubernetes** (containerization)
- **Maven** (build management)
- **OpenAPI/Swagger** (API documentation)

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+
- Docker (optional)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd billing-service
   ```

2. **Set up the database**
   ```bash
   # Create PostgreSQL database
   createdb warehousing_billing_db
   
   # Start Redis (using Docker)
   docker run -d --name redis -p 6379:6379 redis:7-alpine
   ```

3. **Configure environment variables**
   ```bash
   export DB_USERNAME=your_db_user
   export DB_PASSWORD=your_db_password
   export REDIS_HOST=localhost
   export REDIS_PORT=6379
   ```

4. **Build and run the application**
   ```bash
   # Build the application
   mvn clean compile package
   
   # Run the application
   mvn spring-boot:run
   
   # Or run the JAR
   java -jar target/billing-service-1.0.0-SNAPSHOT.jar
   ```

5. **Access the application**
   - API Base URL: `http://localhost:8081/api/v1/billing`
   - API Documentation: `http://localhost:8081/api/v1/billing/swagger-ui.html`
   - Health Check: `http://localhost:8081/api/v1/billing/actuator/health`

### Docker Setup

1. **Build Docker image**
   ```bash
   docker build -t billing-service:latest .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## API Documentation

### Key Endpoints

#### Billing Accounts
- `POST /accounts` - Create billing account
- `GET /accounts/{id}` - Get billing account
- `PUT /accounts/{id}` - Update billing account
- `GET /accounts/search` - Search billing accounts

#### Subscriptions
- `POST /subscriptions` - Create subscription
- `GET /subscriptions/{id}` - Get subscription
- `PUT /subscriptions/{id}/status` - Update subscription status

#### Invoices
- `POST /invoices` - Create invoice
- `GET /invoices/{id}` - Get invoice
- `POST /invoices/{id}/send` - Send invoice

#### Payments
- `POST /payments` - Record payment
- `GET /payments/{id}` - Get payment details

### Authentication

The service uses OAuth2/JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Database Schema

### Core Tables

- **billing_accounts**: Warehouse partner billing information
- **subscriptions**: Subscription plans and status
- **subscription_usage**: Usage tracking and metrics
- **invoices**: Invoice details and status
- **invoice_line_items**: Individual line items
- **payments**: Payment records and status

### Key Relationships

- BillingAccount → Subscriptions (1:N)
- BillingAccount → Invoices (1:N)
- Subscription → SubscriptionUsage (1:N)
- Invoice → InvoiceLineItems (1:N)
- Invoice → Payments (1:N)

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: warehousing-billing-service
  datasource:
    url: jdbc:postgresql://localhost:5432/warehousing_billing_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}

app:
  billing:
    currency:
      default: USD
      supported: USD,EUR,GBP,CAD,AUD...
    tax:
      default-rate: 0.20
    invoice:
      prefix: WH-INV
      auto-generate: true
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `warehousing_user` |
| `DB_PASSWORD` | Database password | `warehousing_pass` |
| `REDIS_HOST` | Redis hostname | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_ISSUER_URI` | JWT issuer URI | `http://localhost:8080/auth/realms/warehousing` |

## Development

### Running Tests

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run with coverage
mvn clean test jacoco:report
```

### Code Quality

```bash
# Run quality checks
mvn checkstyle:check pmd:check spotbugs:check

# Run security scan
mvn org.owasp:dependency-check-maven:check
```

### Database Migrations

The service uses Flyway for database migrations. Migration files are located in `src/main/resources/db/migration/`.

```bash
# Run migrations
mvn flyway:migrate

# Check migration status
mvn flyway:info
```

## Deployment

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: billing-service
spec:
  replicas: 3
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
        image: billing-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
```

### Health Checks

The service provides comprehensive health checks:

- `/actuator/health` - Application health
- `/actuator/health/db` - Database connectivity
- `/actuator/health/redis` - Redis connectivity
- `/actuator/metrics` - Application metrics

## Monitoring

### Metrics

The service exports metrics in Prometheus format:

- Custom business metrics (invoices, payments, subscriptions)
- JVM metrics (memory, GC, threads)
- HTTP metrics (requests, response times)
- Database metrics (connections, queries)

### Logging

Structured logging with configurable levels:

```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "level": "INFO",
  "logger": "com.ecosystem.warehousing.billing.service",
  "message": "Invoice generated successfully",
  "invoiceId": "uuid",
  "billingAccountId": "uuid"
}
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Write comprehensive unit tests (80%+ coverage)
- Use meaningful commit messages
- Update documentation for API changes

## Security

- OAuth2/JWT authentication required
- Input validation on all endpoints
- SQL injection prevention with parameterized queries
- Rate limiting on public endpoints
- Sensitive data encryption at rest

## Support

For support and questions:

- **Email**: billing-team@ecosystem.com
- **Documentation**: [API Documentation](http://localhost:8081/api/v1/billing/swagger-ui.html)
- **Issues**: Create an issue in the repository

## License

Proprietary License - See LICENSE file for details.

---

**Generated with [Memex](https://memex.tech)**  
Co-Authored-By: Memex <noreply@memex.tech>