#!/bin/bash

echo "🧪 Running Warehousing Domain Tests..."
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test results
PASSED=0
FAILED=0
SKIPPED=0

# Start test dependencies
echo "🐳 Starting test dependencies..."
docker-compose -f docker-compose.test.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for test services to be ready..."
sleep 30

# Test services array
SERVICES=(
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
    "warehousing-shared"
    "shared-infrastructure-test"
)

# Run tests for each service
for service in "${SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        echo ""
        echo "🔍 Testing $service..."
        
        cd "$service"
        
        # Run Maven tests
        if mvn clean test -Dspring.profiles.active=test > "../test-results-$service.log" 2>&1; then
            echo -e "${GREEN}✅ $service: PASSED${NC}"
            ((PASSED++))
        else
            echo -e "${RED}❌ $service: FAILED${NC}"
            echo "   Check test-results-$service.log for details"
            ((FAILED++))
        fi
        
        cd ..
    else
        echo -e "${YELLOW}⚠️  $service: SKIPPED (no pom.xml found)${NC}"
        ((SKIPPED++))
    fi
done

# Test Node.js services
NODE_SERVICES=("global-hq-admin" "regional-admin" "staff-mobile-app")

for service in "${NODE_SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/package.json" ]; then
        echo ""
        echo "🔍 Testing $service..."
        
        cd "$service"
        
        # Install dependencies if needed
        if [ ! -d "node_modules" ]; then
            echo "   Installing dependencies..."
            npm ci > "../npm-install-$service.log" 2>&1
        fi
        
        # Run tests
        if npm test -- --watchAll=false --coverage > "../test-results-$service.log" 2>&1; then
            echo -e "${GREEN}✅ $service: PASSED${NC}"
            ((PASSED++))
        else
            echo -e "${RED}❌ $service: FAILED${NC}"
            echo "   Check test-results-$service.log for details"
            ((FAILED++))
        fi
        
        cd ..
    else
        echo -e "${YELLOW}⚠️  $service: SKIPPED (no package.json found)${NC}"
        ((SKIPPED++))
    fi
done

# Stop test dependencies
echo ""
echo "🛑 Stopping test dependencies..."
docker-compose -f docker-compose.test.yml down

# Print summary
echo ""
echo "📊 Test Summary:"
echo -e "   ${GREEN}Passed: $PASSED${NC}"
echo -e "   ${RED}Failed: $FAILED${NC}"
echo -e "   ${YELLOW}Skipped: $SKIPPED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}💥 $FAILED test(s) failed!${NC}"
    exit 1
fi
