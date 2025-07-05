package com.gogidix.warehousing.operations.entity;

import com.gogidix.warehousing.operations.enums.ZoneType;
import com.gogidix.warehousing.operations.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Warehouse Zone Entity
 * 
 * Represents a dedicated area within a warehouse or vendor facility
 * with specific storage and operational characteristics.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "warehouse_zones", indexes = {
    @Index(name = "idx_zone_layout_id", columnList = "warehouseLayoutId"),
    @Index(name = "idx_zone_code", columnList = "zoneCode"),
    @Index(name = "idx_zone_type", columnList = "zoneType")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarehouseZone extends BaseEntity {

    // Core Identification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_layout_id", nullable = false)
    @NotNull(message = "Warehouse layout is required")
    private WarehouseLayout warehouseLayout;
    
    @Column(name = "zone_name", nullable = false, length = 100)
    @NotBlank(message = "Zone name is required")
    @Size(max = 100, message = "Zone name must not exceed 100 characters")
    private String zoneName;
    
    @Column(name = "zone_code", nullable = false, length = 20)
    @NotBlank(message = "Zone code is required")
    @Size(max = 20, message = "Zone code must not exceed 20 characters")
    private String zoneCode;

    // Zone Properties
    @Enumerated(EnumType.STRING)
    @Column(name = "zone_type", nullable = false, length = 30)
    @NotNull(message = "Zone type is required")
    private ZoneType zoneType;
    
    @Column(name = "floor_level", nullable = false)
    @Min(value = 0, message = "Floor level must be non-negative")
    private Integer floorLevel = 0;
    
    @Column(name = "priority", nullable = false)
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 10, message = "Priority must not exceed 10")
    private Integer priority = 5;

    // Physical Dimensions
    @Column(name = "area_sqft", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Area must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid area format")
    private Double areaSqft;
    
    @Column(name = "length_ft", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Length must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid length format")
    private Double lengthFt;
    
    @Column(name = "width_ft", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Width must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid width format")
    private Double widthFt;
    
    @Column(name = "height_ft", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Height must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid height format")
    private Double heightFt;
    
    @Column(name = "volume_cuft", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Volume must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid volume format")
    private Double volumeCuft;

    // Storage Configuration
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
    
    @Column(name = "max_storage_capacity_cuft", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Max storage capacity must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid capacity format")
    private Double maxStorageCapacityCuft;

    // Special Characteristics
    @Column(name = "temperature_min_f")
    private Integer temperatureMinF;
    
    @Column(name = "temperature_max_f")
    private Integer temperatureMaxF;
    
    @Column(name = "humidity_min_pct")
    @DecimalMin(value = "0.0", message = "Minimum humidity must be non-negative")
    @DecimalMax(value = "100.0", message = "Minimum humidity must not exceed 100")
    private Double humidityMinPct;
    
    @Column(name = "humidity_max_pct")
    @DecimalMin(value = "0.0", message = "Maximum humidity must be non-negative")
    @DecimalMax(value = "100.0", message = "Maximum humidity must not exceed 100")
    private Double humidityMaxPct;

    // Configuration Flags
    @Column(name = "is_climate_controlled", nullable = false)
    private Boolean isClimateControlled = false;
    
    @Column(name = "is_hazmat", nullable = false)
    private Boolean isHazmat = false;
    
    @Column(name = "is_high_security", nullable = false)
    private Boolean isHighSecurity = false;
    
    @Column(name = "is_high_value", nullable = false)
    private Boolean isHighValue = false;
    
    @Column(name = "is_refrigerated", nullable = false)
    private Boolean isRefrigerated = false;
    
    @Column(name = "is_frozen", nullable = false)
    private Boolean isFrozen = false;
    
    @Column(name = "requires_special_access", nullable = false)
    private Boolean requiresSpecialAccess = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Operational Data
    @Column(name = "max_workers_allowed")
    @Min(value = 0, message = "Maximum workers must be non-negative")
    private Integer maxWorkersAllowed;
    
    @Column(name = "optimal_temperature_f")
    private Integer optimalTemperatureF;
    
    @Column(name = "optimal_humidity_pct")
    @DecimalMin(value = "0.0", message = "Optimal humidity must be non-negative")
    @DecimalMax(value = "100.0", message = "Optimal humidity must not exceed 100")
    private Double optimalHumidityPct;

    // Location Data
    @Column(name = "coordinate_x")
    private Integer coordinateX;
    
    @Column(name = "coordinate_y")
    private Integer coordinateY;
    
    @Column(name = "map_position_json", columnDefinition = "TEXT")
    private String mapPositionJson; // JSON data for visual representation
    
    @Column(name = "direction_notes", columnDefinition = "TEXT")
    private String directionNotes;

    // Relationships
    // TODO: Add WarehouseAisle relationship when implemented
    // @OneToMany(mappedBy = "warehouseZone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<WarehouseAisle> aisles = new ArrayList<>();

    // Business Methods
    // TODO: Implement aisle management when WarehouseAisle is created
    /*
    public WarehouseAisle addAisle(WarehouseAisle aisle) {
        aisles.add(aisle);
        aisle.setWarehouseZone(this);
        numAisles = aisles.size();
        return aisle;
    }

    public void removeAisle(WarehouseAisle aisle) {
        aisles.remove(aisle);
        aisle.setWarehouseZone(null);
        numAisles = aisles.size();
    }
    */

    public void updateStorageCounts() {
        // TODO: Implement when WarehouseAisle is available
        /*
        int totalRacks = 0;
        int totalShelves = 0;
        int totalBins = 0;
        
        for (WarehouseAisle aisle : aisles) {
            totalRacks += aisle.getNumRacks();
            totalShelves += aisle.getNumShelves();
            totalBins += aisle.getNumBins();
        }
        
        this.numRacks = totalRacks;
        this.numShelves = totalShelves;
        this.numBins = totalBins;
        */
    }

    public void calculateVolume() {
        if (lengthFt != null && widthFt != null && heightFt != null) {
            this.volumeCuft = lengthFt * widthFt * heightFt;
        }
        
        if (lengthFt != null && widthFt != null) {
            this.areaSqft = lengthFt * widthFt;
        }
    }

    public boolean hasClimateRequirements() {
        return isClimateControlled || isRefrigerated || isFrozen || 
               temperatureMinF != null || temperatureMaxF != null ||
               humidityMinPct != null || humidityMaxPct != null;
    }

    public boolean hasSpecialRequirements() {
        return isHazmat || isHighSecurity || isHighValue || requiresSpecialAccess;
    }

    public boolean isPickingZone() {
        return zoneType == ZoneType.PICKING || zoneType == ZoneType.FORWARD_PICKING;
    }

    public boolean isStorageZone() {
        return zoneType == ZoneType.BULK_STORAGE || zoneType == ZoneType.RESERVE_STORAGE;
    }
}
