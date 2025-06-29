package com.exalt.warehousing.logistics.model;

/**
 * Represents the possible statuses of a transfer item
 */
public enum TransferItemStatus {
    /**
     * Item is requested but not yet processed
     */
    PENDING,
    
    /**
     * Item is being picked at source warehouse
     */
    PICKING,
    
    /**
     * Item has been picked at source warehouse
     */
    PICKED,
    
    /**
     * Item has been packed for shipping
     */
    PACKED,
    
    /**
     * Item is in transit
     */
    IN_TRANSIT,
    
    /**
     * Item has arrived at destination warehouse
     */
    ARRIVED,
    
    /**
     * Item is being verified at destination warehouse
     */
    VERIFYING,
    
    /**
     * Item has been received and put away
     */
    COMPLETED,
    
    /**
     * Item is missing or damaged
     */
    EXCEPTION,
    
    /**
     * Item has been cancelled
     */
    CANCELLED,
    
    /**
     * Item is partially fulfilled
     */
    PARTIAL
} 
