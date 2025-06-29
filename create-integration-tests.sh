#!/bin/bash

echo "=== CREATING INTEGRATION TESTS FOR WAREHOUSING SERVICES ==="

cd /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing

# Create integration test directory
mkdir -p integration-tests/src/test/java/com/ecosystem/warehousing/integration
mkdir -p integration-tests/src/test/resources

# Create integration test POM
cat > integration-tests/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.ecosystem</groupId>
        <artifactId>warehousing-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <groupId>com.ecosystem.warehousing</groupId>
    <artifactId>integration-tests</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>Warehousing Integration Tests</name>

    <dependencies>
        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- REST Assured for API Testing -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- Testcontainers -->
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>1.19.1</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>1.19.1</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <version>1.19.1</version>
            <scope>test</scope>
        </dependency>
        
        <!-- WireMock for mocking external services -->
        <dependency>
            <groupId>com.github.tomakehurst</groupId>
            <artifactId>wiremock-jre8</artifactId>
            <version>2.35.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
EOF

# Create base integration test class
cat > integration-tests/src/test/java/com/ecosystem/warehousing/integration/BaseIntegrationTest.java << 'EOF'
package com.ecosystem.warehousing.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("warehousing_test")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeAll
    static void setUp() {
        postgres.start();
        kafka.start();
    }
}
EOF

# Create Billing-Inventory Integration Test
cat > integration-tests/src/test/java/com/ecosystem/warehousing/integration/BillingInventoryIntegrationTest.java << 'EOF'
package com.ecosystem.warehousing.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BillingInventoryIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setUpWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Should create billing account when warehouse is created")
    void testWarehouseCreation_CreatesBillingAccount() {
        // Mock inventory service response
        stubFor(post(urlEqualTo("/api/v1/inventory/warehouse"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"123e4567-e89b-12d3-a456-426614174000\",\"name\":\"Test Warehouse\"}")));

        // Create warehouse through billing service
        given()
                .contentType(ContentType.JSON)
                .body("{\"warehouseName\":\"Test Warehouse\",\"planType\":\"BASIC\"}")
                .when()
                .post("/api/v1/billing/accounts")
                .then()
                .statusCode(201)
                .body("warehouseId", notNullValue())
                .body("accountStatus", equalTo("ACTIVE"));
    }

    @Test
    @Order(2)
    @DisplayName("Should update inventory when subscription changes")
    void testSubscriptionUpdate_UpdatesInventoryLimits() {
        // Mock inventory service update
        stubFor(put(urlMatching("/api/v1/inventory/warehouse/.*/limits"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"storageLimit\":\"UPDATED\"}")));

        // Update subscription plan
        given()
                .contentType(ContentType.JSON)
                .body("{\"planType\":\"PREMIUM\"}")
                .when()
                .put("/api/v1/billing/subscriptions/123e4567-e89b-12d3-a456-426614174000")
                .then()
                .statusCode(200)
                .body("planType", equalTo("PREMIUM"));

        // Verify inventory service was called
        verify(putRequestedFor(urlMatching("/api/v1/inventory/warehouse/.*/limits")));
    }
}
EOF

# Create Cross-Region Logistics Integration Test
cat > integration-tests/src/test/java/com/ecosystem/warehousing/integration/CrossRegionLogisticsIntegrationTest.java << 'EOF'
package com.ecosystem.warehousing.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

@EmbeddedKafka(partitions = 1, topics = {"transfer-events", "inventory-updates"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrossRegionLogisticsIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @Order(1)
    @DisplayName("Should initiate cross-region transfer and publish events")
    void testCrossRegionTransfer_PublishesEvents() {
        // Create transfer request
        String transferRequest = """
                {
                    "sourceWarehouseId": "warehouse-1",
                    "destinationWarehouseId": "warehouse-2",
                    "items": [
                        {"productId": "product-1", "quantity": 100}
                    ]
                }
                """;

        // Initiate transfer
        String transferId = given()
                .contentType(ContentType.JSON)
                .body(transferRequest)
                .when()
                .post("/api/v1/transfers")
                .then()
                .statusCode(201)
                .body("transferStatus", equalTo("INITIATED"))
                .extract()
                .path("transferId");

        // Verify event was published
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            given()
                    .when()
                    .get("/api/v1/transfers/" + transferId)
                    .then()
                    .body("transferStatus", equalTo("IN_TRANSIT"));
        });
    }

    @Test
    @Order(2)
    @DisplayName("Should optimize shipping route based on multiple warehouses")
    void testShippingRouteOptimization() {
        // Request optimal route
        given()
                .queryParam("origin", "warehouse-1")
                .queryParam("destination", "warehouse-3")
                .queryParam("weight", "1000")
                .when()
                .get("/api/v1/shipping-routes/optimize")
                .then()
                .statusCode(200)
                .body("route", hasSize(greaterThan(0)))
                .body("estimatedDays", notNullValue())
                .body("estimatedCost", notNullValue());
    }
}
EOF

# Create Service Health Check Integration Test
cat > integration-tests/src/test/java/com/ecosystem/warehousing/integration/ServiceHealthIntegrationTest.java << 'EOF'
package com.ecosystem.warehousing.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ServiceHealthIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Should verify all services are healthy")
    void testAllServicesHealth() {
        // Check actuator health endpoint
        given()
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("components.db.status", equalTo("UP"))
                .body("components.kafka.status", equalTo("UP"));
    }

    @Test
    @DisplayName("Should verify service info endpoint")
    void testServiceInfo() {
        given()
                .when()
                .get("/actuator/info")
                .then()
                .statusCode(200)
                .body("app.name", notNullValue())
                .body("app.version", notNullValue());
    }
}
EOF

# Create test configuration
cat > integration-tests/src/test/resources/application-integration-test.yml << 'EOF'
spring:
  profiles:
    active: integration-test
    
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    
  kafka:
    consumer:
      auto-offset-reset: earliest
      group-id: integration-test-group
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      
management:
  endpoints:
    web:
      exposure:
        include: health,info
        
logging:
  level:
    root: INFO
    com.ecosystem: DEBUG
    org.testcontainers: INFO
    
# Mock service URLs
services:
  inventory:
    url: http://localhost:8089
  billing:
    url: http://localhost:8089
  warehouse:
    url: http://localhost:8089
EOF

# Create Docker Compose for integration testing
cat > integration-tests/docker-compose.test.yml << 'EOF'
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: warehousing_test
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
    ports:
      - "5432:5432"
      
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"
      
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
      
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
EOF

# Create test runner script
cat > integration-tests/run-integration-tests.sh << 'EOF'
#!/bin/bash

echo "Starting integration test environment..."
docker-compose -f docker-compose.test.yml up -d

echo "Waiting for services to be ready..."
sleep 10

echo "Running integration tests..."
mvn test -Dtest="*IntegrationTest"

echo "Stopping test environment..."
docker-compose -f docker-compose.test.yml down

echo "Integration tests complete!"
EOF

chmod +x integration-tests/run-integration-tests.sh

echo "✅ Integration test framework created!"
echo ""
echo "To run integration tests:"
echo "  cd integration-tests"
echo "  ./run-integration-tests.sh"