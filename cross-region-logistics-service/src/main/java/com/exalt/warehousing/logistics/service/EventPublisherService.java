package com.exalt.warehousing.logistics.service;

import com.exalt.warehousing.logistics.model.TransferRequest;

/**
 * Service interface for publishing events
 */
public interface EventPublisherService {
    
    /**
     * Publish a transfer request creation event
     *
     * @param transferRequest the transfer request
     */
    void publishTransferCreated(TransferRequest transferRequest);
    
    /**
     * Publish a transfer request status change event
     *
     * @param transferRequest the transfer request
     * @param previousStatus the previous status
     */
    void publishTransferStatusChanged(TransferRequest transferRequest, String previousStatus);
    
    /**
     * Publish a transfer request cancellation event
     *
     * @param transferRequest the transfer request
     */
    void publishTransferCancelled(TransferRequest transferRequest);
    
    /**
     * Publish a transfer request completion event
     *
     * @param transferRequest the transfer request
     */
    void publishTransferCompleted(TransferRequest transferRequest);
} 
