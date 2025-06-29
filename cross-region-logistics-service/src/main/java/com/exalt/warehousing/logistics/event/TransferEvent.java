package com.exalt.warehousing.logistics.event;

import com.exalt.warehousing.logistics.model.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event representing changes in transfer requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferEvent {
    
    /**
     * Event ID
     */
    private UUID eventId;
    
    /**
     * Event type
     */
    private String eventType;
    
    /**
     * Transfer request ID
     */
    private UUID transferId;
    
    /**
     * Reference number
     */
    private String referenceNumber;
    
    /**
     * Source warehouse ID
     */
    private UUID sourceWarehouseId;
    
    /**
     * Destination warehouse ID
     */
    private UUID destinationWarehouseId;
    
    /**
     * Current status
     */
    private TransferStatus status;
    
    /**
     * Timestamp
     */
    private LocalDateTime timestamp;
    
    /**
     * Additional metadata
     */
    private Object metadata;
} 
