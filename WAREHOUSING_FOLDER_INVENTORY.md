# Warehousing Domain Folder Inventory
**Generated**: June 9, 2025  
**Path**: C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing

## Complete Folder Classification

| Folder Name | Type | Has pom.xml | Has package.json | Has Dockerfile | Classification |
|-------------|------|-------------|------------------|----------------|----------------|
| .git | Infrastructure | ❌ | ❌ | ❌ | Git Repository Metadata |
| .github | Infrastructure | ❌ | ❌ | ❌ | GitHub Configuration |
| .mvn | Infrastructure | ❌ | ❌ | ❌ | Maven Wrapper Files |
| DOC archive | Documentation | ❌ | ❌ | ❌ | Documentation Archive |
| Doc | Documentation | ❌ | ❌ | ❌ | Documentation |
| REMEDIATION-DOCS | Documentation | ❌ | ❌ | ❌ | Remediation Documentation |
| REMEDIATION-SCRIPTS | Support | ❌ | ❌ | ❌ | Remediation Scripts |
| **billing-service** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| build-test-results | Test Artifact | ❌ | ❌ | ❌ | Test Results |
| central-configuration-test | Java Service | ✅ | ❌ | ✅ | Test/Config Service |
| com.ecosystem | Support | ❌ | ❌ | ❌ | Legacy/Unused |
| compilation-results | Test Artifact | ❌ | ❌ | ❌ | Compilation Output |
| config-server-enterprise | Java Service | ✅ | ❌ | ✅ | Configuration Service |
| **cross-region-logistics-service** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| **fulfillment-service** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| **global-hq-admin** | **Frontend App** | ❌ | ✅ | ❌ | **React Frontend** |
| helm | Infrastructure | ❌ | ❌ | ❌ | Helm Charts |
| integration-tests | Java Service | ✅ | ❌ | ✅ | Integration Test Suite |
| **inventory-service** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| kubernetes | Infrastructure | ❌ | ❌ | ❌ | K8s Manifests |
| **regional-admin** | **Frontend App** | ❌ | ✅ | ❌ | **React Frontend** |
| **self-storage-service** | **Java Service** | ✅ | ✅* | ❌ | **Java Service + Frontend** |
| shared-infrastructure-test | Java Service | ✅ | ❌ | ✅ | Test Infrastructure |
| **staff-mobile-app** | **Frontend App** | ❌ | ✅ | ✅ | **React Native App** |
| test-results | Test Artifact | ❌ | ❌ | ❌ | Test Output |
| tools archives | Support | ❌ | ❌ | ❌ | Tool Archives |
| **warehouse-analytics** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| **warehouse-management-service** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| **warehouse-onboarding** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| **warehouse-operations** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| **warehouse-subscription** | **Java Service** | ✅ | ❌ | ✅ | **Java Microservice** |
| warehousing-production | Environment | ✅ | ❌ | ✅ | Production Config |
| **warehousing-shared** | **Library** | ✅ | ❌ | ❌ | **Shared Library** |
| warehousing-staging | Environment | ✅ | ❌ | ✅ | Staging Config |

## Summary

### Core Microservices (10)
1. billing-service
2. cross-region-logistics-service
3. fulfillment-service
4. inventory-service
5. self-storage-service
6. warehouse-analytics
7. warehouse-management-service
8. warehouse-onboarding
9. warehouse-operations
10. warehouse-subscription

### Frontend Applications (3)
1. global-hq-admin (React)
2. regional-admin (React)
3. staff-mobile-app (React Native)

### Shared Components (1)
1. warehousing-shared (Java Library)

### Test/Config Services (3)
1. central-configuration-test
2. config-server-enterprise
3. shared-infrastructure-test
4. integration-tests

### Environment Configs (2)
1. warehousing-production
2. warehousing-staging

### Infrastructure/Support (11)
- .git, .github, .mvn
- DOC archive, Doc, REMEDIATION-DOCS, REMEDIATION-SCRIPTS
- helm, kubernetes
- build-test-results, compilation-results, test-results
- com.ecosystem, tools archives

## Action Items
- Validate all 10 core microservices
- Check 3 frontend applications
- Verify shared library compatibility
- Assess test/config services necessity
- Review environment configurations

*Note: self-storage-service has both backend (pom.xml) and frontend components (package.json in subdirectories)*