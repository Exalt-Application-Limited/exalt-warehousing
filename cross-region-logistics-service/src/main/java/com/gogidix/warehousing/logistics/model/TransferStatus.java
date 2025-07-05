package com.gogidix.warehousing.logistics.model;

/**
 * Represents the possible statuses of a transfer request
 */
public enum TransferStatus {
    /**
     * Transfer has been created but not yet approved
     */
    DRAFT,
    
    /**
     * Transfer has been submitted for approval
     */
    PENDING_APPROVAL,
    
    /**
     * Transfer has been approved and is ready for processing
     */
    APPROVED,
    
    /**
     * Transfer has been rejected
     */
    REJECTED,
    
    /**
     * Items are being picked at the source warehouse
     */
    PICKING,
    
    /**
     * Items are being packed for shipment
     */
    PACKING,
    
    /**
     * Transfer is ready for pickup by carrier
     */
    READY_FOR_PICKUP,
    
    /**
     * Transfer has been picked up by carrier
     */
    IN_TRANSIT,
    
    /**
     * Transfer has arrived at destination warehouse
     */
    ARRIVED,
    
    /**
     * Items are being verified at destination
     */
    VERIFYING,
    
    /**
     * Items have been received and put away
     */
    COMPLETED,
    
    /**
     * Transfer has been cancelled
     */
    CANCELLED,
    
    /**
     * Transfer has issues that need attention
     */
    EXCEPTION
} 
