package com.gogidix.warehousing.operations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Advanced Warehouse Operations Service Application
 * 
 * Comprehensive operational management system providing sophisticated
 * warehouse optimization and staff management capabilities.
 * 
 * Key Features:
 * - Intelligent warehouse layout optimization
 * - Advanced staff assignment and scheduling
 * - Real-time task management and tracking
 * - Equipment lifecycle management
 * - Performance analytics and optimization
 * - Predictive maintenance and capacity planning
 * 
 * Revolutionary Vendor Choice Model Integration:
 * - Unified operations across warehouse and vendor self-storage
 * - Cross-facility performance comparison
 * - Intelligent workload distribution
 * - Vendor facility optimization recommendations
 * 
 * Advanced Capabilities:
 * - AI-powered picking path optimization
 * - Dynamic space allocation
 * - Predictive staffing models
 * - Real-time operational dashboards
 * - Automated performance optimization
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.microcommerce.warehousing.operations",
    "com.microcommerce.warehousing.shared"
})
@EntityScan(basePackages = {
    "com.microcommerce.warehousing.operations.entity",
    "com.microcommerce.warehousing.shared.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.microcommerce.warehousing.operations.repository",
    "com.microcommerce.warehousing.shared.repository"
})
@EnableFeignClients(basePackages = {
    "com.microcommerce.warehousing.operations.client",
    "com.microcommerce.warehousing.shared.client"
})
@EnableKafka
@EnableAsync
@EnableScheduling
public class WarehouseOperationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseOperationsApplication.class, args);
    }
}
