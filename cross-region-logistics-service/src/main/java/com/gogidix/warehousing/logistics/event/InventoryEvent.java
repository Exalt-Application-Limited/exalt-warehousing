package com.gogidix.warehousing.logistics.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event representing changes in inventory
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    
    /**
     * Event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private String eventType;
    
    /**
     * Inventory item ID
     */
    private UUID inventoryItemId;
    
    /**
     * SKU
     */
    private String sku;
    
    /**
     * Warehouse ID
     */
    private UUID warehouseId;
    
    /**
     * Available quantity
     */
    private Integer availableQuantity;
    
    /**
     * Reserved quantity
     */
    private Integer reservedQuantity;
    
    /**
     * Timestamp
     */
    private LocalDateTime timestamp;
} 
