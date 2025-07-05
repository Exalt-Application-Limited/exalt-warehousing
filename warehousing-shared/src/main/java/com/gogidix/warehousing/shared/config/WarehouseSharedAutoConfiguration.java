package com.gogidix.warehousing.shared.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * Auto-configuration class for Warehousing Shared Library
 * 
 * This configuration class provides common beans and settings that can be shared
 * across all warehousing microservices. It includes JSON serialization configuration,
 * common utilities, and default settings.
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.ecosystem.warehousing.shared")
public class WarehouseSharedAutoConfiguration {

    /**
     * Configure ObjectMapper for consistent JSON serialization across services
     * 
     * @return configured ObjectMapper instance
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java Time module for proper LocalDateTime serialization
        mapper.registerModule(new JavaTimeModule());
        
        // Configure serialization behavior
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        
        // Configure deserialization behavior
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        
        // Set property naming strategy (snake_case for API consistency)
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // Include only non-null values in JSON output
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // Set default timezone to UTC
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        
        return mapper;
    }

    /**
     * Configure timezone utilities
     * 
     * @return timezone configuration bean
     */
    @Bean
    @ConditionalOnMissingBean
    public TimezoneConfiguration timezoneConfiguration() {
        return new TimezoneConfiguration();
    }

    /**
     * Configure validation utilities
     * 
     * @return validation configuration bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ValidationConfiguration validationConfiguration() {
        return new ValidationConfiguration();
    }

    /**
     * Configure warehouse calculation utilities
     * 
     * @return calculation configuration bean
     */
    @Bean
    @ConditionalOnMissingBean
    public CalculationConfiguration calculationConfiguration() {
        return new CalculationConfiguration();
    }

    /**
     * Timezone configuration class
     */
    public static class TimezoneConfiguration {
        
        /**
         * Get the default system timezone
         * 
         * @return default timezone (UTC)
         */
        public TimeZone getDefaultTimezone() {
            return TimeZone.getTimeZone(ZoneOffset.UTC);
        }
        
        /**
         * Get timezone by ID
         * 
         * @param timezoneId timezone identifier
         * @return timezone instance
         */
        public TimeZone getTimezone(String timezoneId) {
            if (timezoneId == null || timezoneId.trim().isEmpty()) {
                return getDefaultTimezone();
            }
            return TimeZone.getTimeZone(timezoneId);
        }
        
        /**
         * Check if timezone ID is valid
         * 
         * @param timezoneId timezone identifier
         * @return true if timezone is valid
         */
        public boolean isValidTimezone(String timezoneId) {
            if (timezoneId == null || timezoneId.trim().isEmpty()) {
                return false;
            }
            
            TimeZone tz = TimeZone.getTimeZone(timezoneId);
            // If timezone is invalid, getTimeZone returns GMT
            return !tz.getID().equals("GMT") || timezoneId.equals("GMT") || timezoneId.equals("UTC");
        }
    }

    /**
     * Validation configuration class
     */
    public static class ValidationConfiguration {
        
        // Default validation settings
        private static final int DEFAULT_MAX_STRING_LENGTH = 255;
        private static final int DEFAULT_MAX_TEXT_LENGTH = 4000;
        private static final int DEFAULT_MAX_NAME_LENGTH = 100;
        
        /**
         * Get default maximum string length
         * 
         * @return default maximum string length
         */
        public int getDefaultMaxStringLength() {
            return DEFAULT_MAX_STRING_LENGTH;
        }
        
        /**
         * Get default maximum text length
         * 
         * @return default maximum text length
         */
        public int getDefaultMaxTextLength() {
            return DEFAULT_MAX_TEXT_LENGTH;
        }
        
        /**
         * Get default maximum name length
         * 
         * @return default maximum name length
         */
        public int getDefaultMaxNameLength() {
            return DEFAULT_MAX_NAME_LENGTH;
        }
    }

    /**
     * Calculation configuration class
     */
    public static class CalculationConfiguration {
        
        // Default calculation settings
        private static final int DEFAULT_DECIMAL_SCALE = 4;
        private static final int DEFAULT_PERCENTAGE_SCALE = 2;
        private static final int DEFAULT_CURRENCY_SCALE = 2;
        
        /**
         * Get default decimal scale for calculations
         * 
         * @return default decimal scale
         */
        public int getDefaultDecimalScale() {
            return DEFAULT_DECIMAL_SCALE;
        }
        
        /**
         * Get default percentage scale
         * 
         * @return default percentage scale
         */
        public int getDefaultPercentageScale() {
            return DEFAULT_PERCENTAGE_SCALE;
        }
        
        /**
         * Get default currency scale
         * 
         * @return default currency scale
         */
        public int getDefaultCurrencyScale() {
            return DEFAULT_CURRENCY_SCALE;
        }
    }
}