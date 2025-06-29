#!/bin/bash
# Comprehensive build and test script for warehousing domain

echo "================================================"
echo "WAREHOUSING DOMAIN - COMPLETE BUILD AND TEST"
echo "================================================"
echo ""

# Set the base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Function to build a module
build_module() {
    local module_name=$1
    local module_path="$BASE_DIR/$module_name"
    
    echo "Building $module_name..."
    cd "$module_path" || exit 1
    
    if mvn clean install -DskipTests; then
        echo "✅ $module_name built successfully"
        return 0
    else
        echo "❌ $module_name build failed"
        return 1
    fi
}

# Function to test a module
test_module() {
    local module_name=$1
    local module_path="$BASE_DIR/$module_name"
    
    echo "Testing $module_name..."
    cd "$module_path" || exit 1
    
    if mvn test; then
        echo "✅ $module_name tests passed"
        return 0
    else
        echo "❌ $module_name tests failed"
        return 1
    fi
}

# Track results
FAILED_BUILDS=()
FAILED_TESTS=()
SUCCESSFUL_BUILDS=()
SUCCESSFUL_TESTS=()

# Step 1: Build parent POM
echo "Step 1: Building parent POM..."
cd "$BASE_DIR" || exit 1
if mvn clean install -N -DskipTests; then
    echo "✅ Parent POM built successfully"
else
    echo "❌ Parent POM build failed"
    exit 1
fi
echo ""

# Step 2: Build shared library first (dependency for others)
echo "Step 2: Building shared library..."
if build_module "warehousing-shared"; then
    SUCCESSFUL_BUILDS+=("warehousing-shared")
else
    FAILED_BUILDS+=("warehousing-shared")
fi
echo ""

# Step 3: Build all services in order
echo "Step 3: Building all warehousing services..."
SERVICES=(
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
    "warehousing-production"
    "warehousing-staging"
)

for service in "${SERVICES[@]}"; do
    echo ""
    echo "----------------------------------------"
    if build_module "$service"; then
        SUCCESSFUL_BUILDS+=("$service")
    else
        FAILED_BUILDS+=("$service")
    fi
done

# Step 4: Run tests for successfully built modules
echo ""
echo "Step 4: Running tests for successfully built modules..."
for module in "${SUCCESSFUL_BUILDS[@]}"; do
    echo ""
    echo "----------------------------------------"
    if test_module "$module"; then
        SUCCESSFUL_TESTS+=("$module")
    else
        FAILED_TESTS+=("$module")
    fi
done

# Final Report
echo ""
echo "================================================"
echo "WAREHOUSING DOMAIN BUILD AND TEST REPORT"
echo "================================================"
echo ""
echo "Build Results:"
echo "✅ Successful builds: ${#SUCCESSFUL_BUILDS[@]}"
for module in "${SUCCESSFUL_BUILDS[@]}"; do
    echo "   - $module"
done
echo ""
echo "❌ Failed builds: ${#FAILED_BUILDS[@]}"
for module in "${FAILED_BUILDS[@]}"; do
    echo "   - $module"
done
echo ""
echo "Test Results:"
echo "✅ Successful tests: ${#SUCCESSFUL_TESTS[@]}"
for module in "${SUCCESSFUL_TESTS[@]}"; do
    echo "   - $module"
done
echo ""
echo "❌ Failed tests: ${#FAILED_TESTS[@]}"
for module in "${FAILED_TESTS[@]}"; do
    echo "   - $module"
done
echo ""
echo "================================================"

# Exit with appropriate code
if [ ${#FAILED_BUILDS[@]} -eq 0 ] && [ ${#FAILED_TESTS[@]} -eq 0 ]; then
    echo "✅ All builds and tests PASSED!"
    exit 0
else
    echo "❌ Some builds or tests FAILED!"
    exit 1
fi
