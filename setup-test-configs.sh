#!/bin/bash

echo "🧪 Setting up test configurations with mocked dependencies..."
echo ""

# Java services
JAVA_SERVICES=(
    "billing-service"
    "config-server-enterprise"
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "self-storage-service"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
    "warehousing-production"
    "warehousing-shared"
    "warehousing-staging"
    "shared-infrastructure-test"
)

# Create test configuration for Java services
create_test_config() {
    local service=$1
    
    if [ -d "$service" ]; then
        # Create src/test/resources directory
        mkdir -p "$service/src/test/resources"
        
        # Create application-test.yml
        cat > "$service/src/test/resources/application-test.yml" << EOF
# Test Configuration for $service
spring:
  application:
    name: $service-test
  profiles:
    active: test
  
  # H2 Database for testing
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
  
  h2:
    console:
      enabled: true
  
  # Kafka Test Configuration
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
    producer:
      retries: 0
  
  # Redis Test Configuration
  data:
    redis:
      host: localhost
      port: 6379
      password:
      timeout: 60000ms
  
  # Cloud Config disabled for tests
  cloud:
    config:
      enabled: false
    discovery:
      enabled: false

# Eureka disabled for tests
eureka:
  client:
    enabled: false

# Security configuration for tests
jwt:
  secret: test-secret-key-for-unit-tests-only
  expiration: 86400000

# Logging for tests
logging:
  level:
    com.exalt.warehousing: DEBUG
    org.springframework.test: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# Management endpoints for tests
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

# Test specific properties
test:
  mock:
    external-api: true
    kafka: true
    redis: true
EOF
        
        # Create TestConfiguration class
        mkdir -p "$service/src/test/java/com/exalt/warehousing/${service//-/}/config"
        cat > "$service/src/test/java/com/exalt/warehousing/${service//-/}/config/TestConfiguration.java" << EOF
package com.exalt.warehousing.${service//-/}.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.config.client.ConfigClientProperties;

/**
 * Test configuration for mocking external dependencies
 */
@TestConfiguration
public class TestConfiguration {
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    
    @MockBean
    private EurekaClientConfigBean eurekaClientConfigBean;
    
    @MockBean
    private ConfigClientProperties configClientProperties;
}
EOF
        
        # Create abstract base test class
        cat > "$service/src/test/java/com/exalt/warehousing/${service//-/}/BaseIntegrationTest.java" << EOF
package com.exalt.warehousing.${service//-/};

import com.exalt.warehousing.${service//-/}.config.TestConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base integration test class with common configuration
 */
@SpringBootTest(
    classes = TestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public abstract class BaseIntegrationTest {
    // Common test setup can be added here
}
EOF
        
        echo "✅ Created test configuration for $service"
    fi
}

# Create Docker Compose for test dependencies
create_test_docker_compose() {
    cat > "docker-compose.test.yml" << 'EOF'
version: '3.8'

services:
  postgres-test:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: warehousing_test
      POSTGRES_USER: test_user
      POSTGRES_PASSWORD: test_pass
    ports:
      - "5433:5432"
    tmpfs:
      - /var/lib/postgresql/data
    networks:
      - test-network

  redis-test:
    image: redis:7-alpine
    ports:
      - "6380:6379"
    command: redis-server --appendonly yes
    tmpfs:
      - /data
    networks:
      - test-network

  kafka-test:
    image: confluentinc/cp-kafka:7.4.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-test:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    ports:
      - "9093:9092"
    depends_on:
      - zookeeper-test
    networks:
      - test-network

  zookeeper-test:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2182:2181"
    networks:
      - test-network

  mailhog-test:
    image: mailhog/mailhog:latest
    ports:
      - "1025:1025"
      - "8025:8025"
    networks:
      - test-network

networks:
  test-network:
    driver: bridge

volumes:
  postgres-test-data:
  redis-test-data:
EOF
    echo "✅ Created docker-compose.test.yml"
}

# Create test runner script
create_test_runner() {
    cat > "run-tests.sh" << 'EOF'
#!/bin/bash

echo "🧪 Running Warehousing Domain Tests..."
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test results
PASSED=0
FAILED=0
SKIPPED=0

# Start test dependencies
echo "🐳 Starting test dependencies..."
docker-compose -f docker-compose.test.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for test services to be ready..."
sleep 30

# Test services array
SERVICES=(
    "billing-service"
    "config-server-enterprise"
    "cross-region-logistics-service"
    "fulfillment-service"
    "inventory-service"
    "self-storage-service"
    "warehouse-analytics"
    "warehouse-management-service"
    "warehouse-onboarding"
    "warehouse-operations"
    "warehouse-subscription"
    "warehousing-shared"
    "shared-infrastructure-test"
)

# Run tests for each service
for service in "${SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        echo ""
        echo "🔍 Testing $service..."
        
        cd "$service"
        
        # Run Maven tests
        if mvn clean test -Dspring.profiles.active=test > "../test-results-$service.log" 2>&1; then
            echo -e "${GREEN}✅ $service: PASSED${NC}"
            ((PASSED++))
        else
            echo -e "${RED}❌ $service: FAILED${NC}"
            echo "   Check test-results-$service.log for details"
            ((FAILED++))
        fi
        
        cd ..
    else
        echo -e "${YELLOW}⚠️  $service: SKIPPED (no pom.xml found)${NC}"
        ((SKIPPED++))
    fi
done

# Test Node.js services
NODE_SERVICES=("global-hq-admin" "regional-admin" "staff-mobile-app")

for service in "${NODE_SERVICES[@]}"; do
    if [ -d "$service" ] && [ -f "$service/package.json" ]; then
        echo ""
        echo "🔍 Testing $service..."
        
        cd "$service"
        
        # Install dependencies if needed
        if [ ! -d "node_modules" ]; then
            echo "   Installing dependencies..."
            npm ci > "../npm-install-$service.log" 2>&1
        fi
        
        # Run tests
        if npm test -- --watchAll=false --coverage > "../test-results-$service.log" 2>&1; then
            echo -e "${GREEN}✅ $service: PASSED${NC}"
            ((PASSED++))
        else
            echo -e "${RED}❌ $service: FAILED${NC}"
            echo "   Check test-results-$service.log for details"
            ((FAILED++))
        fi
        
        cd ..
    else
        echo -e "${YELLOW}⚠️  $service: SKIPPED (no package.json found)${NC}"
        ((SKIPPED++))
    fi
done

# Stop test dependencies
echo ""
echo "🛑 Stopping test dependencies..."
docker-compose -f docker-compose.test.yml down

# Print summary
echo ""
echo "📊 Test Summary:"
echo -e "   ${GREEN}Passed: $PASSED${NC}"
echo -e "   ${RED}Failed: $FAILED${NC}"
echo -e "   ${YELLOW}Skipped: $SKIPPED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}💥 $FAILED test(s) failed!${NC}"
    exit 1
fi
EOF
    
    chmod +x "run-tests.sh"
    echo "✅ Created test runner script"
}

# Create test configurations for all Java services
for service in "${JAVA_SERVICES[@]}"; do
    create_test_config "$service"
done

# Create Docker Compose for test dependencies
create_test_docker_compose

# Create test runner script
create_test_runner

echo ""
echo "✅ Test configurations setup completed!"
echo ""
echo "📊 Summary:"
echo "- Created application-test.yml for all Java services"
echo "- Set up H2 database for testing"
echo "- Mocked Eureka, Config Server, Kafka, and Redis"
echo "- Created Docker Compose for test dependencies"
echo "- Created test runner script"
echo ""
echo "🚀 To run tests:"
echo "   ./run-tests.sh"
echo ""
echo "🐳 To start test dependencies only:"
echo "   docker-compose -f docker-compose.test.yml up -d"