package com.gogidix.warehousing.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Customer Support Communication Service Application
 * 
 * Unified platform for customer support operations including
 * multi-channel communication, ticket management, and AI assistance.
 * 
 * Key Features:
 * - Multi-channel support (chat, email, phone, social)
 * - Real-time chat with WebSocket support
 * - Intelligent ticket routing and escalation
 * - AI-powered chat assistance and suggestions
 * - Knowledge base integration
 * - SLA tracking and performance metrics
 * - Customer satisfaction surveys
 * 
 * @author Exalt Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@EnableAsync
public class CustomerSupportCommunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerSupportCommunicationApplication.class, args);
    }
}