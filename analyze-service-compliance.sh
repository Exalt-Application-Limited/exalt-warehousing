#!/bin/bash

# Required directories for compliance
REQUIRED_DIRS=(".github/workflows" "tests" "k8s" "api-docs" "database" "docs" "scripts" "i18n")

# Required files for compliance
REQUIRED_FILES=("README.md" "Dockerfile" ".gitignore")

# Function to check compliance
check_compliance() {
    local service_path=$1
    local service_name=$(basename "$service_path")
    local service_type=$2
    
    echo "=== $service_name ==="
    echo "Service Type: $service_type"
    echo ""
    
    # Check for required directories
    echo "Missing Directories:"
    for dir in "${REQUIRED_DIRS[@]}"; do
        if [ ! -d "$service_path/$dir" ]; then
            echo "  - $dir"
        fi
    done
    
    # Check for required files
    echo ""
    echo "Missing Files:"
    for file in "${REQUIRED_FILES[@]}"; do
        if [ ! -f "$service_path/$file" ]; then
            echo "  - $file"
        fi
    done
    
    # Check for configuration files
    echo ""
    echo "Configuration Files:"
    if [ "$service_type" = "Java" ]; then
        if [ -f "$service_path/pom.xml" ]; then
            echo "  - pom.xml: Present"
        else
            echo "  - pom.xml: MISSING"
        fi
    elif [ "$service_type" = "Frontend" ]; then
        if [ -f "$service_path/package.json" ]; then
            echo "  - package.json: Present"
        else
            echo "  - package.json: MISSING"
        fi
    fi
    
    # Additional checks
    echo ""
    echo "Additional Findings:"
    
    # Check for src directory
    if [ -d "$service_path/src" ]; then
        echo "  - src directory: Present"
    else
        echo "  - src directory: MISSING"
    fi
    
    # Check for test directory structure
    if [ "$service_type" = "Java" ]; then
        if [ -d "$service_path/src/test" ]; then
            echo "  - src/test directory: Present"
        else
            echo "  - src/test directory: MISSING"
        fi
    fi
    
    # Check for logs directory
    if [ -d "$service_path/logs" ]; then
        echo "  - logs directory: Present (should be in .gitignore)"
    fi
    
    # Check for target directory
    if [ -d "$service_path/target" ]; then
        echo "  - target directory: Present (should be in .gitignore)"
    fi
    
    echo ""
    echo "----------------------------------------"
    echo ""
}

# Main execution
echo "WAREHOUSING SERVICES COMPLIANCE ANALYSIS"
echo "========================================"
echo ""

# Java Services
JAVA_SERVICES=(
    "billing-service"
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "self-storage-service"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
)

echo "JAVA SERVICES"
echo "============="
echo ""

for service in "${JAVA_SERVICES[@]}"; do
    if [ -d "$service" ]; then
        check_compliance "$service" "Java"
    fi
done

# Frontend Services
FRONTEND_SERVICES=(
    "global-hq-admin"
    "regional-admin"
    "staff-mobile-app"
)

echo ""
echo "FRONTEND SERVICES"
echo "================="
echo ""

for service in "${FRONTEND_SERVICES[@]}"; do
    if [ -d "$service" ]; then
        check_compliance "$service" "Frontend"
    fi
done

# Summary
echo ""
echo "SUMMARY"
echo "======="
echo "Total Java Services: ${#JAVA_SERVICES[@]}"
echo "Total Frontend Services: ${#FRONTEND_SERVICES[@]}"
echo "Total Services: $((${#JAVA_SERVICES[@]} + ${#FRONTEND_SERVICES[@]}))"