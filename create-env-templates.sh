#!/bin/bash

echo "📝 Creating .env.template files for all services..."
echo ""

# Java services
JAVA_SERVICES=(
    "billing-service"
    "config-server-enterprise"
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "self-storage-service"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
    "warehousing-production"
    "warehousing-shared"
    "warehousing-staging"
    "shared-infrastructure-test"
)

# Create Java service env template
create_java_env_template() {
    local service=$1
    
    if [ -d "$service" ]; then
        cat > "$service/.env.template" << 'EOF'
# Application Configuration
SPRING_APPLICATION_NAME=${service}
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Eureka Configuration
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/
EUREKA_INSTANCE_PREFER_IP_ADDRESS=true

# Config Server
SPRING_CLOUD_CONFIG_URI=http://localhost:8888
SPRING_CLOUD_CONFIG_FAIL_FAST=true

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/warehousing_${service}
SPRING_DATASOURCE_USERNAME=warehousing_user
SPRING_DATASOURCE_PASSWORD=
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SPRING_KAFKA_CONSUMER_GROUP_ID=${service}-group
SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET=earliest

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=

# Security
JWT_SECRET=
JWT_EXPIRATION=86400000

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_EXALT_WAREHOUSING=DEBUG

# Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always

# External APIs
EXTERNAL_API_KEY=
EXTERNAL_API_URL=

# AWS Configuration (if needed)
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
EOF
        
        # Replace ${service} with actual service name
        sed -i "s/\${service}/$service/g" "$service/.env.template"
        
        echo "✅ Created .env.template for $service"
    fi
}

# Create env templates for all Java services
for service in "${JAVA_SERVICES[@]}"; do
    create_java_env_template "$service"
done

# Special cases - config-server-enterprise
if [ -d "config-server-enterprise" ]; then
    cat > "config-server-enterprise/.env.template" << 'EOF'
# Config Server Configuration
SPRING_APPLICATION_NAME=config-server-enterprise
SERVER_PORT=8888
SPRING_PROFILES_ACTIVE=native,vault

# Git Repository Configuration
SPRING_CLOUD_CONFIG_SERVER_GIT_URI=
SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME=
SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD=
SPRING_CLOUD_CONFIG_SERVER_GIT_CLONE_ON_START=true

# Native File System (for local testing)
SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS=classpath:/config,file:./config

# Vault Configuration
SPRING_CLOUD_CONFIG_SERVER_VAULT_HOST=localhost
SPRING_CLOUD_CONFIG_SERVER_VAULT_PORT=8200
SPRING_CLOUD_CONFIG_SERVER_VAULT_TOKEN=

# Security
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=

# Eureka Configuration
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/
EUREKA_INSTANCE_PREFER_IP_ADDRESS=true

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CLOUD_CONFIG=DEBUG
EOF
    echo "✅ Updated .env.template for config-server-enterprise"
fi

# Create .gitignore if it doesn't exist
echo ""
echo "🔧 Ensuring .gitignore files exist..."
for service in "${JAVA_SERVICES[@]}"; do
    if [ -d "$service" ] && [ ! -f "$service/.gitignore" ]; then
        cat > "$service/.gitignore" << 'EOF'
# Compiled output
target/
*.class
*.jar
*.war
*.ear

# IDE files
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# Environment files
.env
.env.local
.env.*.local

# Logs
logs/
*.log

# OS files
.DS_Store
Thumbs.db

# Maven
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# Test reports
surefire-reports/
failsafe-reports/

# Build files
dependency-reduced-pom.xml
EOF
        echo "✅ Created .gitignore for $service"
    fi
done

echo ""
echo "✅ Environment templates creation completed!"
echo ""
echo "📊 Summary:"
echo "- Created .env.template for all Java services"
echo "- Updated config-server-enterprise with specific configuration"
echo "- Ensured .gitignore files exist"
echo ""
echo "⚠️  Note: Copy .env.template to .env and fill in the required values before running services"