package com.exalt.warehousing.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Warehouse Subscription Service
 * 
 * This service manages warehouse partner subscriptions, billing, usage tracking,
 * and subscription lifecycle management with payment processing integration.
 * 
 * Features:
 * - Subscription plan management
 * - Usage-based billing and metering
 * - Payment processing with Stripe integration
 * - Automated billing cycles and renewals
 * - Usage analytics and reporting
 * - Subscription lifecycle management
 * - Multi-tier pricing strategies
 * - Real-time usage tracking
 * 
 * Technical Stack:
 * - Spring Boot 3.1 with Java 17
 * - PostgreSQL for data persistence
 * - Redis for caching and session management
 * - Kafka for event-driven messaging
 * - Stripe for payment processing
 * - Quartz for scheduled billing operations
 * - OpenFeign for external service integration
 * 
 * @author Warehouse Team
 * @version 1.0.0
 * @since 2023-11-01
 */
@SpringBootApplication(scanBasePackages = {
    "com.ecosystem.warehousing.subscription",
    "com.ecosystem.warehousing.shared"
})
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@EnableKafka
@EnableFeignClients
public class WarehouseSubscriptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseSubscriptionApplication.class, args);
    }
}