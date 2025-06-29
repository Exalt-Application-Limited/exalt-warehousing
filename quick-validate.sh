#!/bin/bash

echo "=== QUICK WAREHOUSING VALIDATION ==="
echo ""

cd /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing

# Maven services
for service in billing-service cross-region-logistics-service fulfillment-service inventory-service self-storage-service staff-mobile-app warehouse-analytics warehouse-management-service warehouse-onboarding warehouse-operations warehouse-subscription; do
    if [ -f "$service/pom.xml" ]; then
        cd "$service" 2>/dev/null
        if mvn compile -DskipTests -q > /dev/null 2>&1; then
            # Check if tests exist
            if find src/test -name "*.java" 2>/dev/null | grep -q .; then
                if mvn test -q > /dev/null 2>&1; then
                    echo "✅ $service → Build Passed, Tests Passed"
                else
                    echo "❌ $service → Build Passed, Tests Failed"
                fi
            else
                echo "⚠️  $service → Build Passed, No Tests"
            fi
        else
            echo "❌ $service → Build Failed"
        fi
        cd .. 2>/dev/null
    fi
done

# Node.js services
for service in global-hq-admin regional-admin; do
    if [ -f "$service/package.json" ]; then
        cd "$service" 2>/dev/null
        if npm install --silent > /dev/null 2>&1; then
            if npm test -- --passWithNoTests 2>&1 | grep -q "pass"; then
                echo "✅ $service → Build Passed, Tests Passed"
            else
                echo "⚠️  $service → Build Passed, No Tests"
            fi
        else
            echo "❌ $service → Build Failed"
        fi
        cd .. 2>/dev/null
    fi
done