package com.gogidix.warehousing.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Cross-domain inventory event for publishing inventory-related changes
 * to other domains like social commerce and centralized dashboard.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CrossDomainInventoryEvent extends CrossDomainEvent {

    private String productSku;
    private UUID productId;
    private UUID warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private String inventoryStatus;
    private Double stockLevel; // percentage of stock level
    private Boolean lowStock;
    private Boolean outOfStock;
    private String inventoryAction;
    private Map<String, Object> productMetadata;

    /**
     * Factory method to create a stock update event
     * 
     * @param productId the product ID
     * @param productSku the product SKU
     * @param warehouseId the warehouse ID
     * @param availableQuantity available quantity
     * @param reservedQuantity reserved quantity
     * @return the cross-domain event
     */
    public static CrossDomainInventoryEvent createStockUpdate(
            UUID productId, 
            String productSku, 
            UUID warehouseId,
            String warehouseCode,
            Integer availableQuantity, 
            Integer reservedQuantity) {
        
        boolean isOutOfStock = availableQuantity <= 0;
        boolean isLowStock = availableQuantity > 0 && availableQuantity <= 5;
        String status = isOutOfStock ? "OUT_OF_STOCK" : (isLowStock ? "LOW_STOCK" : "IN_STOCK");
        
        return CrossDomainInventoryEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("INVENTORY_UPDATED")
                .aggregateId(productId)
                .aggregateType("Product")
                .targetDomain("all")
                .targetService("inventory-sync")
                .priority(isOutOfStock || isLowStock ? EventPriority.HIGH : EventPriority.MEDIUM)
                .businessEntityId(productId)
                .businessEntityType("Product")
                .productId(productId)
                .productSku(productSku)
                .warehouseId(warehouseId)
                .warehouseCode(warehouseCode)
                .availableQuantity(availableQuantity)
                .reservedQuantity(reservedQuantity)
                .inventoryStatus(status)
                .lowStock(isLowStock)
                .outOfStock(isOutOfStock)
                .inventoryAction("STOCK_UPDATE")
                .productMetadata(new HashMap<>())
                .build();
    }
    
    /**
     * Factory method to create a low stock alert event
     * 
     * @param productId the product ID
     * @param productSku the product SKU
     * @param warehouseId the warehouse ID
     * @param availableQuantity available quantity
     * @return the cross-domain event
     */
    public static CrossDomainInventoryEvent createLowStockAlert(
            UUID productId, 
            String productSku, 
            UUID warehouseId,
            String warehouseName,
            Integer availableQuantity) {
        
        return CrossDomainInventoryEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("LOW_STOCK_ALERT")
                .aggregateId(productId)
                .aggregateType("Product")
                .targetDomain("social-commerce")
                .targetService("inventory-alerts")
                .priority(EventPriority.HIGH)
                .businessEntityId(productId)
                .businessEntityType("Product")
                .productId(productId)
                .productSku(productSku)
                .warehouseId(warehouseId)
                .warehouseName(warehouseName)
                .availableQuantity(availableQuantity)
                .inventoryStatus("LOW_STOCK")
                .lowStock(true)
                .outOfStock(false)
                .inventoryAction("ALERT")
                .productMetadata(new HashMap<>())
                .build();
    }
    
    /**
     * Factory method to create an out of stock alert event
     * 
     * @param productId the product ID
     * @param productSku the product SKU
     * @param warehouseId the warehouse ID
     * @return the cross-domain event
     */
    public static CrossDomainInventoryEvent createOutOfStockAlert(
            UUID productId, 
            String productSku, 
            UUID warehouseId,
            String warehouseName) {
        
        return CrossDomainInventoryEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("OUT_OF_STOCK_ALERT")
                .aggregateId(productId)
                .aggregateType("Product")
                .targetDomain("social-commerce")
                .targetService("inventory-alerts")
                .priority(EventPriority.CRITICAL)
                .businessEntityId(productId)
                .businessEntityType("Product")
                .productId(productId)
                .productSku(productSku)
                .warehouseId(warehouseId)
                .warehouseName(warehouseName)
                .availableQuantity(0)
                .inventoryStatus("OUT_OF_STOCK")
                .lowStock(false)
                .outOfStock(true)
                .inventoryAction("ALERT")
                .productMetadata(new HashMap<>())
                .build();
    }
}