package com.gogidix.warehousing.logistics.integration;

import com.gogidix.warehousing.logistics.client.InventoryClient;
import com.gogidix.warehousing.logistics.client.WarehouseClient;
import com.gogidix.warehousing.logistics.model.TransferItem;
import com.gogidix.warehousing.logistics.model.TransferItemStatus;
import com.gogidix.warehousing.logistics.model.TransferRequest;
import com.gogidix.warehousing.logistics.model.TransferStatus;
import com.gogidix.warehousing.logistics.service.TransferService;
import com.gogidix.warehousing.shared.exception.ServiceIntegrationException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Coordinates the workflow between different services for transfer operations
 */
@Component
@Slf4j
public class TransferWorkflowCoordinator {

    private final TransferService transferService;
    private final InventoryClient inventoryClient;
    private final WarehouseClient warehouseClient;
    
    @Value("${integration.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${integration.retry.delay-ms:500}")
    private long retryDelayMs;

    @Autowired
    public TransferWorkflowCoordinator(
            TransferService transferService,
            InventoryClient inventoryClient,
            WarehouseClient warehouseClient) {
        this.transferService = transferService;
        this.inventoryClient = inventoryClient;
        this.warehouseClient = warehouseClient;
    }

    /**
     * Process a transfer request approval
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest approveTransfer(UUID transferRequestId) {
        log.info("Processing approval for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in PENDING_APPROVAL status
        if (transferRequest.getStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Transfer request is not in PENDING_APPROVAL status");
        }
        
        try {
            // Reserve inventory in source warehouse
            for (TransferItem item : transferRequest.getItems()) {
                Map<String, Object> reservationRequest = new HashMap<>();
                reservationRequest.put("inventoryId", item.getInventoryId());
                reservationRequest.put("quantity", item.getRequestedQuantity());
                reservationRequest.put("reason", "TRANSFER");
                reservationRequest.put("referenceId", transferRequest.getReferenceNumber());
                
                // Use retry mechanism for inventory operations
                withRetry(() -> inventoryClient.reserveInventory(reservationRequest),
                        "Failed to reserve inventory for item: " + item.getId());
            }
            
            // Update transfer request status
            return transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.APPROVED);
        } catch (Exception e) {
            log.error("Failed to approve transfer request: {}", transferRequestId, e);
            
            // Attempt to release any previously reserved inventory 
            try {
                rollbackInventoryReservations(transferRequest);
            } catch (Exception rollbackEx) {
                log.error("Failed to rollback inventory reservations for transfer request: {}", 
                        transferRequestId, rollbackEx);
            }
            
            if (e instanceof ServiceIntegrationException) {
                throw e;
            }
            throw new ServiceIntegrationException("Failed to approve transfer: " + e.getMessage(), e);
        }
    }

    /**
     * Rollback inventory reservations for a transfer request
     *
     * @param transferRequest the transfer request
     */
    private void rollbackInventoryReservations(TransferRequest transferRequest) {
        log.info("Rolling back inventory reservations for transfer request: {}", transferRequest.getId());
        
        for (TransferItem item : transferRequest.getItems()) {
            try {
                Map<String, Object> releaseRequest = new HashMap<>();
                releaseRequest.put("inventoryId", item.getInventoryId());
                releaseRequest.put("quantity", item.getRequestedQuantity());
                releaseRequest.put("reason", "TRANSFER_CANCELLED");
                releaseRequest.put("referenceId", transferRequest.getReferenceNumber());
                
                inventoryClient.releaseInventory(releaseRequest);
            } catch (Exception e) {
                log.error("Failed to release inventory for item: {}", item.getId(), e);
                // Continue with other items even if one fails
            }
        }
    }

