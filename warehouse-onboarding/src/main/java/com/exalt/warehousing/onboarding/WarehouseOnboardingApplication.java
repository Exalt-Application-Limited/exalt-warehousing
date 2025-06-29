package com.exalt.warehousing.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Warehouse Onboarding Service Application
 * 
 * Provides comprehensive warehouse partner onboarding with:
 * - KYC verification and compliance checks
 * - Document validation and storage
 * - Automated approval workflows
 * - Integration with external verification services
 * 
 * @author Warehouse Onboarding Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
public class WarehouseOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseOnboardingApplication.class, args);
    }
}