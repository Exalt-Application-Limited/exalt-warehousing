package com.gogidix.warehousing.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Storage Unit Entity
 * 
 * Represents an individual storage unit within a facility.
 * Contains specific details about size, pricing, and availability.
 */
@Entity
@Table(name = "storage_units")
@Data
@EqualsAndHashCode(callSuper = false)
public class StorageUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String unitNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private StorageFacility facility;

    // Unit Specifications
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unitType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitSize unitSize;

    @Column(nullable = false)
    private BigDecimal width;

    @Column(nullable = false)
    private BigDecimal length;

    @Column(nullable = false)
    private BigDecimal height;

    @Column(nullable = false)
    private BigDecimal squareFootage;

    @Column(nullable = false)
    private BigDecimal cubicFootage;

    // Location within facility
    @Column(nullable = false)
    private String floor;

    @Column(nullable = false)
    private String section;

    private String buildingNumber;

    // Features and Amenities
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "unit_features", joinColumns = @JoinColumn(name = "unit_id"))
    @Column(name = "feature")
    private List<UnitFeature> features = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isClimateControlled = false;

    @Column(nullable = false)
    private Boolean hasElectricity = false;

    @Column(nullable = false)
    private Boolean hasDriveUpAccess = false;

    @Column(nullable = false)
    private Boolean hasElevatorAccess = false;

    // Pricing
    @Column(nullable = false)
    private BigDecimal monthlyRate;

    @Column(nullable = false)
    private BigDecimal securityDeposit;

    private BigDecimal adminFee;

    private BigDecimal insuranceRate;

    // Availability and Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitStatus status;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Column
    private LocalDateTime availableDate;

    @Column
    private String currentTenantId;

    @Column
    private LocalDateTime leaseStartDate;

    @Column
    private LocalDateTime leaseEndDate;

    // Maintenance and Condition
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitCondition condition;

    @Column
    private LocalDateTime lastInspectionDate;

    @Column
    private LocalDateTime lastCleaningDate;

    @Column
    private LocalDateTime nextMaintenanceDate;

    @Column(columnDefinition = "TEXT")
    private String maintenanceNotes;

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // One-to-many relationships
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UnitReservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UnitImage> images = new ArrayList<>();

    // Business methods
    public BigDecimal getTotalMonthlyRate() {
        BigDecimal total = this.monthlyRate;
        if (this.insuranceRate != null) {
            total = total.add(this.insuranceRate);
        }
        return total;
    }

    public BigDecimal getTotalUpfrontCost() {
        BigDecimal total = this.monthlyRate.add(this.securityDeposit);
        if (this.adminFee != null) {
            total = total.add(this.adminFee);
        }
        return total;
    }

    public boolean isBookable() {
        return this.isAvailable && 
               this.status == UnitStatus.AVAILABLE &&
               this.condition != UnitCondition.OUT_OF_ORDER;
    }

    public void makeUnavailable() {
        this.isAvailable = false;
        this.status = UnitStatus.OCCUPIED;
    }

    public void makeAvailable() {
        this.isAvailable = true;
        this.status = UnitStatus.AVAILABLE;
        this.currentTenantId = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
    }

    public void reserve(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        this.currentTenantId = tenantId;
        this.leaseStartDate = startDate;
        this.leaseEndDate = endDate;
        this.status = UnitStatus.RESERVED;
        this.isAvailable = false;
    }

    public void occupy(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        this.currentTenantId = tenantId;
        this.leaseStartDate = startDate;
        this.leaseEndDate = endDate;
        this.status = UnitStatus.OCCUPIED;
        this.isAvailable = false;
    }

    public boolean requiresClimateControl() {
        return this.isClimateControlled;
    }

    public String getDisplaySize() {
        return String.format("%.0f' x %.0f' x %.0f'", 
                           this.width, this.length, this.height);
    }

    // Enums
    public enum UnitType {
        SMALL,
        MEDIUM,
        LARGE,
        EXTRA_LARGE,
        VEHICLE,
        CLIMATE_CONTROLLED,
        DRIVE_UP,
        INDOOR,
        OUTDOOR
    }

    public enum UnitSize {
        SIZE_5X5,    // 25 sq ft
        SIZE_5X10,   // 50 sq ft
        SIZE_5X15,   // 75 sq ft
        SIZE_10X10,  // 100 sq ft
        SIZE_10X15,  // 150 sq ft
        SIZE_10X20,  // 200 sq ft
        SIZE_10X25,  // 250 sq ft
        SIZE_10X30,  // 300 sq ft
        SIZE_15X20,  // 300 sq ft
        SIZE_20X20,  // 400 sq ft
        SIZE_20X30,  // 600 sq ft
        SIZE_CUSTOM
    }

    public enum UnitFeature {
        GROUND_FLOOR,
        UPPER_FLOOR,
        CORNER_UNIT,
        END_UNIT,
        DOUBLE_WIDE,
        HIGH_CEILING,
        VEHICLE_ACCESS,
        LOADING_DOCK_ACCESS,
        POWER_OUTLET,
        LIGHTING,
        VENTILATION,
        SECURITY_ALARM,
        INDIVIDUAL_ALARM,
        FIRE_SPRINKLER
    }

    public enum UnitStatus {
        AVAILABLE,
        RESERVED,
        OCCUPIED,
        MAINTENANCE,
        OUT_OF_ORDER,
        PENDING_INSPECTION,
        CLEANING_REQUIRED
    }

    public enum UnitCondition {
        EXCELLENT,
        GOOD,
        FAIR,
        NEEDS_REPAIR,
        OUT_OF_ORDER,
        UNDER_RENOVATION
    }
}