    /**
     * Process a transfer request picking completion
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest completePickingForTransfer(UUID transferRequestId) {
        log.info("Processing picking completion for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in PICKING status
        if (transferRequest.getStatus() != TransferStatus.PICKING) {
            throw new IllegalStateException("Transfer request is not in PICKING status");
        }
        
        // Check if all items have been picked
        boolean allItemsPicked = transferRequest.getItems().stream()
                .allMatch(item -> item.getStatus() == TransferItemStatus.PICKED);
        
        if (!allItemsPicked) {
            throw new IllegalStateException("Not all items have been picked");
        }
        
        // Update transfer request status
        return transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.PACKING);
    }

    /**
     * Process a transfer request packing completion
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest completePackingForTransfer(UUID transferRequestId) {
        log.info("Processing packing completion for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in PACKING status
        if (transferRequest.getStatus() != TransferStatus.PACKING) {
            throw new IllegalStateException("Transfer request is not in PACKING status");
        }
        
        // Check if all items have been packed
        boolean allItemsPacked = transferRequest.getItems().stream()
                .allMatch(item -> item.getStatus() == TransferItemStatus.PACKED);
        
        if (!allItemsPacked) {
            throw new IllegalStateException("Not all items have been packed");
        }
        
        // Update transfer request status
        return transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.READY_FOR_PICKUP);
    }

    /**
     * Process a transfer request pickup
     *
     * @param transferRequestId the transfer request ID
     * @param carrierId the carrier ID
     * @param trackingNumber the tracking number
     * @param shippingLabelUrl the shipping label URL
     * @return the updated transfer request
     */
    public TransferRequest pickupTransfer(UUID transferRequestId, String carrierId, String trackingNumber, String shippingLabelUrl) {
        log.info("Processing pickup for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in READY_FOR_PICKUP status
        if (transferRequest.getStatus() != TransferStatus.READY_FOR_PICKUP) {
            throw new IllegalStateException("Transfer request is not in READY_FOR_PICKUP status");
        }
        
        try {
            // Update shipping information
            transferService.updateTrackingInformation(transferRequestId, carrierId, trackingNumber, shippingLabelUrl);
            
            // Update status to IN_TRANSIT
            TransferRequest updatedRequest = transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.IN_TRANSIT);
            
            // Update all items to IN_TRANSIT status
            for (TransferItem item : updatedRequest.getItems()) {
                transferService.updateTransferItemStatus(transferRequestId, UUID.fromString(item.getId()), TransferItemStatus.IN_TRANSIT);
            }
            
            return updatedRequest;
        } catch (Exception e) {
            log.error("Failed to process pickup for transfer request: {}", transferRequestId, e);
            
            // Set the transfer request to EXCEPTION status
            try {
                transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.EXCEPTION);
            } catch (Exception statusEx) {
                log.error("Failed to update transfer request status to EXCEPTION: {}", transferRequestId, statusEx);
            }
            
            if (e instanceof ServiceIntegrationException) {
                throw e;
            }
            throw new ServiceIntegrationException("Failed to process pickup: " + e.getMessage(), e);
        }
    }

    /**
     * Process a transfer request arrival
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest arriveTransfer(UUID transferRequestId) {
        log.info("Processing arrival for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in IN_TRANSIT status
        if (transferRequest.getStatus() != TransferStatus.IN_TRANSIT) {
            throw new IllegalStateException("Transfer request is not in IN_TRANSIT status");
        }
        
        // Update status to ARRIVED
        TransferRequest updatedRequest = transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.ARRIVED);
        
        // Update all items to ARRIVED status
        for (TransferItem item : updatedRequest.getItems()) {
            transferService.updateTransferItemStatus(transferRequestId, UUID.fromString(item.getId()), TransferItemStatus.ARRIVED);
        }
        
        return updatedRequest;
    }

    /**
     * Process a transfer request verification
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest verifyTransfer(UUID transferRequestId) {
        log.info("Processing verification for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in ARRIVED status
        if (transferRequest.getStatus() != TransferStatus.ARRIVED) {
            throw new IllegalStateException("Transfer request is not in ARRIVED status");
        }
        
        // Update status to VERIFYING
        return transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.VERIFYING);
    }

    /**
     * Process a transfer request completion
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest completeTransfer(UUID transferRequestId) {
        log.info("Processing completion for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Validate current status - must be in VERIFYING status
        if (transferRequest.getStatus() != TransferStatus.VERIFYING) {
            throw new IllegalStateException("Transfer request is not in VERIFYING status");
        }
        
        try {
            // Transfer inventory from source to destination warehouse
            for (TransferItem item : transferRequest.getItems()) {
                Map<String, Object> transferReq = new HashMap<>();
                transferReq.put("inventoryId", item.getInventoryId());
                transferReq.put("sourceWarehouseId", transferRequest.getSourceWarehouseId());
                transferReq.put("destinationWarehouseId", transferRequest.getDestinationWarehouseId());
                transferReq.put("quantity", item.getActualQuantity() != null ? item.getActualQuantity() : item.getRequestedQuantity());
                transferReq.put("reason", "TRANSFER");
                transferReq.put("referenceId", transferRequest.getReferenceNumber());
                
                // Use retry mechanism for inventory operations
                withRetry(() -> inventoryClient.transferInventory(transferReq),
                        "Failed to transfer inventory for item: " + item.getId());
                
                // Update item status to COMPLETED
                transferService.updateTransferItemStatus(transferRequestId, UUID.fromString(item.getId()), TransferItemStatus.COMPLETED);
            }
            
            // Update transfer request status to COMPLETED
            return transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.COMPLETED);
        } catch (Exception e) {
            log.error("Failed to complete transfer request: {}", transferRequestId, e);
            
            // Update transfer request status to EXCEPTION
            try {
                transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.EXCEPTION);
            } catch (Exception statusEx) {
                log.error("Failed to update transfer request status to EXCEPTION: {}", transferRequestId, statusEx);
            }
            
            if (e instanceof ServiceIntegrationException) {
                throw e;
            }
            throw new ServiceIntegrationException("Failed to complete transfer: " + e.getMessage(), e);
        }
    }

    /**
     * Process a transfer request cancellation
     *
     * @param transferRequestId the transfer request ID
     * @return the updated transfer request
     */
    public TransferRequest cancelTransfer(UUID transferRequestId) {
        log.info("Processing cancellation for transfer request: {}", transferRequestId);
        
        TransferRequest transferRequest = transferService.getTransferRequest(transferRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferRequestId));
        
        // Only allow cancellation for specific statuses
        TransferStatus currentStatus = transferRequest.getStatus();
        if (currentStatus == TransferStatus.COMPLETED || 
                currentStatus == TransferStatus.CANCELLED) {
            throw new IllegalStateException("Transfer request cannot be cancelled in " + currentStatus + " status");
        }
        
        try {
            // If inventory was reserved, release it
            if (currentStatus == TransferStatus.APPROVED || 
                    currentStatus == TransferStatus.PICKING || 
                    currentStatus == TransferStatus.PACKING || 
                    currentStatus == TransferStatus.READY_FOR_PICKUP) {
                
                rollbackInventoryReservations(transferRequest);
            }
            
            // Cancel the transfer request
            return transferService.cancelTransferRequest(transferRequestId);
        } catch (Exception e) {
            log.error("Failed to cancel transfer request: {}", transferRequestId, e);
            
            // Even if cancellation fails, still try to update the status to EXCEPTION
            try {
                transferService.updateTransferRequestStatus(transferRequestId, TransferStatus.EXCEPTION);
            } catch (Exception statusEx) {
                log.error("Failed to update transfer request status to EXCEPTION: {}", transferRequestId, statusEx);
            }
            
            if (e instanceof ServiceIntegrationException) {
                throw e;
            }
            throw new ServiceIntegrationException("Failed to cancel transfer: " + e.getMessage(), e);
        }
    }
    
    /**
     * Execute an operation with retry logic
     *
     * @param operation the operation to execute
     * @param errorMessage the error message if all retries fail
     * @return the result of the operation
     * @throws ServiceIntegrationException if all retries fail
     */
    private <T> T withRetry(RetryableOperation<T> operation, String errorMessage) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxRetryAttempts) {
            try {
                return operation.execute();
            } catch (FeignException e) {
                lastException = e;
                
                if (isRetryable(e)) {
                    attempts++;
                    log.warn("Retry attempt {} for operation. Error: {}", attempts, e.getMessage());
                    
                    if (attempts < maxRetryAttempts) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(retryDelayMs * attempts); // Exponential backoff
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new ServiceIntegrationException("Retry interrupted: " + ie.getMessage());
                        }
                    }
                } else {
                    // Not retryable
                    break;
                }
            } catch (Exception e) {
                lastException = e;
                break;
            }
        }
        
        throw new ServiceIntegrationException(errorMessage + 
                (lastException != null ? ": " + lastException.getMessage() : ""), lastException);
    }
    
    /**
     * Check if an exception is retryable
     *
     * @param e the exception
     * @return true if retryable
     */
    private boolean isRetryable(FeignException e) {
        // Retry on temporary errors like 429 (Too Many Requests), 502, 503, 504
        int status = e.status();
        return status == 429 || status == 502 || status == 503 || status == 504;
    }
    
    /**
     * Functional interface for retryable operations
     */
    @FunctionalInterface
    private interface RetryableOperation<T> {
        T execute();
    }
} 
