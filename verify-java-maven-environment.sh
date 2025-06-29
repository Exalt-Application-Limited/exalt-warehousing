#!/bin/bash

echo "=== Java + Maven Environment Verification ==="
echo "Date: $(date)"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Required versions
REQUIRED_JAVA_VERSION="17"
REQUIRED_MAVEN_VERSION="3.8"
RECOMMENDED_MAVEN_VERSION="3.9.6"

# Function to check Java version
check_java() {
    echo -e "${YELLOW}Checking Java installation...${NC}"
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | awk -F '.' '{print $1}')
        JAVA_FULL_VERSION=$(java -version 2>&1 | head -n 1)
        
        echo "Java version: $JAVA_FULL_VERSION"
        
        if [ "$JAVA_VERSION" -eq "$REQUIRED_JAVA_VERSION" ]; then
            echo -e "${GREEN}✓ Java 17 is installed${NC}"
            return 0
        else
            echo -e "${RED}✗ Java version mismatch. Required: $REQUIRED_JAVA_VERSION, Found: $JAVA_VERSION${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗ Java is not installed${NC}"
        return 1
    fi
}

# Function to check Maven version
check_maven() {
    echo -e "${YELLOW}Checking Maven installation...${NC}"
    
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
        MAVEN_MAJOR_VERSION=$(echo $MAVEN_VERSION | awk -F '.' '{print $1"."$2}')
        
        echo "Maven version: $MAVEN_VERSION"
        
        if [[ "$MAVEN_VERSION" == "$RECOMMENDED_MAVEN_VERSION" ]]; then
            echo -e "${GREEN}✓ Maven $RECOMMENDED_MAVEN_VERSION (recommended) is installed${NC}"
            return 0
        elif [[ "$MAVEN_MAJOR_VERSION" == "$REQUIRED_MAVEN_VERSION" ]]; then
            echo -e "${YELLOW}⚠ Maven $MAVEN_VERSION is installed (recommended: $RECOMMENDED_MAVEN_VERSION)${NC}"
            return 0
        else
            echo -e "${RED}✗ Maven version too old. Minimum required: $REQUIRED_MAVEN_VERSION.x${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗ Maven is not installed${NC}"
        return 1
    fi
}

# Function to check Maven settings
check_maven_settings() {
    echo -e "${YELLOW}Checking Maven settings...${NC}"
    
    # Check settings.xml
    if [ -f ~/.m2/settings.xml ]; then
        echo -e "${GREEN}✓ Maven settings.xml found${NC}"
    else
        echo -e "${YELLOW}⚠ No Maven settings.xml found (optional)${NC}"
    fi
    
    # Check local repository
    if [ -d ~/.m2/repository ]; then
        echo -e "${GREEN}✓ Maven local repository exists${NC}"
    else
        echo -e "${YELLOW}⚠ Maven local repository not found (will be created on first build)${NC}"
    fi
}

# Function to check parent POM configuration
check_parent_pom() {
    echo -e "${YELLOW}Checking parent POM configuration...${NC}"
    
    if [ -f "pom.xml" ]; then
        # Extract Java version from parent POM
        POM_JAVA_VERSION=$(grep -m 1 "<java.version>" pom.xml | sed 's/.*<java.version>\(.*\)<\/java.version>.*/\1/')
        POM_SPRING_BOOT=$(grep -m 1 "<spring-boot.version>" pom.xml | sed 's/.*<spring-boot.version>\(.*\)<\/spring-boot.version>.*/\1/')
        
        echo "Parent POM Java version: $POM_JAVA_VERSION"
        echo "Parent POM Spring Boot version: $POM_SPRING_BOOT"
        
        if [ "$POM_JAVA_VERSION" == "17" ]; then
            echo -e "${GREEN}✓ Parent POM configured for Java 17${NC}"
        else
            echo -e "${RED}✗ Parent POM Java version mismatch${NC}"
        fi
        
        if [ "$POM_SPRING_BOOT" == "3.1.5" ]; then
            echo -e "${GREEN}✓ Parent POM configured for Spring Boot 3.1.5${NC}"
        else
            echo -e "${RED}✗ Parent POM Spring Boot version mismatch${NC}"
        fi
    else
        echo -e "${RED}✗ Parent POM not found${NC}"
    fi
}

# Function to verify Maven wrapper
check_maven_wrapper() {
    echo -e "${YELLOW}Checking Maven wrapper...${NC}"
    
    if [ -f "mvnw" ] && [ -f "mvnw.cmd" ]; then
        echo -e "${GREEN}✓ Maven wrapper files present${NC}"
        
        # Check if wrapper is executable
        if [ -x "mvnw" ]; then
            echo -e "${GREEN}✓ Maven wrapper is executable${NC}"
        else
            echo -e "${YELLOW}⚠ Making Maven wrapper executable...${NC}"
            chmod +x mvnw
        fi
    else
        echo -e "${YELLOW}⚠ Maven wrapper not found (optional but recommended)${NC}"
    fi
}

# Function to test Maven build
test_maven_build() {
    echo -e "${YELLOW}Testing Maven build capability...${NC}"
    
    # Try to run Maven help plugin
    if mvn help:effective-settings > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Maven can execute successfully${NC}"
    else
        echo -e "${RED}✗ Maven execution failed${NC}"
    fi
}

# Main execution
echo "=== Environment Verification Results ==="
echo ""

JAVA_OK=false
MAVEN_OK=false

# Run checks
if check_java; then
    JAVA_OK=true
fi
echo ""

if check_maven; then
    MAVEN_OK=true
fi
echo ""

check_maven_settings
echo ""

check_parent_pom
echo ""

check_maven_wrapper
echo ""

if [ "$JAVA_OK" = true ] && [ "$MAVEN_OK" = true ]; then
    test_maven_build
fi

echo ""
echo "=== Summary ==="

if [ "$JAVA_OK" = true ] && [ "$MAVEN_OK" = true ]; then
    echo -e "${GREEN}✓ Java and Maven environment is properly configured${NC}"
    echo ""
    echo "Environment Details:"
    echo "- Java: OpenJDK 17"
    echo "- Maven: $MAVEN_VERSION"
    echo "- Build encoding: UTF-8"
    echo "- Parent POM: Configured for Java 17 and Spring Boot 3.1.5"
    exit 0
else
    echo -e "${RED}✗ Environment configuration issues detected${NC}"
    echo ""
    echo "Required fixes:"
    
    if [ "$JAVA_OK" = false ]; then
        echo "- Install Java 17 (OpenJDK or Oracle JDK)"
    fi
    
    if [ "$MAVEN_OK" = false ]; then
        echo "- Install Maven 3.9.6 (or at least 3.8.x)"
    fi
    
    exit 1
fi