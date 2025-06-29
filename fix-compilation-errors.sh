#!/bin/bash

echo "=== Fixing Compilation Errors ==="
echo "Date: $(date)"
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Fix 1: Add MapStruct version to fulfillment-service
echo -e "${YELLOW}Fixing MapStruct version in fulfillment-service...${NC}"
sed -i '/<artifactId>mapstruct<\/artifactId>/a\            <version>1.5.5.Final</version>' fulfillment-service/pom.xml
echo -e "${GREEN}✓ MapStruct version added${NC}"

# Fix 2: Fix shared common package references
echo -e "${YELLOW}Fixing shared package references...${NC}"

# Find and fix all references to com.exalt.warehousing.shared.common
find . -name "*.java" -type f -exec grep -l "com.exalt.warehousing.shared.common" {} \; | while read file; do
    echo "  Fixing: $file"
    sed -i 's/com.exalt.warehousing.shared.common/com.exalt.warehousing.shared/g' "$file"
done

echo -e "${GREEN}✓ Package references fixed${NC}"

# Fix 3: Add missing Spring Boot plugin version
echo -e "${YELLOW}Adding Spring Boot plugin version...${NC}"

# Add version to Spring Boot Maven plugin where missing
find . -name "pom.xml" -type f | while read pom; do
    if grep -q "spring-boot-maven-plugin" "$pom" && ! grep -B2 "spring-boot-maven-plugin" "$pom" | grep -q "<version>"; then
        echo "  Fixing: $pom"
        sed -i '/<artifactId>spring-boot-maven-plugin<\/artifactId>/a\                <version>${spring-boot.version}</version>' "$pom"
    fi
done

echo -e "${GREEN}✓ Spring Boot plugin versions added${NC}"

# Fix 4: Add warehousing-shared dependency where needed
echo -e "${YELLOW}Adding warehousing-shared dependency to services...${NC}"

SERVICES=(
    "inventory-service"
    "fulfillment-service"
    "billing-service"
    "warehouse-management-service"
    "warehouse-analytics"
    "warehouse-subscription"
    "warehouse-operations"
)

for service in "${SERVICES[@]}"; do
    if [ -f "$service/pom.xml" ]; then
        # Check if warehousing-shared dependency exists
        if ! grep -q "warehousing-shared" "$service/pom.xml"; then
            echo "  Adding to: $service"
            
            # Add warehousing-shared dependency after spring-boot-starter-web
            sed -i '/<artifactId>spring-boot-starter-web<\/artifactId>/,/<\/dependency>/a\
        \
        <!-- Warehousing Shared -->\
        <dependency>\
            <groupId>com.exalt.warehousing</groupId>\
            <artifactId>warehousing-shared</artifactId>\
            <version>1.0.0</version>\
        </dependency>' "$service/pom.xml"
        fi
    fi
done

echo -e "${GREEN}✓ Warehousing-shared dependencies added${NC}"

# Fix 5: Add missing MapStruct processor
echo -e "${YELLOW}Adding MapStruct annotation processor...${NC}"

# For services using MapStruct, add the annotation processor
if grep -q "mapstruct" fulfillment-service/pom.xml; then
    # Check if annotation processor is missing
    if ! grep -q "mapstruct-processor" fulfillment-service/pom.xml; then
        sed -i '/<\/dependencies>/i\
        <dependency>\
            <groupId>org.mapstruct</groupId>\
            <artifactId>mapstruct-processor</artifactId>\
            <version>1.5.5.Final</version>\
            <scope>provided</scope>\
        </dependency>' fulfillment-service/pom.xml
    fi
fi

echo -e "${GREEN}✓ MapStruct processor added${NC}"

echo ""
echo -e "${GREEN}All compilation fixes applied!${NC}"
echo ""
echo "Next steps:"
echo "1. Run: mvn clean compile"
echo "2. Fix any remaining errors"
echo "3. Run tests: mvn test"