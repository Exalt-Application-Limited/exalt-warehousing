package com.exalt.warehousing.logistics.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event representing changes in warehouse information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseEvent {
    
    /**
     * Event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private String eventType;
    
    /**
     * Warehouse ID
     */
    private UUID warehouseId;
    
    /**
     * Warehouse code
     */
    private String warehouseCode;
    
    /**
     * Status
     */
    private String status;
    
    /**
     * Region code
     */
    private String regionCode;
    
    /**
     * Country code
     */
    private String countryCode;
    
    /**
     * Timestamp
     */
    private LocalDateTime timestamp;
} 
