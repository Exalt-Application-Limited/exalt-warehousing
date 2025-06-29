package com.exalt.warehousing.shared.enums;

import lombok.Getter;

/**
 * Enumeration of warehouse operational statuses
 * 
 * This enum defines the various operational states a warehouse can be in,
 * from initial setup through active operations to decommissioning.
 */
@Getter
public enum WarehouseStatus {
    
    /**
     * Warehouse is in planning phase
     * - Site selection in progress
     * - Permits being obtained
     * - Design and layout planning
     */
    PLANNING("Planning", "Warehouse is in the planning and design phase", false, false),
    
    /**
     * Warehouse is under construction
     * - Building construction in progress
     * - Infrastructure being installed
     * - Not yet operational
     */
    UNDER_CONSTRUCTION("Under Construction", "Warehouse facility is being constructed", false, false),
    
    /**
     * Warehouse setup and configuration in progress
     * - Equipment installation
     * - Systems configuration
     * - Staff training
     * - Initial testing
     */
    SETUP("Setup", "Warehouse setup and configuration in progress", false, false),
    
    /**
     * Warehouse is being tested and commissioned
     * - Systems testing
     * - Process validation
     * - Staff certification
     * - Trial operations
     */
    COMMISSIONING("Commissioning", "Warehouse systems testing and validation", false, true),
    
    /**
     * Warehouse is fully operational
     * - All systems active
     * - Normal operations
     * - Full capacity available
     */
    ACTIVE("Active", "Warehouse is fully operational and available", true, true),
    
    /**
     * Warehouse is operational but at reduced capacity
     * - Limited operations
     * - Partial system availability
     * - Reduced staff or equipment
     */
    LIMITED_OPERATIONS("Limited Operations", "Warehouse operating at reduced capacity", true, true),
    
    /**
     * Warehouse is temporarily offline for maintenance
     * - Scheduled maintenance
     * - System upgrades
     * - Equipment repairs
     * - Temporary unavailability
     */
    MAINTENANCE("Maintenance", "Warehouse temporarily offline for maintenance", false, false),
    
    /**
     * Warehouse operations are temporarily suspended
     * - Emergency shutdown
     * - Safety concerns
     * - System failures
     * - Awaiting repairs
     */
    SUSPENDED("Suspended", "Warehouse operations temporarily suspended", false, false),
    
    /**
     * Warehouse is being decommissioned
     * - Operations winding down
     * - Inventory being relocated
     * - Equipment being removed
     */
    DECOMMISSIONING("Decommissioning", "Warehouse is being decommissioned", false, true),
    
    /**
     * Warehouse has been permanently closed
     * - No longer operational
     * - All operations ceased
     * - Facility may be repurposed or demolished
     */
    CLOSED("Closed", "Warehouse has been permanently closed", false, false),
    
    /**
     * Warehouse status is unknown or cannot be determined
     * - Communication issues
     * - System problems
     * - Pending status update
     */
    UNKNOWN("Unknown", "Warehouse status cannot be determined", false, false);

    /**
     * Human-readable display name
     */
    private final String displayName;
    
    /**
     * Detailed description of the status
     */
    private final String description;
    
    /**
     * Whether the warehouse can process orders in this status
     */
    private final boolean canProcessOrders;
    
    /**
     * Whether the warehouse allows inventory movement in this status
     */
    private final boolean allowsInventoryMovement;

    /**
     * Constructor for WarehouseStatus enum
     * 
     * @param displayName human-readable name
     * @param description detailed description
     * @param canProcessOrders whether order processing is allowed
     * @param allowsInventoryMovement whether inventory movement is allowed
     */
    WarehouseStatus(String displayName, String description, 
                   boolean canProcessOrders, boolean allowsInventoryMovement) {
        this.displayName = displayName;
        this.description = description;
        this.canProcessOrders = canProcessOrders;
        this.allowsInventoryMovement = allowsInventoryMovement;
    }

    /**
     * Check if the warehouse is fully operational
     * 
     * @return true if warehouse is in active operational status
     */
    public boolean isFullyOperational() {
        return this == ACTIVE;
    }

    /**
     * Check if the warehouse is in any operational state
     * 
     * @return true if warehouse can perform any operations
     */
    public boolean isOperational() {
        return canProcessOrders || allowsInventoryMovement;
    }

    /**
     * Check if the warehouse is in a transitional state
     * 
     * @return true if warehouse is in setup, commissioning, or decommissioning
     */
    public boolean isTransitional() {
        return this == SETUP || this == COMMISSIONING || this == DECOMMISSIONING;
    }

