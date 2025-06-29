package com.exalt.warehousing.logistics.model;

/**
 * Represents the priority levels for transfer requests
 */
public enum TransferPriority {
    /**
     * Low priority transfer that can be processed when convenient
     */
    LOW,
    
    /**
     * Standard priority transfer with normal processing times
     */
    NORMAL,
    
    /**
     * High priority transfer that should be processed before normal transfers
     */
    HIGH,
    
    /**
     * Urgent transfer that requires immediate attention
     */
    URGENT
} 
