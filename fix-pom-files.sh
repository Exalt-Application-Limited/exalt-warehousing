#!/bin/bash

echo "🔧 Fixing POM Files..."
echo ""

# Function to fix a service POM
fix_service_pom() {
    local service=$1
    local pom_file="$service/pom.xml"
    
    if [ -f "$pom_file" ]; then
        echo "Fixing $service..."
        
        # Create a temporary file
        cp "$pom_file" "$pom_file.broken"
        
        # Restore proper Maven coordinates structure
        cat > "$pom_file" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.exalt.warehousing</groupId>
        <artifactId>warehousing-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>$service</artifactId>
    <packaging>jar</packaging>
    <name>Warehousing ${service^} Service</name>
    <description>Warehousing ${service} microservice</description>

    <dependencies>
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF
        echo "✅ Fixed $service"
    fi
}

# Fix all Java services
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

for service in "${JAVA_SERVICES[@]}"; do
    fix_service_pom "$service"
done

echo ""
echo "✅ POM files fixed!"