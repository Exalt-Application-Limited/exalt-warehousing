package com.exalt.warehousing.shared.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event published when an inventory reservation is completed/fulfilled
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservationCompletedEvent extends BaseEvent {
    
    private String reservationId;
    private String orderId;
    private String customerId;
    private String warehouseId;
    private String productId;
    private String sku;
    private Integer quantityReserved;
    private Integer quantityFulfilled;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private LocalDateTime completedAt;
    private String completedBy;
    private String fulfillmentOrderId;
    private String shipmentId;
    private String trackingNumber;
    private String notes;
    private List<ReservationItem> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationItem {
        private UUID inventoryItemId;
        private UUID warehouseId;
        private String sku;
        private Integer quantity;
        private String notes;
        private UUID productId;
        private String productName;
        private Double unitPrice;
    }
    
    public InventoryReservationCompletedEvent(String reservationId, String orderId, String customerId,
                                            String warehouseId, String productId, String sku,
                                            Integer quantityReserved, Integer quantityFulfilled,
                                            BigDecimal unitPrice, BigDecimal totalValue,
                                            String completedBy, String fulfillmentOrderId) {
        super("InventoryReservationCompleted", reservationId, "InventoryReservation");
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.sku = sku;
        this.quantityReserved = quantityReserved;
        this.quantityFulfilled = quantityFulfilled;
        this.unitPrice = unitPrice;
        this.totalValue = totalValue;
        this.completedAt = LocalDateTime.now();
        this.completedBy = completedBy;
        this.fulfillmentOrderId = fulfillmentOrderId;
    }
}
