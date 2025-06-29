#\!/bin/bash

echo "=== WAREHOUSING DOMAIN VALIDATION REPORT ==="
echo "Date: $(date)"
echo ""

SERVICES=(
    "billing-service"
    "cross-region-logistics-service"
    "fulfillment-service"
    "global-hq-admin"
    "inventory-service"
    "regional-admin"
    "self-storage-service"
    "staff-mobile-app"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
)

for service in "${SERVICES[@]}"; do
    echo "----------------------------------------"
    echo "Validating: $service"
    cd "$service" 2>/dev/null || { echo "❌ $service → Directory not found"; continue; }
    
    if [ -f "pom.xml" ]; then
        echo "Type: Maven project"
        
        # Compile
        mvn compile -DskipTests > compile.log 2>&1
        if [ $? -eq 0 ]; then
            echo "Build: ✅ PASSED"
            
            # Test
            mvn test > test.log 2>&1
            if [ $? -eq 0 ]; then
                echo "Tests: ✅ PASSED"
                echo "Result: ✅ $service → Build Passed, Tests Passed"
            else
                echo "Tests: ❌ FAILED"
                # Get failure details
                grep -E "(Tests run: < /dev/null | Failed tests:|ERROR)" test.log | head -5
                echo "Result: ❌ $service → Build Passed, Tests Failed"
            fi
        else
            echo "Build: ❌ FAILED"
            # Get failure details
            grep -E "(ERROR|error:|cannot find symbol|Failed to execute)" compile.log | head -5
            echo "Result: ❌ $service → Build Failed"
        fi
        
    elif [ -f "package.json" ]; then
        echo "Type: Node.js project"
        
        # Install dependencies
        npm install > install.log 2>&1
        if [ $? -eq 0 ]; then
            echo "Install: ✅ PASSED"
            
            # Check if build script exists
            if npm run | grep -q "build"; then
                npm run build > build.log 2>&1
                if [ $? -eq 0 ]; then
                    echo "Build: ✅ PASSED"
                else
                    echo "Build: ❌ FAILED"
                fi
            else
                echo "Build: ⚠️  No build script"
            fi
            
            # Test
            if npm run | grep -q "test"; then
                npm test > test.log 2>&1
                if [ $? -eq 0 ]; then
                    echo "Tests: ✅ PASSED"
                    echo "Result: ✅ $service → Build Passed, Tests Passed"
                else
                    echo "Tests: ❌ FAILED"
                    grep -E "(FAIL|Error:|failed)" test.log | head -5
                    echo "Result: ❌ $service → Build Passed, Tests Failed"
                fi
            else
                echo "Tests: ⚠️  No test script"
                echo "Result: ⚠️  $service → Build Passed, No Tests"
            fi
        else
            echo "Install: ❌ FAILED"
            grep -E "(ERROR|error:|npm ERR!)" install.log | head -5
            echo "Result: ❌ $service → Build Failed"
        fi
    else
        echo "❌ $service → No build file (pom.xml or package.json) found"
    fi
    
    cd ..
done

echo ""
echo "=== VALIDATION COMPLETE ==="
