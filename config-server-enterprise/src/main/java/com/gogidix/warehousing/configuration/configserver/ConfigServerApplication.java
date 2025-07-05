package com.gogidix.warehousing.configuration.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Enterprise Configuration Server
 * 
 * Centralized configuration management for all Exalt Ecosystem services.
 * Provides configuration data to all microservices across all domains.
 * 
 * @author Exalt Ecosystem Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
