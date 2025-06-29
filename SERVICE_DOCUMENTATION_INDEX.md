# Warehousing Domain - Service Documentation Index

## Overview
This index provides quick access to detailed documentation for each of the 20 services in the warehousing domain. Each service has comprehensive documentation covering business logic, technical architecture, API specifications, and integration patterns.

## Core Business Services (7)

### 1. [Billing Service](./billing-service/README.md) - Port 8200
**Business Focus**: Financial management and invoicing for warehouse operations
- Multi-tier subscription management
- Usage-based billing and invoicing
- Payment processing and financial reporting
- **Key APIs**: `/api/v1/billing/*`
- **Database**: PostgreSQL (billing data), Redis (caching)
- **Integration**: Social-Commerce, Payment Gateways, Tax Services

### 2. [Cross-Region Logistics Service](./cross-region-logistics-service/README.md) - Port 8201
**Business Focus**: Multi-region warehouse coordination and optimization
- Cross-region inventory balancing
- Inter-warehouse transfer coordination
- Regional compliance management
- **Key APIs**: `/api/v1/cross-region/*`
- **Database**: PostgreSQL (regional data), Redis (distributed cache)
- **Integration**: Inventory Service, Courier Services, Customs APIs

### 3. [Fulfillment Service](./fulfillment-service/README.md) - Port 8202
**Business Focus**: End-to-end order processing and fulfillment
- Complete order lifecycle management
- Picking and packing optimization
- Quality control integration
- **Key APIs**: `/api/v1/fulfillment/*`
- **Database**: PostgreSQL (orders), MongoDB (tracking)
- **Integration**: Social-Commerce, Inventory, Courier Services

### 4. [Inventory Service](./inventory-service/README.md) - Port 8203
**Business Focus**: Comprehensive inventory management and tracking
- Multi-location inventory tracking
- Real-time stock reservations
- Automated replenishment
- **Key APIs**: `/api/v1/inventory/*`
- **Database**: PostgreSQL (inventory), Elasticsearch (search)
- **Integration**: Fulfillment, Warehouse Management, Suppliers

### 5. [Self-Storage Service](./self-storage-service/README.md) - Port 8204
**Business Focus**: Vendor self-storage management and coordination
- Vendor space allocation
- Self-service inventory management
- Usage-based billing integration
- **Key APIs**: `/api/v1/self-storage/*`
- **Database**: PostgreSQL (storage data), MinIO (documents)
- **Integration**: Billing Service, Inventory Service, Vendor Portal

### 6. [Warehouse Analytics](./warehouse-analytics/README.md) - Port 8205
**Business Focus**: Business intelligence and operational analytics
- Real-time operational dashboards
- Predictive analytics and forecasting
- Performance metrics and reporting
- **Key APIs**: `/api/v1/analytics/*`
- **Database**: ClickHouse (time-series), PostgreSQL (analytics)
- **Integration**: All Services (data collection), BI Tools

### 7. [Warehouse Management Service](./warehouse-management-service/README.md) - Port 8206
**Business Focus**: Operational management and coordination
- Real-time warehouse oversight
- Task management and coordination
- Resource allocation optimization
- **Key APIs**: `/api/v1/warehouse-mgmt/*`
- **Database**: PostgreSQL (operational), Redis (real-time)
- **Integration**: Fulfillment, Staff Mobile App, Operations

## Management Services (3)

### 8. [Warehouse Onboarding](./warehouse-onboarding/README.md) - Port 8207
**Business Focus**: Partner registration and KYC compliance
- Multi-step partner onboarding
- KYC/AML compliance verification
- Document and contract management
- **Key APIs**: `/api/v1/onboarding/*`
- **Database**: PostgreSQL (partners), MinIO (documents)
- **Integration**: KYC Providers, Legal Systems, Billing Service

### 9. [Warehouse Operations](./warehouse-operations/README.md) - Port 8208
**Business Focus**: Daily operational management and coordination
- Operations planning and scheduling
- Staff and equipment management
- Safety and compliance monitoring
- **Key APIs**: `/api/v1/operations/*`
- **Database**: PostgreSQL (operations), Redis (coordination)
- **Integration**: Staff Mobile App, HR Systems, Maintenance

### 10. [Warehouse Subscription](./warehouse-subscription/README.md) - Port 8209
**Business Focus**: Service plans and subscription management
- Multi-tier subscription plans
- Feature access control
- Usage monitoring and billing
- **Key APIs**: `/api/v1/subscriptions/*`
- **Database**: PostgreSQL (subscriptions), Redis (access control)
- **Integration**: Billing Service, Identity Service, Customer Portal

## Support Services (4)

### 11. [Config Server Enterprise](./config-server-enterprise/README.md) - Port 8210
**Business Focus**: Centralized configuration management
- Environment-specific configurations
- Secure secret management
- Dynamic configuration updates
- **Key APIs**: `/api/v1/config/*`
- **Storage**: Git Repository, HashiCorp Vault
- **Integration**: All Services (configuration), CI/CD Pipeline

