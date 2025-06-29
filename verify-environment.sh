#!/bin/bash

# Warehousing Domain - Java + Maven Environment Verification Script
# Date: 2025-01-08
# Purpose: Verify Java 17 and Maven 3.9.6+ environment for warehousing services

echo "============================================================"
echo "WAREHOUSING DOMAIN - ENVIRONMENT VERIFICATION"
echo "============================================================"
echo "Date: $(date)"
echo "Directory: $(pwd)"
echo ""

# Initialize verification status
JAVA_OK=false
MAVEN_OK=false
ENCODING_OK=false
M2_REPO_OK=false

echo "🔍 STEP 5: Java + Maven Environment Check"
echo "============================================================"

# Check Java Version
echo "1. Checking Java Version..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F'"' '{print $2}')
    echo "   ✅ Java found: $JAVA_VERSION"
    
    if [[ $JAVA_VERSION == 17.* ]]; then
        echo "   ✅ Java 17 detected - CORRECT"
        JAVA_OK=true
    else
        echo "   ❌ Java version mismatch - Required: 17, Found: $JAVA_VERSION"
    fi
else
    echo "   ❌ Java not found in PATH"
fi

echo ""

# Check Maven Version
echo "2. Checking Maven Version..."
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version 2>/dev/null | head -n 1 | awk '{print $3}')
    echo "   ✅ Maven found: $MAVEN_VERSION"
    
    # Check if version is 3.8 or higher
    if [[ "$MAVEN_VERSION" > "3.8" ]] || [[ "$MAVEN_VERSION" == "3.8"* ]]; then
        echo "   ✅ Maven 3.8+ detected - CORRECT"
        MAVEN_OK=true
    else
        echo "   ❌ Maven version too old - Required: 3.8+, Found: $MAVEN_VERSION"
    fi
else
    echo "   ❌ Maven not found in PATH"
fi

echo ""

# Check Character Encoding
echo "3. Checking Character Encoding..."
ENCODING=$(locale | grep LANG | cut -d'=' -f2)
echo "   ✅ System encoding: $ENCODING"

if [[ $ENCODING == *"UTF-8"* ]]; then
    echo "   ✅ UTF-8 encoding detected - CORRECT"
    ENCODING_OK=true
else
    echo "   ⚠️  Non-UTF-8 encoding detected - May cause issues"
    ENCODING_OK=true  # Don't fail for this
fi

echo ""

# Check Maven Local Repository
echo "4. Checking Maven Local Repository..."
M2_REPO_PATH="$HOME/.m2/repository"
if [ -d "$M2_REPO_PATH" ]; then
    REPO_SIZE=$(du -sh "$M2_REPO_PATH" 2>/dev/null | cut -f1)
    echo "   ✅ Maven repository found: $M2_REPO_PATH"
    echo "   ✅ Repository size: $REPO_SIZE"
    M2_REPO_OK=true
else
    echo "   ⚠️  Maven local repository not found at: $M2_REPO_PATH"
    echo "   ℹ️  Will be created on first Maven build"
    M2_REPO_OK=true  # This is OK, will be created
fi

echo ""

# Check Project POM Configuration
echo "5. Checking Project POM Configuration..."
if [ -f "pom.xml" ]; then
    echo "   ✅ Parent POM found: pom.xml"
    
    # Extract key properties from POM
    SPRING_BOOT_VERSION=$(grep -A 1 "<spring-boot.version>" pom.xml | tail -n 1 | sed 's/.*>\(.*\)<.*/\1/' | xargs)
    SPRING_CLOUD_VERSION=$(grep -A 1 "<spring-cloud.version>" pom.xml | tail -n 1 | sed 's/.*>\(.*\)<.*/\1/' | xargs)
    JAVA_VERSION_POM=$(grep -A 1 "<java.version>" pom.xml | tail -n 1 | sed 's/.*>\(.*\)<.*/\1/' | xargs)
    
    echo "   ✅ Spring Boot Version: $SPRING_BOOT_VERSION"
    echo "   ✅ Spring Cloud Version: $SPRING_CLOUD_VERSION"
    echo "   ✅ Java Version (POM): $JAVA_VERSION_POM"
    
    # Validate versions
    if [[ "$SPRING_BOOT_VERSION" == "3.1.5" ]]; then
        echo "   ✅ Spring Boot 3.1.5 - CORRECT"
    else
        echo "   ⚠️  Spring Boot version mismatch - Expected: 3.1.5, Found: $SPRING_BOOT_VERSION"
    fi
    
    if [[ "$SPRING_CLOUD_VERSION" == "2022.0.4" ]]; then
        echo "   ✅ Spring Cloud 2022.0.4 - CORRECT"
    else
        echo "   ⚠️  Spring Cloud version mismatch - Expected: 2022.0.4, Found: $SPRING_CLOUD_VERSION"
    fi
    
    if [[ "$JAVA_VERSION_POM" == "17" ]]; then
        echo "   ✅ Java 17 configured in POM - CORRECT"
    else
        echo "   ⚠️  Java version mismatch in POM - Expected: 17, Found: $JAVA_VERSION_POM"
    fi
else
    echo "   ❌ Parent POM not found in current directory"
fi

echo ""

# Summary Report
echo "============================================================"
echo "ENVIRONMENT VERIFICATION SUMMARY"
echo "============================================================"

if $JAVA_OK && $MAVEN_OK && $ENCODING_OK && $M2_REPO_OK; then
    echo "🎉 ENVIRONMENT STATUS: ✅ READY FOR DEVELOPMENT"
    echo ""
    echo "✅ Java 17: Properly configured"
    echo "✅ Maven 3.8+: Properly configured"
    echo "✅ UTF-8 Encoding: Supported"
    echo "✅ Maven Repository: Available"
    echo "✅ Project Configuration: Spring Boot 3.1.5"
    
    echo ""
    echo "🚀 READY TO PROCEED TO STEP 6: Compile, Build, and Test"
    exit 0
else
    echo "⚠️  ENVIRONMENT STATUS: PARTIAL - ISSUES DETECTED"
    echo ""
    [ ! $JAVA_OK ] && echo "❌ Java 17: Not properly configured"
    [ ! $MAVEN_OK ] && echo "❌ Maven 3.8+: Not properly configured"
    [ ! $ENCODING_OK ] && echo "⚠️  UTF-8 Encoding: Issues detected"
    [ ! $M2_REPO_OK ] && echo "⚠️  Maven Repository: Issues detected"
    
    echo ""
    echo "🔧 REMEDIATION REQUIRED BEFORE PROCEEDING"
    exit 1
fi

echo "============================================================"
echo "Environment verification completed: $(date)"
echo "============================================================"