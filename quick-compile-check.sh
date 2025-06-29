#!/bin/bash

echo "=== Quick Compilation Check ==="
echo "Date: $(date)"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Services to check
SERVICES=(
    "warehousing-shared"
    "inventory-service"
    "fulfillment-service"
    "billing-service"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-analytics"
    "warehouse-subscription"
    "warehouse-operations"
)

# Quick compile check for each service
echo "Checking compilation status..."
echo ""

TOTAL=0
SUCCESS=0

for service in "${SERVICES[@]}"; do
    ((TOTAL++))
    
    if [ -d "$service" ]; then
        echo -n "$service: "
        
        cd "$service"
        
        # Quick compile (no tests)
        if mvn compile -q > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Compiles${NC}"
            ((SUCCESS++))
        else
            echo -e "${RED}✗ Compilation error${NC}"
            
            # Show first error
            mvn compile 2>&1 | grep -A 2 "ERROR" | head -n 3
        fi
        
        cd ..
    else
        echo -e "$service: ${RED}✗ Not found${NC}"
    fi
done

echo ""
echo "Summary: $SUCCESS/$TOTAL services compile successfully"

if [ $SUCCESS -eq $TOTAL ]; then
    echo -e "${GREEN}✓ All services compile successfully!${NC}"
else
    echo -e "${YELLOW}⚠ Some services have compilation errors${NC}"
fi