    /**
     * Check if the warehouse is offline
     * 
     * @return true if warehouse is not operational
     */
    public boolean isOffline() {
        return this == MAINTENANCE || this == SUSPENDED || this == CLOSED || this == UNKNOWN;
    }

    /**
     * Check if the warehouse is in a problematic state requiring attention
     * 
     * @return true if warehouse status indicates problems
     */
    public boolean requiresAttention() {
        return this == SUSPENDED || this == UNKNOWN || this == LIMITED_OPERATIONS;
    }

    /**
     * Get the operational capacity percentage for this status
     * 
     * @return operational capacity as a percentage (0-100)
     */
    public int getOperationalCapacity() {
        switch (this) {
            case ACTIVE:
                return 100;
            case LIMITED_OPERATIONS:
                return 50;
            case COMMISSIONING:
                return 25;
            case DECOMMISSIONING:
                return 10;
            case PLANNING:
            case UNDER_CONSTRUCTION:
            case SETUP:
            case MAINTENANCE:
            case SUSPENDED:
            case CLOSED:
            case UNKNOWN:
            default:
                return 0;
        }
    }

    /**
     * Get valid transition states from the current status
     * 
     * @return array of valid next statuses
     */
    public WarehouseStatus[] getValidTransitions() {
        switch (this) {
            case PLANNING:
                return new WarehouseStatus[]{UNDER_CONSTRUCTION, CLOSED};
            case UNDER_CONSTRUCTION:
                return new WarehouseStatus[]{SETUP, SUSPENDED, CLOSED};
            case SETUP:
                return new WarehouseStatus[]{COMMISSIONING, SUSPENDED, CLOSED};
            case COMMISSIONING:
                return new WarehouseStatus[]{ACTIVE, LIMITED_OPERATIONS, SUSPENDED, SETUP};
            case ACTIVE:
                return new WarehouseStatus[]{LIMITED_OPERATIONS, MAINTENANCE, SUSPENDED, DECOMMISSIONING};
            case LIMITED_OPERATIONS:
                return new WarehouseStatus[]{ACTIVE, MAINTENANCE, SUSPENDED, DECOMMISSIONING};
            case MAINTENANCE:
                return new WarehouseStatus[]{ACTIVE, LIMITED_OPERATIONS, SUSPENDED};
            case SUSPENDED:
                return new WarehouseStatus[]{ACTIVE, LIMITED_OPERATIONS, MAINTENANCE, DECOMMISSIONING, CLOSED};
            case DECOMMISSIONING:
                return new WarehouseStatus[]{CLOSED, SUSPENDED};
            case CLOSED:
                return new WarehouseStatus[]{PLANNING}; // Can be reopened with new planning
            case UNKNOWN:
                return values(); // Unknown status can transition to any status once determined
            default:
                return new WarehouseStatus[]{};
        }
    }

    /**
     * Check if transition to another status is valid
     * 
     * @param targetStatus the status to transition to
     * @return true if the transition is valid
     */
    public boolean canTransitionTo(WarehouseStatus targetStatus) {
        WarehouseStatus[] validTransitions = getValidTransitions();
        for (WarehouseStatus validStatus : validTransitions) {
            if (validStatus == targetStatus) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the priority level for this status (for alerts and notifications)
     * 
     * @return priority level (1 = highest, 5 = lowest)
     */
    public int getPriorityLevel() {
        switch (this) {
            case SUSPENDED:
            case UNKNOWN:
                return 1; // Critical
            case LIMITED_OPERATIONS:
            case MAINTENANCE:
                return 2; // High
            case DECOMMISSIONING:
                return 3; // Medium
            case COMMISSIONING:
            case SETUP:
                return 4; // Low
            case ACTIVE:
            case PLANNING:
            case UNDER_CONSTRUCTION:
            case CLOSED:
            default:
                return 5; // Informational
        }
    }

    /**
     * Get the color code for UI representation
     * 
     * @return color code for status visualization
     */
    public String getColorCode() {
        switch (this) {
            case ACTIVE:
                return "#28a745"; // Green
            case LIMITED_OPERATIONS:
                return "#ffc107"; // Yellow
            case COMMISSIONING:
            case SETUP:
                return "#17a2b8"; // Blue
            case MAINTENANCE:
                return "#fd7e14"; // Orange
            case SUSPENDED:
            case UNKNOWN:
                return "#dc3545"; // Red
            case DECOMMISSIONING:
            case CLOSED:
                return "#6c757d"; // Gray
            case PLANNING:
            case UNDER_CONSTRUCTION:
                return "#6f42c1"; // Purple
            default:
                return "#6c757d"; // Default gray
        }
    }
}
