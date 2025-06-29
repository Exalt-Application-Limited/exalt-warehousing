package com.exalt.warehousing.billingservice;

import com.exalt.warehousing.billing.BillingServiceApplication;
import com.exalt.warehousing.billingservice.config.BillingTestConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base integration test class with common configuration
 */
@SpringBootTest(
    classes = {BillingServiceApplication.class, BillingTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public abstract class BaseIntegrationTest {
    // Common test setup can be added here
}
