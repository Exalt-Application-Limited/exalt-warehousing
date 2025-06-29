package com.exalt.warehousing.logistics.service;

import com.exalt.warehousing.logistics.model.TransferItemStatus;
import com.exalt.warehousing.logistics.model.TransferRequest;
import com.exalt.warehousing.logistics.model.TransferStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing transfer requests between warehouses
 */
public interface TransferService {
    
    /**
     * Create a new transfer request
     *
     * @param transferRequest the transfer request to create
     * @return the created transfer request
     */
    TransferRequest createTransferRequest(TransferRequest transferRequest);
    
    /**
     * Get a transfer request by ID
     *
     * @param id the transfer request ID
     * @return the transfer request if found
     */
    Optional<TransferRequest> getTransferRequest(UUID id);
    
    /**
     * Get a transfer request by reference number
     *
     * @param referenceNumber the reference number
     * @return the transfer request if found
     */
    Optional<TransferRequest> getTransferRequestByReferenceNumber(String referenceNumber);
    
    /**
     * Get all transfer requests
     *
     * @return list of all transfer requests
     */
    List<TransferRequest> getAllTransferRequests();
    
    /**
     * Get transfer requests by source warehouse ID
     *
     * @param sourceWarehouseId the source warehouse ID
     * @return list of transfer requests
     */
    List<TransferRequest> getTransferRequestsBySourceWarehouse(UUID sourceWarehouseId);
    
    /**
     * Get transfer requests by destination warehouse ID
     *
     * @param destinationWarehouseId the destination warehouse ID
     * @return list of transfer requests
     */
    List<TransferRequest> getTransferRequestsByDestinationWarehouse(UUID destinationWarehouseId);
    
    /**
     * Get transfer requests by status
     *
     * @param status the status
     * @return list of transfer requests
     */
    List<TransferRequest> getTransferRequestsByStatus(TransferStatus status);
    
    /**
     * Update the status of a transfer request
     *
     * @param id the transfer request ID
     * @param status the new status
     * @return the updated transfer request
     */
    TransferRequest updateTransferRequestStatus(UUID id, TransferStatus status);
    
    /**
     * Update the status of a transfer item
     *
     * @param transferRequestId the transfer request ID
     * @param transferItemId the transfer item ID
     * @param status the new status
     * @return the updated transfer request
     */
    TransferRequest updateTransferItemStatus(UUID transferRequestId, UUID transferItemId, TransferItemStatus status);
    
    /**
     * Update tracking information for a transfer request
     *
     * @param id the transfer request ID
     * @param carrier the shipping carrier
     * @param trackingNumber the tracking number
     * @param labelUrl the shipping label URL
     * @return the updated transfer request
     */
    TransferRequest updateTrackingInformation(UUID id, String carrier, String trackingNumber, String labelUrl);
    
    /**
     * Cancel a transfer request
     *
     * @param id the transfer request ID
     * @return the updated transfer request
     */
    TransferRequest cancelTransferRequest(UUID id);
    
    /**
     * Generate a unique reference number for a new transfer request
     *
     * @return the generated reference number
     */
    String generateReferenceNumber();
} 
