package com.gogidix.warehousing.pricing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.RoundingMode;

/**
 * Availability Snapshot Entity
 * 
 * Real-time snapshot of storage unit availability across facilities.
 */
@Entity
@Table(name = "availability_snapshots")
@Data
@EqualsAndHashCode(callSuper = false)
public class AvailabilitySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long facilityId;

    @Column(nullable = false)
    private String facilityCode;

    @Column(nullable = false)
    private String unitType;

    @Column(nullable = false)
    private String unitSize;

    @Column(nullable = false)
    private Integer totalUnits;

    @Column(nullable = false)
    private Integer availableUnits;

    @Column(nullable = false)
    private Integer reservedUnits;

    @Column(nullable = false)
    private Integer occupiedUnits;

    @Column(nullable = false)
    private Integer maintenanceUnits;

    @Column(nullable = false)
    private BigDecimal occupancyRate;

    @Column(nullable = false)
    private BigDecimal currentPrice;

    @Column(nullable = false)
    private BigDecimal recommendedPrice;

    @Column
    private BigDecimal priceAdjustment;

    @Column(nullable = false)
    private Integer demandScore;

    @Column
    private LocalDateTime lastBookingDate;

    @Column
    private LocalDateTime nextAvailableDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime snapshotTimestamp;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String unitTypeBreakdown;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    // Business Methods
    public BigDecimal getAvailabilityRate() {
        if (totalUnits == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(availableUnits)
                .divide(BigDecimal.valueOf(totalUnits), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean hasAvailability() {
        return this.availableUnits > 0;
    }

    public boolean isHighDemand() {
        return this.demandScore >= 80;
    }

    public boolean isLowAvailability() {
        return getAvailabilityRate().compareTo(BigDecimal.valueOf(20)) <= 0;
    }
}