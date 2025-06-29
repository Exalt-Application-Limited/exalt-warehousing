#!/bin/bash

# Quick build check for warehousing services
echo "============================================================"
echo "WAREHOUSING DOMAIN - QUICK BUILD CHECK"
echo "============================================================"
echo ""

# Define target services
JAVA_SERVICES=(
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

# Quick compile check
for service in "${JAVA_SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        echo "🔍 Checking $service..."
        cd "$service"
        
        # Just compile, don't run tests
        mvn compile -q 2>&1
        if [ $? -eq 0 ]; then
            echo "✅ $service - Compiles successfully"
        else
            echo "❌ $service - Compilation errors"
        fi
        cd ..
    else
        echo "⚠️  $service - Missing or no pom.xml"
    fi
done

echo ""
echo "✅ Quick check complete"