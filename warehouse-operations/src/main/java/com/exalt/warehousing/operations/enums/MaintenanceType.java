package com.exalt.warehousing.operations.enums;

/**
 * Enum for maintenance types
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public enum MaintenanceType {
    PREVENTIVE("Preventive Maintenance"),
    CORRECTIVE("Corrective Maintenance"),
    EMERGENCY("Emergency Maintenance"),
    SCHEDULED("Scheduled Maintenance"),
    PREDICTIVE("Predictive Maintenance"),
    ROUTINE("Routine Maintenance");

    private final String description;

    MaintenanceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

