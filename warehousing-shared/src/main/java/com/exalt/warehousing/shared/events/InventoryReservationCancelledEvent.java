package com.exalt.warehousing.shared.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when an inventory reservation is cancelled
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservationCancelledEvent extends BaseEvent {
    
    private String reservationId;
    private String orderId;
    private String customerId;
    private String warehouseId;
    private String productId;
    private String sku;
    private Integer quantityReserved;
    private Integer quantityReleased;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private ReservationStatus previousStatus;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private String cancellationReason;
    private String refundId;
    private String notes;
    
    public InventoryReservationCancelledEvent(String reservationId, String orderId, String customerId,
                                            String warehouseId, String productId, String sku,
                                            Integer quantityReserved, Integer quantityReleased,
                                            BigDecimal unitPrice, BigDecimal totalValue,
                                            ReservationStatus previousStatus, String cancelledBy,
                                            String cancellationReason) {
        super("InventoryReservationCancelled", reservationId, "InventoryReservation");
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.sku = sku;
        this.quantityReserved = quantityReserved;
        this.quantityReleased = quantityReleased;
        this.unitPrice = unitPrice;
        this.totalValue = totalValue;
        this.previousStatus = previousStatus;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = cancellationReason;
    }
}
