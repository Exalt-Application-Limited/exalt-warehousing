#!/bin/bash

echo "=== GENERATING UNIT TESTS FOR WAREHOUSING SERVICES ==="

cd /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing

# Function to create application test
create_app_test() {
    local package_name=$1
    local class_name=$2
    local test_path=$3
    
    cat > "$test_path" << EOF
package $package_name;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ${class_name}Tests {

    @Test
    void contextLoads() {
        // Test that the Spring Boot application context loads successfully
    }
}
EOF
}

# Services to process
services=(
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "self-storage-service"
    "staff-mobile-app"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
)

for service in "${services[@]}"; do
    echo "Processing: $service"
    
    if [ ! -d "$service" ]; then
        echo "  ⚠️  Service directory not found"
        continue
    fi
    
    # Find main application class
    app_file=$(find "$service/src/main/java" -name "*Application.java" 2>/dev/null | head -1)
    
    if [ -z "$app_file" ]; then
        echo "  ⚠️  No Application class found"
        continue
    fi
    
    # Extract package name
    package_name=$(grep "^package" "$app_file" | sed 's/package //; s/;//')
    class_name=$(basename "$app_file" .java)
    
    # Create test directory
    test_dir="$service/src/test/java/$(echo $package_name | tr '.' '/')"
    mkdir -p "$test_dir"
    
    # Create test file
    test_file="$test_dir/${class_name}Tests.java"
    
    if [ ! -f "$test_file" ]; then
        create_app_test "$package_name" "$class_name" "$test_file"
        echo "  ✅ Created: ${class_name}Tests.java"
    else
        echo "  ⏭️  Skipped: ${class_name}Tests.java (already exists)"
    fi
    
    # Create test resources
    resources_dir="$service/src/test/resources"
    mkdir -p "$resources_dir"
    
    # Create application-test.yml
    test_config="$resources_dir/application-test.yml"
    if [ ! -f "$test_config" ]; then
        cat > "$test_config" << 'EOF'
spring:
  profiles:
    active: test
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
    
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    
  h2:
    console:
      enabled: false
      
logging:
  level:
    root: WARN
    com.ecosystem: INFO
EOF
        echo "  ✅ Created: application-test.yml"
    fi
done

echo ""
echo "✅ Unit test generation complete!"