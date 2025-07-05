package com.gogidix.warehousing.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Customer Storage Marketplace Service Application
 * 
 * B2C marketplace service that enables customers to discover, compare,
 * and book storage facilities for personal and business use.
 * 
 * Key Features:
 * - Storage facility listings and search
 * - Pricing comparison across providers
 * - Real-time availability checking
 * - Customer reviews and ratings
 * - Booking and reservation management
 * - Payment integration
 * - Customer support integration
 * 
 * @author Exalt Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
public class CustomerStorageMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerStorageMarketplaceApplication.class, args);
    }
}