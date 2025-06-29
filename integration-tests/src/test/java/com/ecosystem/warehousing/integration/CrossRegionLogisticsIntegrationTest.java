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
