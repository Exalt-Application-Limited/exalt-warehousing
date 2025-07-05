package com.gogidix.warehousing.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Warehousing Billing Service Application
 * 
 * Standalone microservice for managing warehousing billing operations including:
 * - Storage cost calculation based on space utilization
 * - Subscription billing for warehouse partners
 * - Invoice generation and payment processing  
 * - Commission tracking for revenue sharing
 * - Multi-currency support for global operations
 * - Tax calculation and compliance frameworks
 * - Automated billing workflows and notifications
 * - Financial reporting and analytics
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableCaching
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}