### 12. [Integration Tests](./integration-tests/README.md) - Port 8211
**Business Focus**: End-to-end testing and validation
- Cross-service integration testing
- API contract validation
- Performance and load testing
- **Key APIs**: `/api/v1/integration-tests/*`
- **Tools**: TestContainers, REST Assured, JMeter
- **Integration**: All Services (testing), CI/CD Pipeline

### 13. [Shared Infrastructure Test](./shared-infrastructure-test/README.md) - Port 8212
**Business Focus**: Infrastructure validation and testing
- Component health validation
- Performance benchmarking
- Disaster recovery testing
- **Key APIs**: `/api/v1/infra-test/*`
- **Tools**: Docker, JMeter, Prometheus
- **Integration**: All Infrastructure, Monitoring Systems

### 14. [Central Configuration Test](./central-configuration-test/README.md) - Port 8213
**Business Focus**: Configuration testing and validation
- Configuration syntax validation
- Environment-specific testing
- Security compliance checking
- **Key APIs**: `/api/v1/config-test/*`
- **Tools**: YAML validators, Security scanners
- **Integration**: Config Server, All Services, CI/CD Pipeline

## Frontend Applications (3)

### 15. [Global HQ Admin](./global-hq-admin/README.md) - Port 3200
**Business Focus**: Executive dashboard and global oversight
- Global operations overview
- Strategic analytics and reporting
- Executive decision support
- **Technology**: React 18 + TypeScript
- **UI Framework**: Material-UI
- **Integration**: Analytics APIs, Financial Services

### 16. [Regional Admin](./regional-admin/README.md) - Port 3201
**Business Focus**: Regional management and operations
- Regional operations dashboard
- Staff and resource management
- Local compliance monitoring
- **Technology**: Vue.js 3 + TypeScript
- **UI Framework**: Vuetify
- **Integration**: Operations APIs, Staff Management

### 17. [Staff Mobile App](./staff-mobile-app/README.md) - Mobile
**Business Focus**: Field worker task management
- Mobile task interface
- Inventory operations
- Quality control and safety
- **Technology**: React Native + TypeScript
- **Features**: Barcode scanning, GPS, Push notifications
- **Integration**: Fulfillment, Operations, Quality Systems

## Environment Services (3)

### 18. [Warehousing Production](./warehousing-production/README.md) - Port 8214
**Business Focus**: Production environment management
- Live system deployment
- Production monitoring
- Scaling and maintenance
- **Key APIs**: `/api/v1/production/*`
- **Tools**: Kubernetes, Prometheus, Terraform
- **Integration**: All Services (production), Infrastructure

### 19. [Warehousing Shared](./warehousing-shared/README.md) - Libraries
**Business Focus**: Common utilities and shared components
- Standardized data models
- Common utility libraries
- Integration patterns
- **Distribution**: Maven libraries
- **Components**: Security, Utilities, Integration clients
- **Integration**: All Services (dependency)

### 20. [Warehousing Staging](./warehousing-staging/README.md) - Port 8215
**Business Focus**: Pre-production testing and validation
- Staging environment management
- Deployment validation
- Integration and performance testing
- **Key APIs**: `/api/v1/staging/*`
- **Tools**: Docker, Kubernetes, TestContainers
- **Integration**: All Services (staging), CI/CD Pipeline

## Quick Reference

### Service Categories by Port Range
- **8200-8206**: Core Business Services
- **8207-8209**: Management Services  
- **8210-8213**: Support Services
- **3200-3201**: Frontend Web Applications
- **8214-8215**: Environment Services
- **Mobile**: Mobile Applications
- **Libraries**: Shared Components

### Technology Stack Summary
- **Backend**: Java 17 + Spring Boot 3.1 + Maven
- **Frontend**: React 18, Vue.js 3, React Native
- **Databases**: PostgreSQL, Redis, MongoDB, ClickHouse
- **Messaging**: Apache Kafka
- **Containerization**: Docker + Kubernetes
- **Monitoring**: Prometheus + Grafana + ELK Stack

### Integration Patterns
- **Synchronous**: HTTP/REST, gRPC
- **Asynchronous**: Apache Kafka, Event-driven
- **Security**: OAuth2/JWT, RBAC, mTLS
- **Data**: Event Sourcing, CQRS, Saga patterns

### Development Standards
- **API Design**: RESTful, OpenAPI 3.0
- **Code Quality**: SonarQube, Security scanning
- **Testing**: Unit, Integration, E2E, Performance
- **Documentation**: Comprehensive, Up-to-date

---

## Navigation
- [Comprehensive Domain Documentation](./COMPREHENSIVE_WAREHOUSING_DOCUMENTATION.md)
- [Individual Service Documentation](#service-links-above)
- [Integration Architecture](#integration-patterns)
- [Deployment Guides](./docs/deployment/)
- [API Reference](./docs/api/)

---

**Last Updated**: June 2025  
**Documentation Version**: 1.0.0  
**Domain**: Warehousing  
**Services Count**: 20