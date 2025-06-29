# Warehouse Onboarding Service

Comprehensive warehouse partner onboarding service with KYC verification, compliance checks, document validation, and automated approval workflows.

## Overview

The Warehouse Onboarding Service manages the complete lifecycle of warehouse partner applications, from initial submission through final approval and activation. It provides automated KYC verification, regulatory compliance checking, document management, and workflow-driven approval processes.

## Features

### Core Functionality
- ✅ **Partner Application Management** - Complete onboarding request lifecycle
- ✅ **KYC Verification** - Automated Know Your Customer verification
- ✅ **Document Management** - Secure document upload, validation, and storage
- ✅ **Compliance Checking** - Regulatory and industry-specific compliance verification
- ✅ **Workflow Engine** - Activiti-based approval workflows
- ✅ **Status Tracking** - Real-time status updates and notifications

### Business Capabilities
- ✅ **Multi-Business Type Support** - Corporations, LLCs, Partnerships, etc.
- ✅ **Storage Type Management** - Ambient, refrigerated, frozen, specialized storage
- ✅ **Service Capability Tracking** - Warehousing, fulfillment, value-added services
- ✅ **Financial Assessment** - Pricing validation and payment terms management
- ✅ **Geographic Coverage** - Global partner onboarding with local compliance

### Technical Features
- ✅ **RESTful API** - Comprehensive REST API with OpenAPI documentation
- ✅ **Event-Driven Architecture** - Kafka integration for real-time events
- ✅ **Secure Document Storage** - AWS S3 integration with encryption
- ✅ **Caching** - Redis-based caching for performance optimization
- ✅ **Monitoring** - Health checks, metrics, and observability

## Technology Stack

- **Framework**: Spring Boot 3.1
- **Language**: Java 17
- **Database**: PostgreSQL with Flyway migrations
- **Caching**: Redis
- **Messaging**: Apache Kafka
- **Workflow**: Activiti 7
- **Security**: OAuth2/JWT
- **Documentation**: OpenAPI/Swagger
- **Containerization**: Docker & Docker Compose

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 15+ (for local development)
- Redis 7+ (for caching)

### Local Development Setup

1. **Clone and Navigate**
   ```bash
   cd warehouse-onboarding
   ```

2. **Build the Service**
   ```bash
   ./test-build.bat  # Windows
   # or
   mvn clean package
   ```

3. **Start Infrastructure**
   ```bash
   docker-compose up -d warehouse-onboarding-db warehouse-onboarding-redis
   ```

4. **Run the Service**
   ```bash
   java -jar target/warehouse-onboarding-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
   ```

5. **Access the Service**
   - API Documentation: http://localhost:8085/warehouse-onboarding/swagger-ui.html
   - Health Check: http://localhost:8085/warehouse-onboarding/actuator/health
   - API Base: http://localhost:8085/warehouse-onboarding/api/v1/warehousing/onboarding

### Full Docker Deployment

```bash
docker-compose up -d
```

This starts:
- PostgreSQL database
- Redis cache
- Kafka messaging
- MailHog (email testing)
- MinIO (S3-compatible storage)
- Warehouse Onboarding Service

## API Endpoints

### Core Onboarding Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/requests` | Submit new onboarding request |
| GET | `/requests/{id}` | Get onboarding request details |
| GET | `/requests` | List requests with filtering |
| POST | `/requests/{id}/approve` | Approve onboarding request |
| POST | `/requests/{id}/reject` | Reject onboarding request |
| POST | `/requests/{id}/suspend` | Suspend onboarding request |
| POST | `/requests/{id}/resume` | Resume suspended request |

### KYC and Compliance

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/requests/{id}/kyc/start` | Start KYC verification |
| POST | `/requests/{id}/compliance/start` | Start compliance checking |
| GET | `/requests/attention-required` | Get requests needing attention |

### Validation and Statistics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/validate/business-registration` | Validate business registration uniqueness |
| GET | `/validate/tax-identification` | Validate tax ID uniqueness |
| GET | `/validate/contact-email` | Validate email uniqueness |
| GET | `/statistics` | Get onboarding statistics |

## Data Models

### Core Entities

#### PartnerOnboardingRequest
- Company information (name, registration, tax ID)
- Contact details (primary contact, address)
- Warehouse capabilities (storage types, capacity)
- Service capabilities (fulfillment, value-added services)
- Financial information (pricing, payment terms)
- Status tracking (onboarding status, KYC status)

#### OnboardingDocument
- Document metadata (type, filename, size)
- Storage information (S3 location, hash)
- Verification status and results
- Expiry tracking for time-sensitive documents

