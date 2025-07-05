package com.gogidix.warehousing.operations.enums;

import lombok.Getter;

/**
 * Staff Assignment Type Enumeration
 * 
 * Defines the different types of staff assignments for warehouse
 * and vendor facility operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum AssignmentType {

    PICKING("Picking", "Order picking operations"),
    PACKING("Packing", "Order packing operations"),
    RECEIVING("Receiving", "Inventory receiving operations"),
    PUTAWAY("Putaway", "Inventory storage placement"),
    CYCLE_COUNT("Cycle Count", "Inventory counting and verification"),
    REPLENISHMENT("Replenishment", "Replenishing pick locations"),
    SHIPPING("Shipping", "Order shipping operations"),
    QUALITY_CONTROL("Quality Control", "Quality inspection and control"),
    RETURNS_PROCESSING("Returns Processing", "Processing returned items"),
    MAINTENANCE("Maintenance", "Equipment or facility maintenance"),
    SUPERVISION("Supervision", "Supervisory role"),
    TRAINING("Training", "Staff training activities"),
    CLEANUP("Cleanup", "Area cleaning or organization"),
    SPECIAL_PROJECT("Special Project", "One-time special project work");

    private final String displayName;
    private final String description;

    AssignmentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isInventoryRelated() {
        return this == RECEIVING || this == PUTAWAY || 
               this == CYCLE_COUNT || this == REPLENISHMENT;
    }

    public boolean isOrderRelated() {
        return this == PICKING || this == PACKING || 
               this == SHIPPING || this == RETURNS_PROCESSING;
    }

    public boolean requiresSpecialSkills() {
        return this == QUALITY_CONTROL || this == MAINTENANCE || 
               this == SUPERVISION || this == SPECIAL_PROJECT;
    }

    public boolean isProductivityTracked() {
        return this == PICKING || this == PACKING || 
               this == RECEIVING || this == PUTAWAY ||
               this == CYCLE_COUNT || this == REPLENISHMENT ||
               this == SHIPPING || this == RETURNS_PROCESSING;
    }

    public boolean isProductive() {
        return this != TRAINING && this != CLEANUP && this != SUPERVISION;
    }

    public boolean isIndirectLabor() {
        return this == TRAINING || this == CLEANUP || 
               this == SUPERVISION || this == MAINTENANCE;
    }
}
