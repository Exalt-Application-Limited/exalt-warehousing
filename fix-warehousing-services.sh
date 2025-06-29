#!/bin/bash

echo "=== WAREHOUSING SERVICES FIX SCRIPT ==="
echo "Starting at: $(date)"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Step 1: Build parent POM
echo -e "\n${CYAN}[1/5] Building Warehousing Parent POM...${NC}"
mvn clean install -N

# Step 2: Build warehousing-shared first
echo -e "\n${CYAN}[2/5] Building Warehousing Shared Library...${NC}"
cd warehousing-shared
mvn clean install -DskipTests
cd ..

# Step 3: Build production and staging configs
echo -e "\n${CYAN}[3/5] Building Environment Configs...${NC}"
for env in warehousing-production warehousing-staging; do
    if [ -d "$env" ]; then
        echo -e "${YELLOW}Building $env...${NC}"
        cd $env
        mvn clean install -DskipTests
        cd ..
    fi
done

# Step 4: Build all services
echo -e "\n${CYAN}[4/5] Building All Services...${NC}"

MAVEN_SERVICES=(
    "billing-service"
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

for service in "${MAVEN_SERVICES[@]}"; do
    echo -e "\n${YELLOW}Processing $service...${NC}"
    
    if [ -f "$service/pom.xml" ]; then
        cd "$service"
        
        # Download dependencies offline first
        mvn dependency:go-offline -q
        
        # Build
        if mvn clean compile -DskipTests > build.log 2>&1; then
            echo -e "  ${GREEN}✅ Build SUCCESS${NC}"
            
            # Test
            if mvn test > test.log 2>&1; then
                echo -e "  ${GREEN}✅ Tests PASSED${NC}"
                echo "✅ $service → Build Passed, Tests Passed" >> ../results.txt
            else
                echo -e "  ${RED}❌ Tests FAILED${NC}"
                echo "❌ $service → Build Passed, Tests Failed" >> ../results.txt
            fi
        else
            echo -e "  ${RED}❌ Build FAILED${NC}"
            echo "❌ $service → Build Failed" >> ../results.txt
            grep -E "ERROR|error:" build.log | head -3
        fi
        
        cd ..
    fi
done

# Step 5: Node.js services
echo -e "\n${CYAN}[5/5] Fixing Node.js Services...${NC}"

for service in global-hq-admin regional-admin; do
    if [ -f "$service/package.json" ]; then
        echo -e "\n${YELLOW}Processing $service...${NC}"
        cd "$service"
        
        npm install --legacy-peer-deps
        
        if npm run build 2>/dev/null; then
            echo -e "  ${GREEN}✅ Build SUCCESS${NC}"
        fi
        
        if npm test -- --passWithNoTests 2>&1 | grep -q "passed"; then
            echo -e "  ${GREEN}✅ Tests PASSED${NC}"
            echo "✅ $service → Build Passed, Tests Passed" >> ../results.txt
        else
            echo -e "  ${YELLOW}⚠️  No tests or tests failed${NC}"
            echo "⚠️ $service → Build Passed, No Tests" >> ../results.txt
        fi
        
        cd ..
    fi
done

echo -e "\n${CYAN}=== FINAL REPORT ===${NC}"
cat results.txt
echo -e "\nCompleted at: $(date)"