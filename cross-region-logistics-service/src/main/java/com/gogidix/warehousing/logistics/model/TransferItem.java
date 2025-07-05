package com.gogidix.warehousing.logistics.model;

import com.gogidix.warehousing.shared.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Represents an item within a transfer request
 */
@Entity
@Table(name = "transfer_item")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "transferRequest")
@ToString(exclude = "transferRequest")
public class TransferItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_request_id", nullable = false)
    private TransferRequest transferRequest;

    @Column(name = "inventory_id", nullable = false)
    private UUID inventoryId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @Column(name = "actual_quantity")
    private Integer actualQuantity;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferItemStatus status;

    @Column(name = "unit_weight")
    private Double unitWeight;

    @Column(name = "unit_volume")
    private Double unitVolume;

    @Column(name = "source_location_id")
    private UUID sourceLocationId;

    @Column(name = "destination_location_id")
    private UUID destinationLocationId;

    @Column(name = "notes")
    private String notes;

    @Column(name = "lot_number")
    private String lotNumber;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "expiration_date")
    private String expirationDate;
} 
