#!/bin/bash

echo "🔧 Standardizing Java Services Build Configuration..."
echo "Target: Java 17, Spring Boot 3.1.5, Maven 3.8.7"
echo ""

# Define Java services array
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
    "staff-mobile-app"
)

# Parent POM template
create_parent_pom() {
    cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.exalt.warehousing</groupId>
    <artifactId>warehousing-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>Warehousing Domain Parent</name>
    <description>Parent POM for all warehousing microservices</description>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>3.1.5</spring-boot.version>
        <spring-cloud.version>2022.0.4</spring-cloud.version>
        <lombok.version>1.18.30</lombok.version>
        <maven-compiler-plugin.version>3.11.0</maven-compiler-plugin.version>
    </properties>

    <modules>
        <module>billing-service</module>
        <module>config-server-enterprise</module>
        <module>cross-region-logistics-service</module>
        <module>fulfillment-service</module>
        <module>inventory-service</module>
        <module>self-storage-service</module>
        <module>warehouse-analytics</module>
        <module>warehouse-management-service</module>
        <module>warehouse-onboarding</module>
        <module>warehouse-operations</module>
        <module>warehouse-subscription</module>
        <module>warehousing-production</module>
        <module>warehousing-shared</module>
        <module>warehousing-staging</module>
        <module>shared-infrastructure-test</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                    <configuration>
                        <excludes>
                            <exclude>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>${maven-compiler-plugin.version}</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
EOF
}

# Function to standardize individual service POM
standardize_service_pom() {
    local service=$1
    local artifactId=$(echo $service | sed 's/-service$//')
    
    echo "📦 Standardizing $service..."
    
    if [ -f "$service/pom.xml" ]; then
        # Backup original
        cp "$service/pom.xml" "$service/pom.xml.original" 2>/dev/null
        
        # Update parent reference
        sed -i '/<parent>/,/<\/parent>/c\
    <parent>\
        <groupId>com.exalt.warehousing</groupId>\
        <artifactId>warehousing-parent</artifactId>\
        <version>1.0.0</version>\
        <relativePath>../pom.xml</relativePath>\
    </parent>' "$service/pom.xml"
        
        # Update groupId and artifactId
        sed -i "s/<groupId>.*<\/groupId>/<groupId>com.exalt.warehousing<\/groupId>/g" "$service/pom.xml"
        sed -i "s/<artifactId>.*<\/artifactId>/<artifactId>$artifactId<\/artifactId>/1" "$service/pom.xml"
        
        # Ensure Lombok dependency
        if ! grep -q "lombok" "$service/pom.xml"; then
            sed -i '/<dependencies>/a\
        <dependency>\
            <groupId>org.projectlombok</groupId>\
            <artifactId>lombok</artifactId>\
            <scope>provided</scope>\
        </dependency>' "$service/pom.xml"
        fi
        
        echo "✅ $service standardized"
    else
        echo "⚠️  $service/pom.xml not found"
    fi
}

# Create parent POM
echo "Creating parent POM..."
create_parent_pom

# Standardize each service
for service in "${JAVA_SERVICES[@]}"; do
    standardize_service_pom "$service"
done

# Fix package naming for shared-infrastructure-test
echo ""
echo "🔧 Fixing package naming for shared-infrastructure-test..."
if [ -d "shared-infrastructure-test/src" ]; then
    find shared-infrastructure-test/src -name "*.java" -type f -exec sed -i 's/package com.exalt.ecosystem/package com.exalt.warehousing/g' {} \;
    echo "✅ Package naming fixed"
fi

echo ""
echo "✅ Java services standardization completed!"
echo ""
echo "📊 Summary:"
echo "- Parent POM created with Java 17 and Spring Boot 3.1.5"
echo "- ${#JAVA_SERVICES[@]} services standardized"
echo "- Lombok dependencies added"
echo "- Package naming fixed for shared-infrastructure-test"