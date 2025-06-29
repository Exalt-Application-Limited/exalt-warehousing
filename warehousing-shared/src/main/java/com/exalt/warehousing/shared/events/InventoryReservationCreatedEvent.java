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
 * Event published when an inventory reservation is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservationCreatedEvent extends BaseEvent {
    
    private String reservationId;
    private UUID orderId;
    private UUID userId;
    private String customerId;
    private UUID warehouseId;
    private UUID productId;
    private String sku;
    private Integer quantityReserved;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    private Integer expirationMinutes;
    private String reservedBy;
    private String notes;
    private String source;
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
    
    public InventoryReservationCreatedEvent(String reservationId, UUID orderId, String customerId,
                                          UUID warehouseId, UUID productId, String sku,
                                          Integer quantityReserved, BigDecimal unitPrice,
                                          BigDecimal totalValue, LocalDateTime expiresAt,
                                          String reservedBy) {
        super("InventoryReservationCreated", reservationId, "InventoryReservation");
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.sku = sku;
        this.quantityReserved = quantityReserved;
        this.unitPrice = unitPrice;
        this.totalValue = totalValue;
        this.status = ReservationStatus.PENDING;
        this.reservedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.reservedBy = reservedBy;
    }
}
