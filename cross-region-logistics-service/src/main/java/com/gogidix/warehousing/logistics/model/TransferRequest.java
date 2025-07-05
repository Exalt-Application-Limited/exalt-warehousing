package com.gogidix.warehousing.logistics.model;

import com.gogidix.warehousing.shared.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a transfer request between warehouses in different regions
 */
@Entity
@Table(name = "transfer_request")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferRequest extends BaseEntity {

    @Column(name = "source_warehouse_id", nullable = false)
    private UUID sourceWarehouseId;

    @Column(name = "destination_warehouse_id", nullable = false)
    private UUID destinationWarehouseId;

    @Column(name = "reference_number", nullable = false, unique = true)
    private String referenceNumber;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferPriority priority;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column(name = "requested_by", nullable = false)
    private UUID requestedBy;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "expected_pickup_date")
    private LocalDateTime expectedPickupDate;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "actual_pickup_date")
    private LocalDateTime actualPickupDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "shipping_carrier")
    private String shippingCarrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "shipping_label_url")
    private String shippingLabelUrl;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "total_volume")
    private Double totalVolume;

    @Column(name = "package_count")
    private Integer packageCount;

    @OneToMany(mappedBy = "transferRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransferItem> items = new ArrayList<>();

    /**
     * Add an item to the transfer request
     *
     * @param item the item to add
     * @return the transfer request for method chaining
     */
    public TransferRequest addItem(TransferItem item) {
        items.add(item);
        item.setTransferRequest(this);
        return this;
    }

    /**
     * Remove an item from the transfer request
     *
     * @param item the item to remove
     * @return the transfer request for method chaining
     */
    public TransferRequest removeItem(TransferItem item) {
        items.remove(item);
        item.setTransferRequest(null);
        return this;
    }
} 
