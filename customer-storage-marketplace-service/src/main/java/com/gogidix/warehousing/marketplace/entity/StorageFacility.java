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
 * Storage Facility Entity
 * 
 * Represents a storage facility available in the marketplace.
 * Contains all information needed for customers to discover and evaluate facilities.
 */
@Entity
@Table(name = "storage_facilities")
@Data
@EqualsAndHashCode(callSuper = false)
public class StorageFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String facilityCode;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Location Information
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    // Contact Information
    @Column(nullable = false)
    private String contactPhone;

    private String contactEmail;

    private String website;

    // Facility Details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityType facilityType;

    @Column(nullable = false)
    private Integer totalUnits;

    @Column(nullable = false)
    private Integer availableUnits;

    @Column(nullable = false)
    private BigDecimal totalSquareFootage;

    // Operating Hours
    @Column(nullable = false)
    private String operatingHours;

    private String holidayHours;

    // Amenities and Features
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "facility_amenities", joinColumns = @JoinColumn(name = "facility_id"))
    @Column(name = "amenity")
    private List<FacilityAmenity> amenities = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "facility_security_features", joinColumns = @JoinColumn(name = "facility_id"))
    @Column(name = "security_feature")
    private List<SecurityFeature> securityFeatures = new ArrayList<>();

    // Pricing and Business
    @Column(nullable = false)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingModel pricingModel;

    private BigDecimal discountPercentage;

    // Status and Ratings
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityStatus status;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalReviews = 0;

    // Metadata
    @Column(nullable = false)
    private String ownerId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastMaintenanceDate;

    @Column
    private LocalDateTime nextInspectionDate;

    // One-to-many relationships
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StorageUnit> storageUnits = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FacilityReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FacilityImage> images = new ArrayList<>();

    // Business methods
    public void incrementReviews() {
        this.totalReviews++;
    }

    public void updateAverageRating(BigDecimal newRating) {
        if (this.totalReviews == 0) {
            this.averageRating = newRating;
        } else {
            BigDecimal totalScore = this.averageRating.multiply(BigDecimal.valueOf(this.totalReviews - 1));
            totalScore = totalScore.add(newRating);
            this.averageRating = totalScore.divide(BigDecimal.valueOf(this.totalReviews), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public void updateAvailableUnits(int unitChange) {
        this.availableUnits += unitChange;
        if (this.availableUnits < 0) {
            this.availableUnits = 0;
        }
        if (this.availableUnits > this.totalUnits) {
            this.availableUnits = this.totalUnits;
        }
    }

    public boolean hasAvailableUnits() {
        return this.availableUnits > 0;
    }

    public BigDecimal getOccupancyRate() {
        if (totalUnits == 0) return BigDecimal.ZERO;
        int occupiedUnits = totalUnits - availableUnits;
        return BigDecimal.valueOf(occupiedUnits)
                .divide(BigDecimal.valueOf(totalUnits), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Enums
    public enum FacilityType {
        SELF_STORAGE,
        WAREHOUSE_STORAGE,
        CLIMATE_CONTROLLED,
        VEHICLE_STORAGE,
        DOCUMENT_STORAGE,
        SPECIALTY_STORAGE
    }

    public enum FacilityAmenity {
        CLIMATE_CONTROL,
        ELEVATOR_ACCESS,
        GROUND_FLOOR_ACCESS,
        DRIVE_UP_ACCESS,
        EXTENDED_HOURS,
        MOVING_SUPPLIES,
        TRUCK_RENTAL,
        PACKING_SERVICE,
        CONFERENCE_ROOM,
        BUSINESS_CENTER,
        WIFI_ACCESS,
        LOADING_DOCK,
        FORKLIFT_SERVICE,
        PACKAGING_MATERIALS
    }

    public enum SecurityFeature {
        GATED_ACCESS,
        KEYPAD_ENTRY,
        SECURITY_CAMERAS,
        ON_SITE_MANAGER,
        SECURITY_GUARD,
        ALARM_SYSTEM,
        MOTION_SENSORS,
        FIRE_PROTECTION,
        SPRINKLER_SYSTEM,
        SMOKE_DETECTION,
        PEST_CONTROL,
        PERIMETER_LIGHTING,
        SECURE_FENCING
    }

    public enum PricingModel {
        FIXED_MONTHLY,
        TIERED_PRICING,
        DYNAMIC_PRICING,
        SEASONAL_PRICING,
        PROMOTIONAL_PRICING
    }

    public enum FacilityStatus {
        ACTIVE,
        TEMPORARILY_CLOSED,
        UNDER_MAINTENANCE,
        FULL_CAPACITY,
        PENDING_APPROVAL,
        SUSPENDED,
        PERMANENTLY_CLOSED
    }
}