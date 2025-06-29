package com.exalt.warehousing.logistics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration
 */
@Configuration
public class AppConfig {

    /**
     * Configure OpenAPI documentation
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Cross-Region Logistics API")
                        .description("API for managing logistics operations between warehousing regions")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Warehousing Domain Team")
                                .email("warehousing@example.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://example.com")));
    }

    /**
     * Create RestTemplate bean
     *
     * @return RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 
