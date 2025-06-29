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
