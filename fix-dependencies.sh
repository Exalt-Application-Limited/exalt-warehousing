#!/bin/bash

echo "🔧 Fixing common dependencies for all warehousing services..."

# Services to fix
SERVICES=(
    "config-server-enterprise"
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
    "warehousing-shared"
    "shared-infrastructure-test"
)

for service in "${SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        echo "🔧 Fixing dependencies for $service..."
        
        # Create backup
        cp "$service/pom.xml" "$service/pom.xml.backup"
        
        # Add missing dependencies after the existing Spring Cloud dependencies
        sed -i '/<\/groupId>/a\\n        <!-- Spring Cloud OpenFeign -->\n        <dependency>\n            <groupId>org.springframework.cloud</groupId>\n            <artifactId>spring-cloud-starter-openfeign</artifactId>\n        </dependency>\n        \n        <!-- Kafka -->\n        <dependency>\n            <groupId>org.springframework.kafka</groupId>\n            <artifactId>spring-kafka</artifactId>\n        </dependency>\n        \n        <!-- OpenAPI/Swagger -->\n        <dependency>\n            <groupId>org.springdoc</groupId>\n            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>\n        </dependency>\n        \n        <!-- Redis -->\n        <dependency>\n            <groupId>org.springframework.boot</groupId>\n            <artifactId>spring-boot-starter-data-redis</artifactId>\n        </dependency>' "$service/pom.xml"
        
        echo "   ✅ Dependencies added to $service"
    else
        echo "   ⚠️ Skipped $service (no pom.xml found)"
    fi
done

echo ""
echo "🎉 Dependency fixes completed!"
echo "Note: Config server specific dependencies need manual addition."