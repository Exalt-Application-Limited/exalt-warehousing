package com.exalt.warehousing.logistics.service.impl;

import com.exalt.warehousing.logistics.model.TransferItem;
import com.exalt.warehousing.logistics.model.TransferItemStatus;
import com.exalt.warehousing.logistics.model.TransferRequest;
import com.exalt.warehousing.logistics.model.TransferStatus;
import com.exalt.warehousing.logistics.repository.TransferItemRepository;
import com.exalt.warehousing.logistics.repository.TransferRequestRepository;
import com.exalt.warehousing.logistics.service.TransferService;
import com.exalt.warehousing.logistics.service.TransferValidationService;
import com.exalt.warehousing.shared.exception.ResourceNotFoundException;
import com.exalt.warehousing.shared.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of the TransferService interface
 */
@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransferRequestRepository transferRequestRepository;
    private final TransferItemRepository transferItemRepository;
    private final TransferValidationService validationService;
    
    @Autowired
    public TransferServiceImpl(TransferRequestRepository transferRequestRepository, 
                             TransferItemRepository transferItemRepository,
                             TransferValidationService validationService) {
        this.transferRequestRepository = transferRequestRepository;
        this.transferItemRepository = transferItemRepository;
        this.validationService = validationService;
    }
    
    @Override
    @Transactional
    public TransferRequest createTransferRequest(TransferRequest transferRequest) {
        log.info("Creating new transfer request from warehouse {} to warehouse {}", 
                transferRequest.getSourceWarehouseId(), transferRequest.getDestinationWarehouseId());
        
        // Validate the transfer request
        TransferValidationService.ValidationResult validationResult = 
                validationService.validateTransferRequest(transferRequest);
        
        if (!validationResult.isValid()) {
            throw new ValidationException("Transfer request validation failed: " + String.join(", ", validationResult.getErrors()));
        }
        
        // Set initial status if not provided
        if (transferRequest.getStatus() == null) {
            transferRequest.setStatus(TransferStatus.DRAFT);
        }
        
        // Generate reference number if not provided
        if (transferRequest.getReferenceNumber() == null || transferRequest.getReferenceNumber().trim().isEmpty()) {
            transferRequest.setReferenceNumber(generateReferenceNumber());
        }
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        transferRequest.setCreatedAt(now);
        transferRequest.setUpdatedAt(now);
        
        // Set request timestamp if not provided
        if (transferRequest.getRequestedAt() == null) {
            transferRequest.setRequestedAt(now);
        }
        
        // Set initial status for all items
        transferRequest.getItems().forEach(item -> {
            item.setStatus(TransferItemStatus.PENDING);
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
        });
        
        return transferRequestRepository.save(transferRequest);
    }
    
    @Override
    public Optional<TransferRequest> getTransferRequest(UUID id) {
        log.debug("Fetching transfer request with id: {}", id);
        return transferRequestRepository.findById(id);
    }
    
    @Override
    public Optional<TransferRequest> getTransferRequestByReferenceNumber(String referenceNumber) {
        log.debug("Fetching transfer request with reference number: {}", referenceNumber);
        return Optional.ofNullable(transferRequestRepository.findByReferenceNumber(referenceNumber));
    }
    
    @Override
    public List<TransferRequest> getAllTransferRequests() {
        log.debug("Fetching all transfer requests");
        return transferRequestRepository.findAll();
    }
    
    @Override
    public List<TransferRequest> getTransferRequestsBySourceWarehouse(UUID sourceWarehouseId) {
        log.debug("Fetching transfer requests from source warehouse: {}", sourceWarehouseId);
        return transferRequestRepository.findBySourceWarehouseId(sourceWarehouseId);
    }
    
    @Override
    public List<TransferRequest> getTransferRequestsByDestinationWarehouse(UUID destinationWarehouseId) {
        log.debug("Fetching transfer requests to destination warehouse: {}", destinationWarehouseId);
        return transferRequestRepository.findByDestinationWarehouseId(destinationWarehouseId);
    }
    
    @Override
    public List<TransferRequest> getTransferRequestsByStatus(TransferStatus status) {
        log.debug("Fetching transfer requests with status: {}", status);
        return transferRequestRepository.findByStatus(status);
    }
    
    @Override
    @Transactional
    public TransferRequest updateTransferRequestStatus(UUID id, TransferStatus status) {
        log.info("Updating status of transfer request {} to {}", id, status);
        
        TransferRequest transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found with id: " + id));
        
        // Validate status transition
        validateStatusTransition(transferRequest.getStatus(), status);
        
        transferRequest.setStatus(status);
        transferRequest.setUpdatedAt(LocalDateTime.now());
        
        // Update transfer item statuses based on transfer request status
        if (status == TransferStatus.APPROVED) {
            transferRequest.getItems().forEach(item -> {
                if (item.getStatus() == TransferItemStatus.PENDING) {
                    item.setStatus(TransferItemStatus.PICKING);
                    item.setUpdatedAt(LocalDateTime.now());
                }
            });
        } else if (status == TransferStatus.IN_TRANSIT) {
            transferRequest.getItems().forEach(item -> {
                if (item.getStatus() == TransferItemStatus.PICKED || item.getStatus() == TransferItemStatus.PACKED) {
                    item.setStatus(TransferItemStatus.IN_TRANSIT);
                    item.setUpdatedAt(LocalDateTime.now());
                }
            });
            
            // Record actual pickup date
            transferRequest.setActualPickupDate(LocalDateTime.now());
        } else if (status == TransferStatus.ARRIVED) {
            transferRequest.getItems().forEach(item -> {
                if (item.getStatus() == TransferItemStatus.IN_TRANSIT) {
                    item.setStatus(TransferItemStatus.ARRIVED);
                    item.setUpdatedAt(LocalDateTime.now());
                }
            });
            
            // Record actual delivery date
            transferRequest.setActualDeliveryDate(LocalDateTime.now());
        } else if (status == TransferStatus.CANCELLED) {
            transferRequest.getItems().forEach(item -> {
                if (item.getStatus() != TransferItemStatus.COMPLETED) {
                    item.setStatus(TransferItemStatus.CANCELLED);
                    item.setUpdatedAt(LocalDateTime.now());
                }
            });
        }
        
        return transferRequestRepository.save(transferRequest);
    }
    
    /**
     * Validate that the status transition is allowed
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     * @throws ValidationException if the transition is not allowed
     */
    private void validateStatusTransition(TransferStatus currentStatus, TransferStatus newStatus) {
        boolean valid = false;
        
        switch (currentStatus) {
            case DRAFT:
                valid = newStatus == TransferStatus.PENDING_APPROVAL || 
                        newStatus == TransferStatus.CANCELLED;
                break;
            case PENDING_APPROVAL:
                valid = newStatus == TransferStatus.APPROVED || 
                        newStatus == TransferStatus.REJECTED || 
                        newStatus == TransferStatus.CANCELLED;
                break;
            case APPROVED:
                valid = newStatus == TransferStatus.PICKING || 
                        newStatus == TransferStatus.CANCELLED;
                break;
            case PICKING:
                valid = newStatus == TransferStatus.PACKING || 
                        newStatus == TransferStatus.CANCELLED || 
                        newStatus == TransferStatus.EXCEPTION;
                break;
            case PACKING:
                valid = newStatus == TransferStatus.READY_FOR_PICKUP || 
                        newStatus == TransferStatus.CANCELLED || 
                        newStatus == TransferStatus.EXCEPTION;
                break;
            case READY_FOR_PICKUP:
                valid = newStatus == TransferStatus.IN_TRANSIT || 
                        newStatus == TransferStatus.CANCELLED || 
                        newStatus == TransferStatus.EXCEPTION;
                break;
            case IN_TRANSIT:
                valid = newStatus == TransferStatus.ARRIVED || 
                        newStatus == TransferStatus.EXCEPTION;
                break;
            case ARRIVED:
                valid = newStatus == TransferStatus.VERIFYING || 
                        newStatus == TransferStatus.EXCEPTION;
                break;
            case VERIFYING:
                valid = newStatus == TransferStatus.COMPLETED || 
                        newStatus == TransferStatus.EXCEPTION;
                break;
            case EXCEPTION:
                valid = newStatus == TransferStatus.PICKING || 
                        newStatus == TransferStatus.PACKING || 
                        newStatus == TransferStatus.READY_FOR_PICKUP || 
                        newStatus == TransferStatus.IN_TRANSIT || 
                        newStatus == TransferStatus.VERIFYING || 
                        newStatus == TransferStatus.COMPLETED || 
                        newStatus == TransferStatus.CANCELLED;
                break;
            case REJECTED:
            case COMPLETED:
            case CANCELLED:
                // Terminal states, no valid transitions
                valid = false;
                break;
            default:
                valid = false;
        }
        
        if (!valid) {
            throw new ValidationException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }
    
    @Override
    @Transactional
    public TransferRequest updateTransferItemStatus(UUID transferRequestId, UUID transferItemId, TransferItemStatus status) {
        log.info("Updating status of transfer item {} to {} in transfer request {}", transferItemId, status, transferRequestId);
        
        TransferRequest transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found with id: " + transferRequestId));
        
        TransferItem transferItem = transferRequest.getItems().stream()
                .filter(item -> item.getId().equals(transferItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Transfer item not found with id: " + transferItemId));
        
        // Validate item status transition
        validateItemStatusTransition(transferItem.getStatus(), status);
        
        transferItem.setStatus(status);
        transferItem.setUpdatedAt(LocalDateTime.now());
        
        // Check if all items are in the same status to update the transfer request status
        boolean allItemsSameStatus = transferRequest.getItems().stream()
                .allMatch(item -> item.getStatus() == status);
        
        if (allItemsSameStatus) {
            switch (status) {
                case PICKED:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.PACKING);
                    break;
                case PACKED:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.READY_FOR_PICKUP);
                    break;
                case IN_TRANSIT:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.IN_TRANSIT);
                    break;
                case ARRIVED:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.VERIFYING);
                    break;
                case COMPLETED:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.COMPLETED);
                    break;
                case CANCELLED:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.CANCELLED);
                    break;
                case EXCEPTION:
                    updateTransferRequestStatus(transferRequestId, TransferStatus.EXCEPTION);
                    break;
                default:
                    log.debug("No transfer request status update for item status: {}", status);
            }
        }
        
        return transferRequestRepository.save(transferRequest);
    }
    
    /**
     * Validate that the item status transition is allowed
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     * @throws ValidationException if the transition is not allowed
     */
    private void validateItemStatusTransition(TransferItemStatus currentStatus, TransferItemStatus newStatus) {
        boolean valid = false;
        
        switch (currentStatus) {
            case PENDING:
                valid = newStatus == TransferItemStatus.PICKING || 
                        newStatus == TransferItemStatus.CANCELLED;
                break;
            case PICKING:
                valid = newStatus == TransferItemStatus.PICKED || 
                        newStatus == TransferItemStatus.PARTIAL || 
                        newStatus == TransferItemStatus.CANCELLED || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case PICKED:
                valid = newStatus == TransferItemStatus.PACKED || 
                        newStatus == TransferItemStatus.CANCELLED || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case PACKED:
                valid = newStatus == TransferItemStatus.IN_TRANSIT || 
                        newStatus == TransferItemStatus.CANCELLED || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case IN_TRANSIT:
                valid = newStatus == TransferItemStatus.ARRIVED || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case ARRIVED:
                valid = newStatus == TransferItemStatus.VERIFYING || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case VERIFYING:
                valid = newStatus == TransferItemStatus.COMPLETED || 
                        newStatus == TransferItemStatus.PARTIAL || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case EXCEPTION:
                valid = newStatus == TransferItemStatus.PICKING || 
                        newStatus == TransferItemStatus.PICKED || 
                        newStatus == TransferItemStatus.PACKED || 
                        newStatus == TransferItemStatus.IN_TRANSIT || 
                        newStatus == TransferItemStatus.ARRIVED || 
                        newStatus == TransferItemStatus.VERIFYING || 
                        newStatus == TransferItemStatus.COMPLETED || 
                        newStatus == TransferItemStatus.PARTIAL || 
                        newStatus == TransferItemStatus.CANCELLED;
                break;
            case PARTIAL:
                valid = newStatus == TransferItemStatus.COMPLETED || 
                        newStatus == TransferItemStatus.EXCEPTION;
                break;
            case COMPLETED:
            case CANCELLED:
                // Terminal states, no valid transitions
                valid = false;
                break;
            default:
                valid = false;
        }
        
        if (!valid) {
            throw new ValidationException("Invalid item status transition from " + currentStatus + " to " + newStatus);
        }
    }
    
    @Override
    @Transactional
    public TransferRequest updateTrackingInformation(UUID id, String carrier, String trackingNumber, String labelUrl) {
        log.info("Updating tracking information for transfer request {}", id);
        
        TransferRequest transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found with id: " + id));
        
        transferRequest.setShippingCarrier(carrier);
        transferRequest.setTrackingNumber(trackingNumber);
        transferRequest.setShippingLabelUrl(labelUrl);
        transferRequest.setUpdatedAt(LocalDateTime.now());
        
        return transferRequestRepository.save(transferRequest);
    }
    
    @Override
    @Transactional
    public TransferRequest cancelTransferRequest(UUID id) {
        log.info("Cancelling transfer request {}", id);
        return updateTransferRequestStatus(id, TransferStatus.CANCELLED);
    }
    
    @Override
    public String generateReferenceNumber() {
        // Format: TR-YYYYMMDD-XXXX where XXXX is a random number
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 10000);
        
        return String.format("TR-%s-%04d", datePart, randomNum);
    }
}
