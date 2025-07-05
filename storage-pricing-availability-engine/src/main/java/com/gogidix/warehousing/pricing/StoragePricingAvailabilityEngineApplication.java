package com.gogidix.warehousing.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Storage Pricing and Availability Engine Application
 * 
 * Advanced pricing engine that implements dynamic pricing algorithms,
 * demand forecasting, and real-time availability management.
 * 
 * Key Features:
 * - Machine learning-based dynamic pricing
 * - Demand forecasting and seasonal adjustments
 * - Competitor analysis integration
 * - Real-time availability synchronization
 * - Revenue optimization algorithms
 * - Multi-tier pricing strategies
 * 
 * @author Exalt Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@EnableScheduling
@EnableAsync
@EnableCaching
public class StoragePricingAvailabilityEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoragePricingAvailabilityEngineApplication.class, args);
    }
}