package com.exalt.warehousing.shared.enums;

import lombok.Getter;

/**
 * Enumeration of warehouse types in the ecosystem
 * 
 * This enum defines the different types of warehouses that can be managed
 * within the ecosystem, each with specific characteristics and capabilities.
 */
@Getter
public enum WarehouseType {
    
    /**
     * Full-service fulfillment center with comprehensive operations
     * Capabilities: Receiving, Storage, Picking, Packing, Shipping, Returns
     */
    FULFILLMENT_CENTER("Fulfillment Center", "Full-service distribution and fulfillment operations", true, true, true),
    
    /**
     * Storage-focused facility with minimal processing
     * Capabilities: Receiving, Storage, Basic fulfillment
     */
    STORAGE_FACILITY("Storage Facility", "Primary storage with basic fulfillment services", true, false, false),
    
    /**
     * Cross-docking facility for rapid transshipment
     * Capabilities: Receiving, Sorting, Immediate shipping
     */
    CROSS_DOCK("Cross Dock", "Rapid transshipment without long-term storage", false, true, true),
    
    /**
     * Regional distribution center serving specific geographic areas
     * Capabilities: Regional distribution, Local delivery, Returns processing
     */
    REGIONAL_DC("Regional Distribution Center", "Regional distribution and local delivery hub", true, true, true),
    
    /**
     * Local delivery station for last-mile operations
     * Capabilities: Last-mile delivery, Local returns
     */
    LOCAL_DELIVERY("Local Delivery Station", "Last-mile delivery and local customer service", false, false, true),
    
    /**
     * Specialized cold storage facility
     * Capabilities: Temperature-controlled storage, Cold chain fulfillment
     */
    COLD_STORAGE("Cold Storage", "Temperature-controlled storage and fulfillment", true, true, false),
    
    /**
     * Bulk storage for large quantities
     * Capabilities: Bulk storage, Wholesale distribution
     */
    BULK_STORAGE("Bulk Storage", "Large quantity storage and wholesale distribution", true, false, false),
    
    /**
     * Self-storage facility for customer storage
     * Capabilities: Customer self-storage units
     */
    SELF_STORAGE("Self Storage", "Customer-managed storage units", true, false, false),
    
    /**
     * Returns processing center
     * Capabilities: Returns processing, Refurbishment, Restocking
     */
    RETURNS_CENTER("Returns Center", "Specialized returns processing and refurbishment", false, true, false),
    
    /**
     * Automated micro-fulfillment center
     * Capabilities: Automated picking, Fast fulfillment, Urban delivery
     */
    MICRO_FULFILLMENT("Micro Fulfillment", "Automated urban fulfillment center", true, true, true),
    
    /**
     * Third-party logistics facility
     * Capabilities: Full 3PL services, Multi-client operations
     */
    THIRD_PARTY_LOGISTICS("3PL Facility", "Third-party logistics and multi-client operations", true, true, true),
    
    /**
     * Manufacturing warehouse with production capabilities
     * Capabilities: Manufacturing, Assembly, Storage, Fulfillment
     */
    MANUFACTURING("Manufacturing Warehouse", "Manufacturing and assembly with fulfillment", true, true, true),
    
    /**
     * Hub facility for inter-warehouse transfers
     * Capabilities: Inter-warehouse transfers, Regional coordination
     */
    HUB("Hub Facility", "Inter-warehouse transfers and regional coordination", false, true, true),
    
    /**
     * Specialty warehouse for specific product types
     * Capabilities: Specialized handling, Industry-specific operations
     */
    SPECIALTY("Specialty Warehouse", "Specialized handling for specific product types", true, true, false),
    
    /**
     * Mobile or temporary warehouse facility
     * Capabilities: Temporary storage, Mobile operations
     */
    MOBILE("Mobile Warehouse", "Temporary or mobile storage facility", true, false, true);

    /**
     * Human-readable display name
     */
    private final String displayName;
    
    /**
     * Detailed description of the warehouse type
     */
    private final String description;
    
    /**
     * Whether this warehouse type supports long-term storage
     */
    private final boolean supportsStorage;
    
    /**
     * Whether this warehouse type supports order processing/fulfillment
     */
    private final boolean supportsProcessing;
    
    /**
     * Whether this warehouse type supports direct shipping operations
     */
    private final boolean supportsShipping;

