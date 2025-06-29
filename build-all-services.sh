#!/bin/bash

# Warehousing Domain - Build and Test Script
# Date: 2025-01-08
# Purpose: Build and test all 11 target services in the warehousing domain

echo "============================================================"
echo "WAREHOUSING DOMAIN - BUILD AND TEST EXECUTION"
echo "============================================================"
echo "Date: $(date)"
echo "Directory: $(pwd)"
echo ""

# Define target services (excluding self-storage-service)
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

FRONTEND_SERVICES=(
    "global-hq-admin"
    "regional-admin"
    "staff-mobile-app"
)

# Initialize counters
TOTAL_SERVICES=0
SUCCESS_COUNT=0
FAILURE_COUNT=0
SKIPPED_COUNT=0

# Create results directory
RESULTS_DIR="build-test-results"
mkdir -p $RESULTS_DIR

# Function to build Java service
build_java_service() {
    local service=$1
    echo ""
    echo "🔨 Building Java Service: $service"
    echo "============================================================"
    
    if [ -d "$service" ]; then
        cd "$service"
        
        # Check if pom.xml exists
        if [ -f "pom.xml" ]; then
            echo "✅ Found pom.xml for $service"
            
            # Run Maven clean install
            echo "📦 Running: mvn clean install"
            
            # Execute build and capture output
            mvn clean install -DskipTests=false > "../$RESULTS_DIR/${service}-build.log" 2>&1
            BUILD_EXIT_CODE=$?
            
            if [ $BUILD_EXIT_CODE -eq 0 ]; then
                echo "✅ BUILD SUCCESS: $service"
                ((SUCCESS_COUNT++))
                
                # Extract test results
                if grep -q "Tests run:" "../$RESULTS_DIR/${service}-build.log"; then
                    TEST_SUMMARY=$(grep "Tests run:" "../$RESULTS_DIR/${service}-build.log" | tail -n 1)
                    echo "🧪 Test Results: $TEST_SUMMARY"
                fi
                
                # Check if JAR was created
                if ls target/*.jar 1> /dev/null 2>&1; then
                    JAR_FILE=$(ls target/*.jar | head -n 1)
                    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
                    echo "📦 JAR Created: $(basename $JAR_FILE) ($JAR_SIZE)"
                fi
            else
                echo "❌ BUILD FAILED: $service (Exit code: $BUILD_EXIT_CODE)"
                ((FAILURE_COUNT++))
                
                # Extract error summary
                echo "❌ Error Summary:"
                tail -n 20 "../$RESULTS_DIR/${service}-build.log" | grep -E "(ERROR|FAILURE|Failed)"
            fi
        else
            echo "⚠️  No pom.xml found for $service - SKIPPING"
            ((SKIPPED_COUNT++))
        fi
        
        cd ..
    else
        echo "❌ Directory not found: $service"
        ((FAILURE_COUNT++))
    fi
    
    ((TOTAL_SERVICES++))
}

# Function to check frontend service
check_frontend_service() {
    local service=$1
    echo ""
    echo "🌐 Checking Frontend Service: $service"
    echo "============================================================"
    
    if [ -d "$service" ]; then
        cd "$service"
        
        # Check if package.json exists
        if [ -f "package.json" ]; then
            echo "✅ Found package.json for $service"
            
            # Check for node_modules
            if [ -d "node_modules" ]; then
                echo "✅ Dependencies installed"
                
                # Try to run build
                if grep -q '"build"' package.json; then
                    echo "📦 Build script available"
                    ((SUCCESS_COUNT++))
                else
                    echo "⚠️  No build script found"
                    ((SKIPPED_COUNT++))
                fi
            else
                echo "⚠️  Dependencies not installed - run 'npm install'"
                ((SKIPPED_COUNT++))
            fi
        else
            echo "❌ No package.json found for $service - NOT IMPLEMENTED"
            echo "❌ $service" >> "../$RESULTS_DIR/not-implemented-services.txt"
            ((FAILURE_COUNT++))
        fi
        
        cd ..
    else
        echo "❌ Directory not found: $service"
        ((FAILURE_COUNT++))
    fi
    
    ((TOTAL_SERVICES++))
}

# Main execution
echo "🚀 Starting build process for warehousing services..."
echo ""

# First, build the parent POM
echo "🔨 Building Parent POM"
echo "============================================================"
mvn clean install -N > "$RESULTS_DIR/parent-pom-build.log" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Parent POM build successful"
else
    echo "❌ Parent POM build failed - continuing with individual services"
fi

# Build shared library first (other services may depend on it)
if [ -d "warehousing-shared" ]; then
    build_java_service "warehousing-shared"
fi

# Build Java services
echo ""
echo "📋 Building Java Microservices..."
for service in "${JAVA_SERVICES[@]}"; do
    build_java_service "$service"
done

# Check frontend services
echo ""
echo "📋 Checking Frontend Applications..."
for service in "${FRONTEND_SERVICES[@]}"; do
    check_frontend_service "$service"
done

# Generate summary report
echo ""
echo "============================================================"
echo "BUILD AND TEST SUMMARY REPORT"
echo "============================================================"
echo "Total Services Processed: $TOTAL_SERVICES"
echo "✅ Successful Builds: $SUCCESS_COUNT"
echo "❌ Failed Builds: $FAILURE_COUNT"
echo "⚠️  Skipped/Not Implemented: $SKIPPED_COUNT"
echo ""

# Create detailed summary report
cat > "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md" << EOF
# Warehousing Domain - Build and Test Log

## Date: $(date)
## Summary: $SUCCESS_COUNT/$TOTAL_SERVICES services built successfully

### Build Results:
- ✅ Successful: $SUCCESS_COUNT
- ❌ Failed: $FAILURE_COUNT
- ⚠️ Skipped/Not Implemented: $SKIPPED_COUNT

### Service Details:
EOF

# Add service-specific results
for service in "${JAVA_SERVICES[@]}" "${FRONTEND_SERVICES[@]}"; do
    echo "" >> "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
    echo "#### $service" >> "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
    if [ -f "$RESULTS_DIR/${service}-build.log" ]; then
        if grep -q "BUILD SUCCESS" "$RESULTS_DIR/${service}-build.log"; then
            echo "- Status: ✅ BUILD SUCCESS" >> "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
            TEST_RESULT=$(grep "Tests run:" "$RESULTS_DIR/${service}-build.log" | tail -n 1)
            if [ ! -z "$TEST_RESULT" ]; then
                echo "- Tests: $TEST_RESULT" >> "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
            fi
        else
            echo "- Status: ❌ BUILD FAILED" >> "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
        fi
    else
        echo "- Status: ⚠️ Not built (missing implementation or dependencies)" >> "$RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
    fi
done

# Create test results summary
cat > "$RESULTS_DIR/WAREHOUSING_TEST_RESULTS.md" << EOF
# Warehousing Domain - Test Results Summary

## Date: $(date)
## Test Execution Report

### Overall Test Status:
EOF

# Extract test results from build logs
for service in "${JAVA_SERVICES[@]}"; do
    if [ -f "$RESULTS_DIR/${service}-build.log" ]; then
        echo "" >> "$RESULTS_DIR/WAREHOUSING_TEST_RESULTS.md"
        echo "### $service" >> "$RESULTS_DIR/WAREHOUSING_TEST_RESULTS.md"
        
        TEST_LINE=$(grep "Tests run:" "$RESULTS_DIR/${service}-build.log" | tail -n 1)
        if [ ! -z "$TEST_LINE" ]; then
            echo "- $TEST_LINE" >> "$RESULTS_DIR/WAREHOUSING_TEST_RESULTS.md"
        else
            echo "- No test results found" >> "$RESULTS_DIR/WAREHOUSING_TEST_RESULTS.md"
        fi
    fi
done

echo ""
echo "📊 Detailed logs saved in: $RESULTS_DIR/"
echo "📋 Build Log: $RESULTS_DIR/WAREHOUSING_BUILD_LOG.md"
echo "🧪 Test Results: $RESULTS_DIR/WAREHOUSING_TEST_RESULTS.md"
echo ""

# Exit with appropriate code
if [ $FAILURE_COUNT -gt 0 ]; then
    echo "⚠️  Build completed with failures"
    exit 1
else
    echo "✅ Build completed successfully"
    exit 0
fi