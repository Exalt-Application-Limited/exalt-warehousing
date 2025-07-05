package com.gogidix.warehousing.onboarding.model;

/**
 * Storage Type Enumeration
 * 
 * Represents different types of storage capabilities a warehouse can offer
 */
public enum StorageType {
    AMBIENT("Ambient temperature storage"),
    REFRIGERATED("Refrigerated storage (2-8°C)"),
    FROZEN("Frozen storage (-18°C or below)"),
    CONTROLLED_ATMOSPHERE("Controlled atmosphere storage"),
    HAZARDOUS_MATERIALS("Hazardous materials storage"),
    HIGH_VALUE("High-value items storage with enhanced security"),
    BULK_STORAGE("Bulk storage for large quantities"),
    TEMPERATURE_SENSITIVE("Temperature-sensitive storage"),
    HUMIDITY_CONTROLLED("Humidity-controlled storage"),
    CLEAN_ROOM("Clean room storage for sensitive products"),
    PHARMACEUTICAL("Pharmaceutical-grade storage"),
    FOOD_GRADE("Food-grade storage with HACCP compliance"),
    AUTOMOTIVE("Automotive parts storage"),
    ELECTRONICS("Electronics storage with anti-static protection"),
    TEXTILES("Textile storage with proper ventilation"),
    CHEMICALS("Chemical storage with safety protocols");

    private final String description;

    StorageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if storage type requires special certifications
     */
    public boolean requiresSpecialCertification() {
        return this == PHARMACEUTICAL || this == FOOD_GRADE || this == HAZARDOUS_MATERIALS || 
               this == CLEAN_ROOM || this == CHEMICALS;
    }

    /**
     * Check if storage type requires temperature monitoring
     */
    public boolean requiresTemperatureMonitoring() {
        return this == REFRIGERATED || this == FROZEN || this == CONTROLLED_ATMOSPHERE || 
               this == TEMPERATURE_SENSITIVE || this == PHARMACEUTICAL;
    }

    /**
     * Check if storage type requires enhanced security
     */
    public boolean requiresEnhancedSecurity() {
        return this == HIGH_VALUE || this == PHARMACEUTICAL || this == HAZARDOUS_MATERIALS || 
               this == CHEMICALS;
    }
}