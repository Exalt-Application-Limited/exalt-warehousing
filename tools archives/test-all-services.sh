#!/bin/bash
# test-all-services.sh - Test compilation and startup for all warehousing services

echo "=========================================="
echo "WAREHOUSING SERVICES COMPILATION TEST"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Array of services to test
declare -a services=(
    "inventory-service"
    "fulfillment-service"
    "warehouse-management-service"
    "cross-region-logistics-service"
    "self-storage-service"
)

# Function to test a service
test_service() {
    local service=$1
    echo -e "${YELLOW}Testing $service...${NC}"
    
    cd "$BASE_DIR/$service"
    
    # Clean and compile
    echo "  Cleaning and compiling..."
    mvn clean compile -DskipTests > /tmp/${service}-compile.log 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "  ${GREEN}✓ Compilation successful${NC}"
        
        # Test if it starts (timeout after 30 seconds)
        echo "  Testing startup..."
        timeout 30s mvn spring-boot:run > /tmp/${service}-startup.log 2>&1 &
        local pid=$!
        
        # Wait a bit for startup
        sleep 15
        
        # Check if process is still running
        if ps -p $pid > /dev/null; then
            echo -e "  ${GREEN}✓ Service started successfully${NC}"
            # Kill the process
            kill $pid 2>/dev/null
            wait $pid 2>/dev/null
        else
            echo -e "  ${RED}✗ Service failed to start${NC}"
            echo "  Check /tmp/${service}-startup.log for details"
        fi
    else
        echo -e "  ${RED}✗ Compilation failed${NC}"
        echo "  Check /tmp/${service}-compile.log for details"
    fi
    
    echo ""
}

# Main execution
echo "Starting service tests..."
echo ""

# Test each service
for service in "${services[@]}"; do
    if [ -d "$BASE_DIR/$service" ]; then
        test_service "$service"
    else
        echo -e "${RED}✗ Service directory not found: $service${NC}"
        echo ""
    fi
done

echo "=========================================="
echo "TEST SUMMARY"
echo "=========================================="
echo ""
echo "Check /tmp/*-compile.log for compilation logs"
echo "Check /tmp/*-startup.log for startup logs"
echo ""
echo "To run a specific service:"
echo "  cd [service-name]"
echo "  mvn spring-boot:run"
echo ""
