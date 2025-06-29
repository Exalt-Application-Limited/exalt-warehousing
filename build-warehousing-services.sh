#!/bin/bash

echo "=== Warehousing Services Build Script ==="
echo "Date: $(date)"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Build tracking
TOTAL_SERVICES=0
SUCCESSFUL_BUILDS=0
FAILED_BUILDS=0
BUILD_LOG="build-results-$(date +%Y%m%d-%H%M%S).log"

# Services to build (excluding frontend apps)
JAVA_SERVICES=(
    "inventory-service"
    "fulfillment-service"
    "billing-service"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-analytics"
    "warehouse-subscription"
    "warehouse-operations"
)

# Initialize log
echo "Build started at: $(date)" > "$BUILD_LOG"
echo "========================================" >> "$BUILD_LOG"

# Function to build a service
build_service() {
    local service=$1
    local service_dir="$service"
    
    echo -e "${BLUE}Building $service...${NC}"
    echo "" >> "$BUILD_LOG"
    echo "Building $service..." >> "$BUILD_LOG"
    
    if [ ! -d "$service_dir" ]; then
        echo -e "${RED}✗ Service directory not found: $service_dir${NC}"
        echo "ERROR: Service directory not found: $service_dir" >> "$BUILD_LOG"
        ((FAILED_BUILDS++))
        return 1
    fi
    
    cd "$service_dir" || return 1
    
    # Clean and compile
    echo "  Cleaning previous build..."
    mvn clean >> "../$BUILD_LOG" 2>&1
    
    echo "  Compiling..."
    if mvn compile >> "../$BUILD_LOG" 2>&1; then
        echo -e "  ${GREEN}✓ Compilation successful${NC}"
        
        # Run tests
        echo "  Running tests..."
        if mvn test >> "../$BUILD_LOG" 2>&1; then
            echo -e "  ${GREEN}✓ Tests passed${NC}"
            
            # Package
            echo "  Packaging..."
            if mvn package -DskipTests >> "../$BUILD_LOG" 2>&1; then
                echo -e "  ${GREEN}✓ Packaging successful${NC}"
                echo -e "${GREEN}✓ $service build successful${NC}"
                echo "SUCCESS: $service build completed" >> "../$BUILD_LOG"
                ((SUCCESSFUL_BUILDS++))
                cd ..
                return 0
            else
                echo -e "  ${RED}✗ Packaging failed${NC}"
                echo "ERROR: $service packaging failed" >> "../$BUILD_LOG"
            fi
        else
            echo -e "  ${RED}✗ Tests failed${NC}"
            echo "ERROR: $service tests failed" >> "../$BUILD_LOG"
        fi
    else
        echo -e "  ${RED}✗ Compilation failed${NC}"
        echo "ERROR: $service compilation failed" >> "../$BUILD_LOG"
        
        # Try to extract specific error
        tail -n 50 "../$BUILD_LOG" | grep -E "(ERROR|error:|cannot find symbol|package .* does not exist)" | head -n 5
    fi
    
    ((FAILED_BUILDS++))
    cd ..
    return 1
}

# Function to check frontend apps
check_frontend_apps() {
    echo ""
    echo -e "${YELLOW}Checking frontend applications...${NC}"
    
    # Check React app
    if [ -d "global-hq-admin" ] && [ -f "global-hq-admin/package.json" ]; then
        echo -e "${GREEN}✓ global-hq-admin (React) - Frontend app present${NC}"
    else
        echo -e "${RED}✗ global-hq-admin - Not found${NC}"
    fi
    
    # Check Vue app
    if [ -d "regional-admin" ] && [ -f "regional-admin/package.json" ]; then
        echo -e "${GREEN}✓ regional-admin (Vue.js) - Frontend app present${NC}"
    else
        echo -e "${RED}✗ regional-admin - Not found${NC}"
    fi
    
    # Check React Native app
    if [ -d "staff-mobile-app" ] && [ -f "staff-mobile-app/package.json" ]; then
        echo -e "${GREEN}✓ staff-mobile-app (React Native) - Mobile app present${NC}"
    else
        echo -e "${RED}✗ staff-mobile-app - Not found${NC}"
    fi
}

# First, install parent POM
echo -e "${YELLOW}Installing parent POM...${NC}"
if mvn install -N >> "$BUILD_LOG" 2>&1; then
    echo -e "${GREEN}✓ Parent POM installed${NC}"
else
    echo -e "${RED}✗ Parent POM installation failed${NC}"
    echo "ERROR: Parent POM installation failed" >> "$BUILD_LOG"
    exit 1
fi

# Build shared module first
echo ""
echo -e "${YELLOW}Building shared module first...${NC}"
if [ -d "warehousing-shared" ]; then
    build_service "warehousing-shared"
fi

# Build each service
echo ""
echo -e "${YELLOW}Building Java microservices...${NC}"
echo ""

for service in "${JAVA_SERVICES[@]}"; do
    ((TOTAL_SERVICES++))
    build_service "$service"
    echo ""
done

# Check frontend apps
check_frontend_apps

# Generate summary report
echo ""
echo "========================================" >> "$BUILD_LOG"
echo "Build Summary" >> "$BUILD_LOG"
echo "========================================" >> "$BUILD_LOG"
echo "Total Java services: $TOTAL_SERVICES" >> "$BUILD_LOG"
echo "Successful builds: $SUCCESSFUL_BUILDS" >> "$BUILD_LOG"
echo "Failed builds: $FAILED_BUILDS" >> "$BUILD_LOG"

# Display summary
echo ""
echo -e "${BLUE}=== Build Summary ===${NC}"
echo "Total Java services: $TOTAL_SERVICES"
echo -e "Successful builds: ${GREEN}$SUCCESSFUL_BUILDS${NC}"
echo -e "Failed builds: ${RED}$FAILED_BUILDS${NC}"
echo ""

# Calculate success rate
if [ $TOTAL_SERVICES -gt 0 ]; then
    SUCCESS_RATE=$((SUCCESSFUL_BUILDS * 100 / TOTAL_SERVICES))
    echo "Success rate: $SUCCESS_RATE%"
    
    if [ $SUCCESS_RATE -eq 100 ]; then
        echo -e "${GREEN}✓ All services built successfully!${NC}"
    elif [ $SUCCESS_RATE -ge 80 ]; then
        echo -e "${YELLOW}⚠ Most services built successfully ($SUCCESS_RATE%)${NC}"
    else
        echo -e "${RED}✗ Build failures detected ($SUCCESS_RATE% success)${NC}"
    fi
fi

echo ""
echo "Build log: $BUILD_LOG"

# List failed services
if [ $FAILED_BUILDS -gt 0 ]; then
    echo ""
    echo -e "${RED}Failed services:${NC}"
    grep "ERROR:" "$BUILD_LOG" | grep -v "Parent POM" | awk '{print $2}' | sort | uniq
fi

# Create test results directory
mkdir -p test-results

# Generate detailed test report
echo ""
echo -e "${YELLOW}Generating test report...${NC}"
find . -path "*/target/surefire-reports/*.xml" -type f | while read -r test_file; do
    service_name=$(echo "$test_file" | cut -d'/' -f2)
    cp "$test_file" "test-results/${service_name}-$(basename "$test_file")"
done

echo -e "${GREEN}✓ Test results collected in test-results/${NC}"

exit $FAILED_BUILDS