#!/bin/bash

# WORKING SERVICES PROTECTION SCRIPT
# This script validates that all confirmed working services remain functional

echo "🛡️  VALIDATING WORKING SERVICES PROTECTION"
echo "=========================================="

# List of confirmed working services
WORKING_SERVICES=(
    "warehousing-shared"
    "config-server-enterprise" 
    "billing-service"
    "warehouse-analytics"
    "warehouse-operations"
    "warehouse-onboarding"
    "self-storage-service"
    "warehousing-production"
    "warehousing-staging"
    "shared-infrastructure-test"
    "central-configuration-test"
    "integration-tests"
    "cross-region-logistics-service"
)

PASSED=0
FAILED=0
FAILED_SERVICES=()

# Test each working service
for service in "${WORKING_SERVICES[@]}"; do
    echo "Testing: $service"
    
    if [ -d "$service" ]; then
        cd "$service"
        if timeout 60 mvn compile -q --no-transfer-progress >/dev/null 2>&1; then
            echo "✅ $service - STILL WORKING"
            ((PASSED++))
        else
            echo "❌ $service - BROKEN! REQUIRES IMMEDIATE RESTORATION"
            FAILED_SERVICES+=("$service")
            ((FAILED++))
        fi
        cd ..
    else
        echo "⚠️  $service - Directory not found"
        ((FAILED++))
    fi
done

echo ""
echo "=========================================="
echo "🛡️  PROTECTION VALIDATION RESULTS"
echo "=========================================="
echo "✅ WORKING: $PASSED services"
echo "❌ BROKEN:  $FAILED services"

if [ $FAILED -gt 0 ]; then
    echo ""
    echo "🚨 CRITICAL: WORKING SERVICES HAVE BEEN DAMAGED!"
    echo "Failed services:"
    for failed_service in "${FAILED_SERVICES[@]}"; do
        echo "  - $failed_service"
    done
    echo ""
    echo "🔧 IMMEDIATE ACTION REQUIRED:"
    echo "1. Stop all development work"
    echo "2. Restore from backups in WORKING_SERVICE_BACKUPS/"
    echo "3. Re-run this validation script"
    echo "4. Only proceed when all working services pass"
    exit 1
else
    echo ""
    echo "🎉 ALL WORKING SERVICES PROTECTED - SAFE TO CONTINUE"
    exit 0
fi