package com.gogidix.warehousing.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Customer Storage Management Service Application
 * 
 * Manages the complete customer rental lifecycle including agreements,
 * payments, access control, and customer account management.
 * 
 * Key Features:
 * - Rental agreement creation and management
 * - Payment processing and billing cycles
 * - Customer account dashboard
 * - Unit access control and security
 * - Automated lease renewals and notifications
 * - Customer communication workflows
 * - Integration with marketplace and billing services
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
public class CustomerStorageManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerStorageManagementApplication.class, args);
    }
}