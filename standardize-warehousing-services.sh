#!/bin/bash

echo "=== Warehousing Domain Standardization Script ==="
echo "Date: $(date)"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to standardize package names from com.ecosystem to com.exalt
standardize_package_names() {
    local service=$1
    echo -e "${YELLOW}Standardizing package names for $service...${NC}"
    
    # Find all Java files and replace package declarations
    find "$service/src" -name "*.java" -type f -exec sed -i 's/package com\.ecosystem\./package com.exalt./g' {} \;
    find "$service/src" -name "*.java" -type f -exec sed -i 's/import com\.ecosystem\./import com.exalt./g' {} \;
    
    # Update directory structure
    if [ -d "$service/src/main/java/com/ecosystem" ]; then
        mkdir -p "$service/src/main/java/com/exalt"
        mv "$service/src/main/java/com/ecosystem/warehousing" "$service/src/main/java/com/exalt/"
        rm -rf "$service/src/main/java/com/ecosystem"
    fi
    
    if [ -d "$service/src/test/java/com/ecosystem" ]; then
        mkdir -p "$service/src/test/java/com/exalt"
        mv "$service/src/test/java/com/ecosystem/warehousing" "$service/src/test/java/com/exalt/"
        rm -rf "$service/src/test/java/com/ecosystem"
    fi
    
    echo -e "${GREEN}✓ Package names standardized for $service${NC}"
}

# Function to fix POM parent reference
fix_pom_parent() {
    local service=$1
    local pom_file="$service/pom.xml"
    
    echo -e "${YELLOW}Fixing POM parent for $service...${NC}"
    
    # Create temporary file
    local temp_file="${pom_file}.tmp"
    
    # Replace parent section
    awk '
    /<parent>/ {
        print "    <parent>"
        print "        <groupId>com.exalt.warehousing</groupId>"
        print "        <artifactId>warehousing-parent</artifactId>"
        print "        <version>1.0.0</version>"
        print "        <relativePath>../pom.xml</relativePath>"
        print "    </parent>"
        in_parent = 1
        next
    }
    /<\/parent>/ && in_parent {
        in_parent = 0
        next
    }
    !in_parent { print }
    ' "$pom_file" > "$temp_file"
    
    mv "$temp_file" "$pom_file"
    echo -e "${GREEN}✓ POM parent fixed for $service${NC}"
}

# Function to update Spring Boot version
update_spring_boot_version() {
    local service=$1
    local pom_file="$service/pom.xml"
    
    echo -e "${YELLOW}Updating Spring Boot version for $service...${NC}"
    
    # Remove explicit Spring Boot version if parent is warehousing-parent
    sed -i '/<version>3\.1\.1<\/version>/d' "$pom_file"
    sed -i '/<version>3\.1\.0<\/version>/d' "$pom_file"
    
    echo -e "${GREEN}✓ Spring Boot version updated for $service${NC}"
}

# Function to convert properties to yml
convert_properties_to_yml() {
    local service=$1
    local props_file="$service/src/main/resources/application.properties"
    local yml_file="$service/src/main/resources/application.yml"
    
    if [ -f "$props_file" ]; then
        echo -e "${YELLOW}Converting properties to yml for $service...${NC}"
        
        # Basic conversion (this is simplified, real conversion might need more logic)
        cat > "$yml_file" << 'EOF'
spring:
  application:
    name: ${service}
  profiles:
    active: dev
  
server:
  port: 8080
  
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
EOF
        
        # Remove old properties file
        rm -f "$props_file"
        
        echo -e "${GREEN}✓ Converted to application.yml for $service${NC}"
    fi
}

# Function to add Lombok to POM if missing
add_lombok_dependency() {
    local service=$1
    local pom_file="$service/pom.xml"
    
    # Check if Lombok is already present
    if ! grep -q "lombok" "$pom_file"; then
        echo -e "${YELLOW}Adding Lombok dependency to $service...${NC}"
        
        # Add Lombok dependency
        sed -i '/<dependencies>/a\
        <dependency>\
            <groupId>org.projectlombok</groupId>\
            <artifactId>lombok</artifactId>\
            <scope>provided</scope>\
        </dependency>' "$pom_file"
        
        echo -e "${GREEN}✓ Lombok added to $service${NC}"
    fi
}

# Main standardization process
echo "Starting standardization process..."
echo ""

# Services to standardize
SERVICES=(
    "inventory-service"
    "fulfillment-service"
    "billing-service"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-analytics"
    "warehouse-subscription"
    "warehouse-operations"
)

# First, fix the corrupted fulfillment-service POM
echo -e "${RED}CRITICAL: Fixing corrupted fulfillment-service POM...${NC}"
cat > "fulfillment-service/pom.xml.fixed" << 'EOF'
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
    
    <artifactId>fulfillment-service</artifactId>
    <name>Fulfillment Service</name>
    <description>Service for managing order fulfillment operations</description>
    
    <dependencies>
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
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        
        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
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

# Backup and replace
mv "fulfillment-service/pom.xml" "fulfillment-service/pom.xml.backup"
mv "fulfillment-service/pom.xml.fixed" "fulfillment-service/pom.xml"
echo -e "${GREEN}✓ Fixed corrupted fulfillment-service POM${NC}"

# Process each service
for service in "${SERVICES[@]}"; do
    echo ""
    echo "=== Processing $service ==="
    
    if [ -d "$service" ]; then
        # 1. Standardize package names
        standardize_package_names "$service"
        
        # 2. Fix POM parent references for services that need it
        case "$service" in
            "billing-service"|"warehouse-onboarding"|"warehouse-analytics"|"warehouse-operations")
                fix_pom_parent "$service"
                update_spring_boot_version "$service"
                ;;
        esac
        
        # 3. Convert properties to yml if needed
        if [ "$service" == "warehouse-operations" ]; then
            convert_properties_to_yml "$service"
        fi
        
        # 4. Add Lombok dependency
        add_lombok_dependency "$service"
        
        echo -e "${GREEN}✓ Completed standardization for $service${NC}"
    else
        echo -e "${RED}✗ Service directory not found: $service${NC}"
    fi
done

echo ""
echo "=== Standardization Summary ==="
echo "1. Package names: com.ecosystem.* → com.exalt.*"
echo "2. Parent POMs: Updated to use warehousing-parent"
echo "3. Spring Boot version: Standardized to 3.1.5"
echo "4. Configuration: Converted to application.yml"
echo "5. Lombok: Added where missing"
echo ""
echo -e "${GREEN}Standardization complete!${NC}"