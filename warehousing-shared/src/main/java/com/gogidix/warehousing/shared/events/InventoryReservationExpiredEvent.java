package com.gogidix.warehousing.shared.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when an inventory reservation expires
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservationExpiredEvent extends BaseEvent {
    
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
    private LocalDateTime reservedAt;
    private LocalDateTime expirationTime;
    private LocalDateTime expiredAt;
    private String expiredBy;
    private boolean autoExpired;
    private String notes;
    
    public InventoryReservationExpiredEvent(String reservationId, String orderId, String customerId,
                                          String warehouseId, String productId, String sku,
                                          Integer quantityReserved, Integer quantityReleased,
                                          BigDecimal unitPrice, BigDecimal totalValue,
                                          LocalDateTime reservedAt, LocalDateTime expirationTime,
                                          boolean autoExpired) {
        super("InventoryReservationExpired", reservationId, "InventoryReservation");
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
        this.reservedAt = reservedAt;
        this.expirationTime = expirationTime;
        this.expiredAt = LocalDateTime.now();
        this.expiredBy = autoExpired ? "SYSTEM" : "MANUAL";
        this.autoExpired = autoExpired;
    }
}
