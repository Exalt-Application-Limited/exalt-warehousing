package com.exalt.warehousing.operations.entity;

import com.exalt.warehousing.operations.enums.LayoutStatus;
import com.exalt.warehousing.operations.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Warehouse Layout Entity
 * 
 * Represents the physical layout and organization of a warehouse or
 * vendor self-storage facility. Provides advanced space management
 * and optimization capabilities.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "warehouse_layouts", indexes = {
    @Index(name = "idx_layout_warehouse_id", columnList = "warehouseId"),
    @Index(name = "idx_layout_vendor_id", columnList = "vendorId"),
    @Index(name = "idx_layout_status", columnList = "status"),
    @Index(name = "idx_layout_type", columnList = "layoutType"),
    @Index(name = "idx_layout_version", columnList = "version")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarehouseLayout extends BaseEntity {

    // Core Identification
    @Column(name = "layout_name", nullable = false, length = 100)
    @NotBlank(message = "Layout name is required")
    @Size(max = 100, message = "Layout name must not exceed 100 characters")
    private String layoutName;
    
    @Column(name = "layout_code", length = 50)
    @Size(max = 50, message = "Layout code must not exceed 50 characters")
    private String layoutCode;
    
    @Column(name = "warehouse_id")
    private Long warehouseId;
    
    @Column(name = "vendor_id")
    private Long vendorId;

    // Layout Metadata
    @Column(name = "layout_type", nullable = false, length = 50)
    @NotBlank(message = "Layout type is required")
    @Size(max = 50, message = "Layout type must not exceed 50 characters")
    private String layoutType;  // e.g., "STANDARD", "CUSTOM", "VENDOR_FACILITY"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Layout status is required")
    private LayoutStatus status;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;
    
    @Column(name = "version", nullable = false)
    @Min(value = 1, message = "Version must be at least 1")
    private Long version = 1L;

    // Physical Dimensions
    @Column(name = "total_area_sqft", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Total area must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid area format")
    private Double totalAreaSqft;
    
    @Column(name = "usable_area_sqft", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Usable area must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid area format")
    private Double usableAreaSqft;
    
    @Column(name = "total_volume_cuft", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Total volume must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid volume format")
    private Double totalVolumeCuft;
    
    @Column(name = "storage_capacity_cuft", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Storage capacity must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid capacity format")
    private Double storageCapacityCuft;

    // Layout Configuration
    @Column(name = "num_zones", nullable = false)
    @Min(value = 0, message = "Number of zones must be non-negative")
    private Integer numZones = 0;
    
    @Column(name = "num_aisles", nullable = false)
    @Min(value = 0, message = "Number of aisles must be non-negative")
    private Integer numAisles = 0;
    
    @Column(name = "num_racks", nullable = false)
    @Min(value = 0, message = "Number of racks must be non-negative")
    private Integer numRacks = 0;
    
    @Column(name = "num_shelves", nullable = false)
    @Min(value = 0, message = "Number of shelves must be non-negative")
    private Integer numShelves = 0;
    
    @Column(name = "num_bins", nullable = false)
    @Min(value = 0, message = "Number of bins must be non-negative")
    private Integer numBins = 0;
    
    @Column(name = "num_dock_doors", nullable = false)
    @Min(value = 0, message = "Number of dock doors must be non-negative")
    private Integer numDockDoors = 0;

    // Operational Data
    @Column(name = "max_staff_capacity")
    @Min(value = 0, message = "Maximum staff capacity must be non-negative")
    private Integer maxStaffCapacity;
    
    @Column(name = "max_daily_throughput")
    @Min(value = 0, message = "Maximum daily throughput must be non-negative")
    private Integer maxDailyThroughput;
    
    @Column(name = "max_order_capacity")
    @Min(value = 0, message = "Maximum order capacity must be non-negative")
    private Integer maxOrderCapacity;
    
    @Column(name = "avg_pick_time_seconds")
    @Min(value = 0, message = "Average pick time must be non-negative")
    private Integer avgPickTimeSeconds;
    
    @Column(name = "avg_travel_distance_ft")
    @DecimalMin(value = "0.0", message = "Average travel distance must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid distance format")
    private Double avgTravelDistanceFt;

    // Layout Data
    @Column(name = "layout_json", columnDefinition = "TEXT")
    private String layoutJson;  // JSON representation of the entire layout
    
    @Column(name = "floor_plan_url", length = 500)
    @Size(max = 500, message = "Floor plan URL must not exceed 500 characters")
    private String floorPlanUrl;
    
    @Column(name = "layout_diagram_url", length = 500)
    @Size(max = 500, message = "Layout diagram URL must not exceed 500 characters")
    private String layoutDiagramUrl;

    // Optimization Data
    @Column(name = "optimization_score")
    @DecimalMin(value = "0.0", message = "Optimization score must be non-negative")
    @DecimalMax(value = "100.0", message = "Optimization score must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid score format")
    private Double optimizationScore;
    
    @Column(name = "space_utilization_pct")
    @DecimalMin(value = "0.0", message = "Space utilization must be non-negative")
    @DecimalMax(value = "100.0", message = "Space utilization must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid percentage format")
    private Double spaceUtilizationPct;
    
    @Column(name = "travel_distance_optimization_pct")
    @DecimalMin(value = "0.0", message = "Travel distance optimization must be non-negative")
    @DecimalMax(value = "100.0", message = "Travel distance optimization must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid percentage format")
    private Double travelDistanceOptimizationPct;
    
    @Column(name = "picking_efficiency_score")
    @DecimalMin(value = "0.0", message = "Picking efficiency score must be non-negative")
    @DecimalMax(value = "100.0", message = "Picking efficiency score must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid score format")
    private Double pickingEfficiencyScore;

    // Configuration and Settings
    @Column(name = "has_mezzanine", nullable = false)
    private Boolean hasMezzanine = false;
    
    @Column(name = "has_automation", nullable = false)
    private Boolean hasAutomation = false;
    
    @Column(name = "has_climate_control", nullable = false)
    private Boolean hasClimateControl = false;
    
    @Column(name = "has_hazmat_area", nullable = false)
    private Boolean hasHazmatArea = false;
    
    @Column(name = "is_multi_level", nullable = false)
    private Boolean isMultiLevel = false;

    // Tracking and Metadata
    @Column(name = "created_by_user_id")
    private Long createdByUserId;
    
    @Column(name = "last_modified_by_user_id")
    private Long lastModifiedByUserId;
    
    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;
    
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    @Column(name = "last_optimized_at")
    private LocalDateTime lastOptimizedAt;

    // Custom Attributes and Notes
    @ElementCollection
    @CollectionTable(
        name = "warehouse_layout_attributes",
        joinColumns = @JoinColumn(name = "layout_id")
    )
    @MapKeyColumn(name = "attribute_name", length = 100)
    @Column(name = "attribute_value", length = 500)
    private Map<String, String> customAttributes;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Relationships
    @OneToMany(mappedBy = "warehouseLayout", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WarehouseZone> zones = new ArrayList<>();

    // Business Methods
    public WarehouseZone addZone(WarehouseZone zone) {
        zones.add(zone);
        // TODO: Uncomment when Lombok processing is fixed
        // zone.setWarehouseLayout(this);
        numZones = zones.size();
        return zone;
    }

    public void removeZone(WarehouseZone zone) {
        zones.remove(zone);
        // TODO: Uncomment when Lombok processing is fixed
        // zone.setWarehouseLayout(null);
        numZones = zones.size();
    }

    public void computeStorageMetrics() {
        // Reset counters
        this.numRacks = 0;
        this.numShelves = 0;
        this.numBins = 0;
        
        // For now, just use the zones list size for basic functionality
        // TODO: Implement proper calculation when Lombok getters are working
    }

    public void activate() {
        this.isActive = true;
        this.status = LayoutStatus.ACTIVE;
        this.effectiveDate = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.status = LayoutStatus.INACTIVE;
        this.expirationDate = LocalDateTime.now();
    }

    public boolean isWarehouseLayout() {
        return warehouseId != null;
    }

    public boolean isVendorFacilityLayout() {
        return vendorId != null;
    }

    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               (effectiveDate == null || now.isAfter(effectiveDate)) && 
               (expirationDate == null || now.isBefore(expirationDate));
    }

    public double getSpaceUtilizationPercent() {
        if (totalAreaSqft == null || totalAreaSqft == 0) {
            return 0.0;
        }
        return (usableAreaSqft != null ? usableAreaSqft : 0.0) / totalAreaSqft * 100.0;
    }
}
