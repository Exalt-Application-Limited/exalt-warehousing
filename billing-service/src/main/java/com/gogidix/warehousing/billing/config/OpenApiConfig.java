package com.gogidix.warehousing.billing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for billing service documentation
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI billingServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort + "/api/v1/billing");
        devServer.setDescription("Development server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.warehousing.ecosystem.com/billing");
        prodServer.setDescription("Production server");

        Contact contact = new Contact();
        contact.setEmail("billing-team@ecosystem.com");
        contact.setName("Billing Service Team");
        contact.setUrl("https://ecosystem.com/billing");

        License license = new License()
                .name("Proprietary License")
                .url("https://ecosystem.com/license");

        Info info = new Info()
                .title("Warehousing Billing Service API")
                .version("1.0.0")
                .contact(contact)
                .description("Comprehensive billing service for warehousing operations including " +
                           "subscription management, invoice generation, payment processing, " +
                           "usage tracking, and financial reporting. Supports multi-currency " +
                           "operations, automated billing cycles, and compliance frameworks.")
                .termsOfService("https://ecosystem.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}