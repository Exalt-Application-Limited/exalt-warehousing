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
