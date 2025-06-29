package com.exalt.warehousing.operations.enums;

import lombok.Getter;

/**
 * Task Type Enumeration
 * 
 * Defines the different types of operational tasks within warehouse
 * and vendor facility operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum TaskType {

    PICKING("Picking", "Picking items for an order", true),
    PACKING("Packing", "Packing items for shipment", true),
    RECEIVING("Receiving", "Receiving inventory items", true),
    PUTAWAY("Putaway", "Storing inventory in locations", true),
    CYCLE_COUNT("Cycle Count", "Inventory counting", true),
    REPLENISHMENT("Replenishment", "Replenishing pick locations", true),
    QUALITY_CHECK("Quality Check", "Quality inspection", false),
    RETURNS_PROCESSING("Returns Processing", "Processing returned items", true),
    SHIPPING_PREPARATION("Shipping Preparation", "Preparing shipments", true),
    MAINTENANCE("Maintenance", "Equipment maintenance", false),
    CLEANING("Cleaning", "Area cleaning", false),
    ORGANIZATION("Organization", "Area organization", false),
    INVENTORY_ADJUSTMENT("Inventory Adjustment", "Adjusting inventory levels", true),
    STOCK_MOVEMENT("Stock Movement", "Moving stock between locations", true),
    ITEM_LOOKUP("Item Lookup", "Searching for specific items", false),
    EQUIPMENT_INSPECTION("Equipment Inspection", "Inspecting equipment", false),
    VENDOR_COORDINATION("Vendor Coordination", "Coordinating with vendors", false),
    TRAINING("Training", "Training activities", false),
    SPECIAL_HANDLING("Special Handling", "Special item handling requirements", true),
    AUDIT("Audit", "Audit or compliance check", false);

    private final String displayName;
    private final String description;
    private final boolean isQuantifiable;

    TaskType(String displayName, String description, boolean isQuantifiable) {
        this.displayName = displayName;
        this.description = description;
        this.isQuantifiable = isQuantifiable;
    }

    public boolean requiresInventoryChange() {
        return this == PICKING || this == RECEIVING || 
               this == PUTAWAY || this == CYCLE_COUNT ||
               this == REPLENISHMENT || this == INVENTORY_ADJUSTMENT ||
               this == STOCK_MOVEMENT || this == RETURNS_PROCESSING;
    }

    public boolean requiresEquipment() {
        return this == PICKING || this == PUTAWAY || 
               this == REPLENISHMENT || this == STOCK_MOVEMENT;
    }

    public boolean isOrderRelated() {
        return this == PICKING || this == PACKING || 
               this == SHIPPING_PREPARATION;
    }

    public boolean isInventoryRelated() {
        return this == RECEIVING || this == PUTAWAY || 
               this == CYCLE_COUNT || this == REPLENISHMENT ||
               this == INVENTORY_ADJUSTMENT || this == STOCK_MOVEMENT ||
               this == ITEM_LOOKUP;
    }

    public boolean requiresVerification() {
        return this == CYCLE_COUNT || this == INVENTORY_ADJUSTMENT ||
               this == QUALITY_CHECK || this == AUDIT;
    }

    public boolean isMaintenanceRelated() {
        return this == MAINTENANCE || this == CLEANING || 
               this == EQUIPMENT_INSPECTION || this == ORGANIZATION;
    }

    public boolean isProductivityTracked() {
        return isQuantifiable && (this != MAINTENANCE && this != CLEANING && 
                                 this != ORGANIZATION && this != TRAINING);
    }

    public boolean requiresSpecialTraining() {
        return this == QUALITY_CHECK || this == SPECIAL_HANDLING || 
               this == MAINTENANCE || this == EQUIPMENT_INSPECTION ||
               this == AUDIT;
    }
}