#### KYCVerification
- Verification type and provider
- Confidence scores and risk assessment
- External verification IDs
- Manual review flags

#### ComplianceCheck
- Compliance type and jurisdiction
- Regulatory body and license information
- Risk rating and compliance status
- Remedial actions and requirements

## Business Rules

### Onboarding Status Flow
1. **SUBMITTED** → Initial application received
2. **UNDER_REVIEW** → Application being reviewed
3. **DOCUMENT_VERIFICATION** → Documents being validated
4. **KYC_IN_PROGRESS** → KYC verification running
5. **COMPLIANCE_CHECK** → Regulatory compliance being verified
6. **FACILITY_INSPECTION_REQUIRED** → Physical inspection needed
7. **PENDING_APPROVAL** → Final management approval
8. **APPROVED** → Partner onboarded successfully

### Validation Rules
- Business registration numbers must be unique
- Tax identification numbers must be unique
- Contact emails must be unique
- Available storage capacity cannot exceed total capacity
- Incorporation date cannot be in the future

### Document Requirements
- **Required Documents**: Business registration, tax ID, bank statements, facility photos
- **Optional Documents**: ISO certifications, insurance certificates, references
- **File Types**: PDF, JPG, PNG, DOC, DOCX (up to 10MB)
- **Retention**: 7 years for compliance

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | warehouse_onboarding |
| `REDIS_HOST` | Redis host | localhost |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka servers | localhost:9092 |
| `AWS_S3_BUCKET` | S3 bucket for documents | warehouse-onboarding-documents |
| `KYC_PROVIDER` | KYC service provider | mock |
| `AUTO_APPROVE_ENABLED` | Enable auto-approval | false |

### Profiles

- **dev**: Development with relaxed security
- **test**: Testing with H2 in-memory database
- **prod**: Production with full security
- **docker**: Docker deployment configuration

## Security

### Authentication & Authorization
- OAuth2/JWT integration with ecosystem auth service
- Role-based access control (RBAC)
- API key authentication for external integrations

### Data Protection
- Document encryption at rest (S3)
- Database encryption for sensitive fields
- PII data handling compliance (GDPR)
- Audit trails for all operations

### Compliance Standards
- SOC 2 Type II compliance framework
- ISO 27001 security standards
- PCI DSS for payment data (where applicable)
- GDPR for data protection

## Monitoring & Observability

### Health Checks
- Application health: `/actuator/health`
- Database connectivity
- Redis connectivity
- External service dependencies

### Metrics
- Prometheus metrics: `/actuator/prometheus`
- Custom business metrics (approval rates, processing times)
- Performance metrics (response times, throughput)

### Logging
- Structured JSON logging
- Request/response correlation IDs
- Security event logging
- Performance profiling

## Development

### Code Structure
```
src/main/java/com/ecosystem/warehousing/onboarding/
├── controller/          # REST controllers
├── service/            # Business logic
├── model/              # Domain entities
├── repository/         # Data access
├── dto/                # Data transfer objects
├── config/             # Configuration classes
└── exception/          # Exception handling
```

### Testing Strategy
- Unit tests for business logic
- Integration tests for API endpoints
- Contract tests for external dependencies
- Load tests for performance validation

### Contributing
1. Create feature branch from `main`
2. Implement changes with tests
3. Ensure code coverage > 80%
4. Submit pull request with documentation

## Deployment

### Production Deployment
1. Build Docker image: `docker build -t warehouse-onboarding:latest .`
2. Push to registry: `docker push registry/warehouse-onboarding:latest`
3. Deploy with Kubernetes manifests or Docker Compose
4. Configure external dependencies (database, Redis, Kafka)
5. Set up monitoring and alerting

### Scaling Considerations
- Stateless design for horizontal scaling
- Database connection pooling
- Redis clustering for cache scaling
- Kafka partitioning for message throughput

## Support

### Documentation
- API Documentation: Available at `/swagger-ui.html`
- Technical Documentation: See `/docs` directory
- Runbooks: Available in operations repository

### Troubleshooting
- Check application logs: `docker-compose logs warehouse-onboarding-service`
- Verify database connectivity
- Check Redis cache status
- Validate external service configurations

### Monitoring
- Health dashboard: Monitor service health and dependencies
- Business metrics: Track onboarding volumes and approval rates
- Alert management: Set up alerts for critical failures

---

## Version History

- **v1.0.0** - Initial release with core onboarding functionality
- **v1.1.0** - Enhanced KYC integration and compliance checking
- **v1.2.0** - Advanced workflow engine and automation features

---

*Generated with [Memex](https://memex.tech)*
*Co-Authored-By: Memex <noreply@memex.tech>*