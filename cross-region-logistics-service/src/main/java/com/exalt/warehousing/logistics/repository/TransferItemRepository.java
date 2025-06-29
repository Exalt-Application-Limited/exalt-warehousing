package com.exalt.warehousing.logistics.repository;

import com.exalt.warehousing.logistics.model.TransferItem;
import com.exalt.warehousing.logistics.model.TransferItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for TransferItem entities
 */
@Repository
public interface TransferItemRepository extends JpaRepository<TransferItem, UUID> {
    
    /**
     * Find transfer items by transfer request ID
     */
    List<TransferItem> findByTransferRequestId(UUID transferRequestId);
    
    /**
     * Find transfer items by product ID
     */
    List<TransferItem> findByProductId(UUID productId);
    
    /**
     * Find transfer items by SKU
     */
    List<TransferItem> findBySku(String sku);
    
    /**
     * Find transfer items by status
     */
    List<TransferItem> findByStatus(TransferItemStatus status);
    
    /**
     * Find transfer items by transfer request ID and status
     */
    List<TransferItem> findByTransferRequestIdAndStatus(UUID transferRequestId, TransferItemStatus status);
    
    /**
     * Find transfer items by source location ID
     */
    List<TransferItem> findBySourceLocationId(UUID sourceLocationId);
    
    /**
     * Find transfer items by destination location ID
     */
    List<TransferItem> findByDestinationLocationId(UUID destinationLocationId);
    
    /**
     * Find transfer items by lot number
     */
    List<TransferItem> findByLotNumber(String lotNumber);
    
    /**
     * Find transfer items by serial number
     */
    List<TransferItem> findBySerialNumber(String serialNumber);
} 
