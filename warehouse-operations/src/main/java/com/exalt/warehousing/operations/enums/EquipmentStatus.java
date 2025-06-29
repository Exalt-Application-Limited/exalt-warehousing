package com.exalt.warehousing.operations.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Equipment Status Enumeration
 * 
 * Defines the possible states for warehouse and vendor facility equipment.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum EquipmentStatus {

    AVAILABLE("Available", "Equipment is available for use"),
    IN_USE("In Use", "Equipment is currently being used"),
    MAINTENANCE("Maintenance", "Equipment is undergoing maintenance"),
    OUT_OF_SERVICE("Out of Service", "Equipment is currently not operational"),
    CHARGING("Charging", "Equipment is charging"),
    RESERVED("Reserved", "Equipment is reserved for future use"),
    SCHEDULED_MAINTENANCE("Scheduled Maintenance", "Equipment is scheduled for maintenance"),
    INSPECTION("Inspection", "Equipment is being inspected"),
    REPAIR("Repair", "Equipment is being repaired"),
    RETIRED("Retired", "Equipment has been permanently retired"),
    IN_TRANSIT("In Transit", "Equipment is being moved between locations");

    private final String displayName;
    private final String description;

    public boolean isAvailableForUse() {
        return this == AVAILABLE;
    }

    public boolean isUnavailable() {
        return this == MAINTENANCE || this == OUT_OF_SERVICE || 
               this == CHARGING || this == SCHEDULED_MAINTENANCE || 
               this == INSPECTION || this == REPAIR || 
               this == RETIRED || this == IN_TRANSIT;
    }

    public boolean isTemporarilyUnavailable() {
        return isUnavailable() && this != RETIRED;
    }

    public boolean isPermanentlyUnavailable() {
        return this == RETIRED;
    }

    public boolean isCurrentlyInUse() {
        return this == IN_USE || this == RESERVED;
    }

    public boolean needsAttention() {
        return this == MAINTENANCE || this == OUT_OF_SERVICE || 
               this == REPAIR;
    }

    public boolean canBeCheckedOut() {
        return this == AVAILABLE;
    }

    public boolean canBeScheduledForMaintenance() {
        return this == AVAILABLE || this == OUT_OF_SERVICE;
    }

    public boolean isOperational() {
        return this != OUT_OF_SERVICE && this != REPAIR && this != RETIRED;
    }
}
