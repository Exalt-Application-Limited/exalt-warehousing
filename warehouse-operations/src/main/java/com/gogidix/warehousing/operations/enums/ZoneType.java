package com.gogidix.warehousing.operations.enums;

import lombok.Getter;

/**
 * Warehouse Zone Type Enumeration
 * 
 * Defines the different functional areas within a warehouse or vendor facility.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum ZoneType {

    RECEIVING("Receiving", "Area for receiving inbound inventory"),
    BULK_STORAGE("Bulk Storage", "Primary storage area for bulk inventory"),
    RESERVE_STORAGE("Reserve Storage", "Secondary storage for overstock or seasonal items"),
    FORWARD_PICKING("Forward Picking", "Primary picking area for fast-moving items"),
    PICKING("Picking", "General picking area"),
    PACKING("Packing", "Area for order packing operations"),
    STAGING("Staging", "Area for staging packed orders before shipping"),
    SHIPPING("Shipping", "Area for outbound shipping operations"),
    RETURNS("Returns", "Area for processing returned items"),
    QUALITY_CONTROL("Quality Control", "Area for quality inspection and control"),
    HAZMAT("Hazardous Materials", "Area for hazardous materials storage"),
    CLIMATE_CONTROLLED("Climate Controlled", "Temperature or humidity controlled storage"),
    HIGH_SECURITY("High Security", "Area with enhanced security controls"),
    HIGH_VALUE("High Value", "Storage for high value items"),
    REFRIGERATED("Refrigerated", "Cold storage area"),
    FROZEN("Frozen", "Frozen storage area"),
    OFFICE("Office", "Office or administrative area"),
    MAINTENANCE("Maintenance", "Area for equipment maintenance"),
    CHARGING("Charging", "Equipment charging area"),
    VENDOR_SPECIFIC("Vendor Specific", "Custom area for vendor requirements");

    private final String displayName;
    private final String description;

    ZoneType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isStorageZone() {
        return this == BULK_STORAGE || this == RESERVE_STORAGE || 
               this == FORWARD_PICKING || this == HIGH_VALUE ||
               this == REFRIGERATED || this == FROZEN ||
               this == CLIMATE_CONTROLLED || this == HAZMAT;
    }

    public boolean isOperationalZone() {
        return this == RECEIVING || this == PICKING || 
               this == PACKING || this == STAGING ||
               this == SHIPPING || this == RETURNS ||
               this == QUALITY_CONTROL;
    }

    public boolean isSpecialHandlingZone() {
        return this == HAZMAT || this == CLIMATE_CONTROLLED || 
               this == HIGH_SECURITY || this == REFRIGERATED ||
               this == FROZEN;
    }

    public boolean requiresSpecialAccess() {
        return this == HIGH_SECURITY || this == HIGH_VALUE || 
               this == HAZMAT;
    }

    public boolean requiresClimateControl() {
        return this == CLIMATE_CONTROLLED || this == REFRIGERATED ||
               this == FROZEN;
    }
}