    /**
     * Constructor for WarehouseType enum
     * 
     * @param displayName human-readable name
     * @param description detailed description
     * @param supportsStorage whether storage is supported
     * @param supportsProcessing whether order processing is supported
     * @param supportsShipping whether shipping is supported
     */
    WarehouseType(String displayName, String description, boolean supportsStorage, 
                  boolean supportsProcessing, boolean supportsShipping) {
        this.displayName = displayName;
        this.description = description;
        this.supportsStorage = supportsStorage;
        this.supportsProcessing = supportsProcessing;
        this.supportsShipping = supportsShipping;
    }

    /**
     * Check if this warehouse type is suitable for e-commerce fulfillment
     * 
     * @return true if suitable for e-commerce operations
     */
    public boolean isEcommerceSuitable() {
        return supportsStorage && supportsProcessing && supportsShipping;
    }

    /**
     * Check if this warehouse type requires specialized equipment
     * 
     * @return true if specialized equipment is required
     */
    public boolean requiresSpecializedEquipment() {
        return this == COLD_STORAGE || this == MICRO_FULFILLMENT || 
               this == MANUFACTURING || this == SPECIALTY;
    }

    /**
     * Check if this warehouse type is automated
     * 
     * @return true if the warehouse type typically uses automation
     */
    public boolean isAutomated() {
        return this == MICRO_FULFILLMENT || this == CROSS_DOCK;
    }

    /**
     * Get the typical capacity category for this warehouse type
     * 
     * @return capacity category
     */
    public CapacityCategory getTypicalCapacityCategory() {
        switch (this) {
            case FULFILLMENT_CENTER:
            case REGIONAL_DC:
            case BULK_STORAGE:
            case THIRD_PARTY_LOGISTICS:
            case MANUFACTURING:
                return CapacityCategory.LARGE;
            case STORAGE_FACILITY:
            case COLD_STORAGE:
            case RETURNS_CENTER:
            case HUB:
            case SPECIALTY:
                return CapacityCategory.MEDIUM;
            case CROSS_DOCK:
            case LOCAL_DELIVERY:
            case SELF_STORAGE:
            case MICRO_FULFILLMENT:
            case MOBILE:
                return CapacityCategory.SMALL;
            default:
                return CapacityCategory.MEDIUM;
        }
    }

    /**
     * Get warehouse types suitable for a specific operation
     * 
     * @param operation the operation type
     * @return array of suitable warehouse types
     */
    public static WarehouseType[] getSuitableTypesForOperation(OperationType operation) {
        switch (operation) {
            case STORAGE:
                return new WarehouseType[]{FULFILLMENT_CENTER, STORAGE_FACILITY, COLD_STORAGE, 
                                         BULK_STORAGE, SELF_STORAGE, THIRD_PARTY_LOGISTICS, 
                                         MANUFACTURING, SPECIALTY, MOBILE};
            case FULFILLMENT:
                return new WarehouseType[]{FULFILLMENT_CENTER, REGIONAL_DC, COLD_STORAGE, 
                                         MICRO_FULFILLMENT, THIRD_PARTY_LOGISTICS, MANUFACTURING};
            case CROSS_DOCKING:
                return new WarehouseType[]{CROSS_DOCK, HUB, REGIONAL_DC};
            case RETURNS:
                return new WarehouseType[]{RETURNS_CENTER, FULFILLMENT_CENTER, REGIONAL_DC};
            case SHIPPING:
                return new WarehouseType[]{FULFILLMENT_CENTER, CROSS_DOCK, REGIONAL_DC, 
                                         LOCAL_DELIVERY, MICRO_FULFILLMENT, HUB, MOBILE};
            default:
                return values();
        }
    }

    /**
     * Capacity categories for warehouses
     */
    public enum CapacityCategory {
        SMALL("Small", "< 50,000 sq ft"),
        MEDIUM("Medium", "50,000 - 200,000 sq ft"),
        LARGE("Large", "200,000 - 1,000,000 sq ft"),
        MEGA("Mega", "> 1,000,000 sq ft");

        @Getter
        private final String displayName;
        
        @Getter
        private final String description;

        CapacityCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }

    /**
     * Operation types for warehouse filtering
     */
    public enum OperationType {
        STORAGE,
        FULFILLMENT,
        CROSS_DOCKING,
        RETURNS,
        SHIPPING,
        MANUFACTURING
    }
}
