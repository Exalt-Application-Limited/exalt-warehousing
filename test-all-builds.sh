#!/bin/bash

WAREHOUSING_PATH="/mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing"
SERVICES=(
    "billing-service"
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
    "self-storage-service"
    "warehousing-shared"
)

echo "=== Warehousing Build Test Results ==="
echo "Date: $(date)"
echo ""

for service in "${SERVICES[@]}"; do
    echo "Testing: $service"
    cd "$WAREHOUSING_PATH/$service"
    
    # Test compilation
    if mvn clean compile -q 2>/dev/null; then
        echo "✅ $service: COMPILATION SUCCESS"
        
        # Test unit tests
        if mvn test -q 2>/dev/null; then
            echo "✅ $service: TESTS PASS"
        else
            echo "❌ $service: TESTS FAIL"
        fi
    else
        echo "❌ $service: COMPILATION FAILED"
        # Show compilation errors
        mvn compile 2>&1 | grep -E "(ERROR|error:|cannot find symbol|package.*does not exist)" | head -10
    fi
    echo "---"
done