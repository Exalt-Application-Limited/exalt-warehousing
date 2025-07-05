package com.gogidix.warehousing.shared.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when an inventory reservation status changes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservationStatusChangedEvent extends BaseEvent {
    
    private String reservationId;
    private UUID orderId;
    private String customerId;
    private UUID warehouseId;
    private UUID productId;
    private String sku;
    private Integer quantityReserved;
    private ReservationStatus previousStatus;
    private ReservationStatus newStatus;
    private LocalDateTime statusChangedAt;
    private String changedBy;
    private String reason;
    private String notes;
    
    public InventoryReservationStatusChangedEvent(String reservationId, UUID orderId, String customerId,
                                                UUID warehouseId, UUID productId, String sku,
                                                Integer quantityReserved, ReservationStatus previousStatus,
                                                ReservationStatus newStatus, String changedBy, String reason) {
        super("InventoryReservationStatusChanged", reservationId, "InventoryReservation");
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.sku = sku;
        this.quantityReserved = quantityReserved;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.statusChangedAt = LocalDateTime.now();
        this.changedBy = changedBy;
        this.reason = reason;
    }
}
