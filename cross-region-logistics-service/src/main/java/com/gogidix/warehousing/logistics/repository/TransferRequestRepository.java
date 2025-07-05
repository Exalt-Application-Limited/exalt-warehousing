package com.gogidix.warehousing.logistics.repository;

import com.gogidix.warehousing.logistics.model.TransferRequest;
import com.gogidix.warehousing.logistics.model.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for TransferRequest entities
 */
@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, UUID> {
    
    /**
     * Find transfer requests by source warehouse ID
     */
    List<TransferRequest> findBySourceWarehouseId(UUID sourceWarehouseId);
    
    /**
     * Find transfer requests by destination warehouse ID
     */
    List<TransferRequest> findByDestinationWarehouseId(UUID destinationWarehouseId);
    
    /**
     * Find transfer requests by reference number
     */
    TransferRequest findByReferenceNumber(String referenceNumber);
    
    /**
     * Find transfer requests by status
     */
    List<TransferRequest> findByStatus(TransferStatus status);
    
    /**
     * Find transfer requests by requested by user ID
     */
    List<TransferRequest> findByRequestedBy(UUID requestedBy);
    
    /**
     * Find transfer requests by source warehouse ID and status
     */
    List<TransferRequest> findBySourceWarehouseIdAndStatus(UUID sourceWarehouseId, TransferStatus status);
    
    /**
     * Find transfer requests by destination warehouse ID and status
     */
    List<TransferRequest> findByDestinationWarehouseIdAndStatus(UUID destinationWarehouseId, TransferStatus status);
    
    /**
     * Find transfer requests with expected pickup date before the given date
     */
    List<TransferRequest> findByExpectedPickupDateBefore(LocalDateTime date);
    
    /**
     * Find transfer requests with expected delivery date before the given date 
     */
    List<TransferRequest> findByExpectedDeliveryDateBefore(LocalDateTime date);
    
    /**
     * Find transfer requests created between the given dates
     */
    List<TransferRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find transfer requests by status and completed between the given dates
     */
    List<TransferRequest> findByStatusAndCompletedAtBetween(TransferStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find transfer requests by status, source warehouse ID and completed between the given dates
     */
    List<TransferRequest> findByStatusAndSourceWarehouseIdAndCompletedAtBetween(TransferStatus status, UUID sourceWarehouseId, LocalDateTime startDate, LocalDateTime endDate);
} 
