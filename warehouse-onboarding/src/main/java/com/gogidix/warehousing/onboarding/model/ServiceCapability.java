package com.gogidix.warehousing.onboarding.model;

/**
 * Service Capability Enumeration
 * 
 * Represents different service capabilities a warehouse partner can provide
 */
public enum ServiceCapability {
    RECEIVING("Receiving and inspection services"),
    STORAGE("Basic storage services"),
    INVENTORY_MANAGEMENT("Inventory management and tracking"),
    ORDER_FULFILLMENT("Order picking and fulfillment"),
    PACKAGING("Custom packaging services"),
    LABELING("Product labeling and re-labeling"),
    QUALITY_CONTROL("Quality control and inspection"),
    RETURNS_PROCESSING("Returns processing and management"),
    CROSS_DOCKING("Cross-docking services"),
    CONSOLIDATION("Order consolidation services"),
    KITTING("Kitting and assembly services"),
    VALUE_ADDED_SERVICES("Value-added services (customization, etc.)"),
    SHIPPING_COORDINATION("Shipping and logistics coordination"),
    REAL_TIME_TRACKING("Real-time inventory tracking"),
    REPORTING_ANALYTICS("Reporting and analytics"),
    TEMPERATURE_MONITORING("Temperature monitoring and alerts"),
    SECURITY_SERVICES("Enhanced security services"),
    INSURANCE_COVERAGE("Insurance coverage for stored goods"),
    CUSTOMS_CLEARANCE("Customs clearance assistance"),
    DOCUMENTATION("Documentation and compliance support"),
    PHOTO_SERVICES("Product photography services"),
    SAMPLE_MANAGEMENT("Sample management and distribution"),
    BATCH_TRACKING("Batch and lot tracking"),
    EXPIRY_MANAGEMENT("Expiry date management"),
    QUARANTINE_SERVICES("Quarantine and hold services");

    private final String description;

    ServiceCapability(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if capability is considered a core service
     */
    public boolean isCoreService() {
        return this == RECEIVING || this == STORAGE || this == INVENTORY_MANAGEMENT || 
               this == ORDER_FULFILLMENT;
    }

    /**
     * Check if capability is a value-added service
     */
    public boolean isValueAddedService() {
        return this == PACKAGING || this == LABELING || this == KITTING || 
               this == VALUE_ADDED_SERVICES || this == PHOTO_SERVICES;
    }

    /**
     * Check if capability requires special technology
     */
    public boolean requiresSpecialTechnology() {
        return this == REAL_TIME_TRACKING || this == TEMPERATURE_MONITORING || 
               this == REPORTING_ANALYTICS || this == BATCH_TRACKING;
    }

    /**
     * Check if capability requires regulatory compliance
     */
    public boolean requiresCompliance() {
        return this == QUALITY_CONTROL || this == TEMPERATURE_MONITORING || 
               this == CUSTOMS_CLEARANCE || this == DOCUMENTATION || 
               this == QUARANTINE_SERVICES;
    }
}