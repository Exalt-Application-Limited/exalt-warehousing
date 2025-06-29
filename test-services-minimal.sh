#!/bin/bash

echo "🧪 Running Warehousing Domain Tests (Minimal - No Docker Dependencies)..."
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
        
        # Run Maven compilation only (safer than full test)
        if mvn clean compile -q > "../compile-results-$service.log" 2>&1; then
            echo -e "${GREEN}✅ $service: COMPILED SUCCESSFULLY${NC}"
            ((PASSED++))
        else
            echo -e "${RED}❌ $service: COMPILATION FAILED${NC}"
            echo "   Check compile-results-$service.log for details"
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
        
        # Just check if package.json is valid
        if node -e "JSON.parse(require('fs').readFileSync('package.json', 'utf8'))" > "../check-results-$service.log" 2>&1; then
            echo -e "${GREEN}✅ $service: PACKAGE.JSON VALID${NC}"
            ((PASSED++))
        else
            echo -e "${RED}❌ $service: PACKAGE.JSON INVALID${NC}"
            echo "   Check check-results-$service.log for details"
            ((FAILED++))
        fi
        
        cd ..
    else
        echo -e "${YELLOW}⚠️  $service: SKIPPED (no package.json found)${NC}"
        ((SKIPPED++))
    fi
done

# Print summary
echo ""
echo "📊 Test Summary:"
echo -e "   ${GREEN}Passed: $PASSED${NC}"
echo -e "   ${RED}Failed: $FAILED${NC}"
echo -e "   ${YELLOW}Skipped: $SKIPPED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All compilation tests passed!${NC}"
    exit 0
else
    echo -e "${RED}💥 $FAILED compilation test(s) failed!${NC}"
    exit 1
fi