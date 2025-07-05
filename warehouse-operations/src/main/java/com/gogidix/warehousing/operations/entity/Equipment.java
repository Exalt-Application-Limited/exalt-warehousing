package com.gogidix.warehousing.operations.entity;

import com.gogidix.warehousing.operations.enums.EquipmentStatus;
import com.gogidix.warehousing.operations.enums.EquipmentType;
import com.gogidix.warehousing.operations.enums.MaintenanceFrequency;
import com.gogidix.warehousing.operations.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Equipment Management Entity
 * 
 * Provides comprehensive tracking and management of warehouse
 * and vendor facility equipment with maintenance scheduling
 * and operational status monitoring.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "equipment", indexes = {
    @Index(name = "idx_equipment_code", columnList = "equipmentCode", unique = true),
    @Index(name = "idx_equipment_warehouse_id", columnList = "warehouseId"),
    @Index(name = "idx_equipment_vendor_id", columnList = "vendorId"),
    @Index(name = "idx_equipment_status", columnList = "status"),
    @Index(name = "idx_equipment_type", columnList = "equipmentType"),
    @Index(name = "idx_equipment_zone_id", columnList = "currentZoneId")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Equipment extends BaseEntity {

    // Core Identification
    @Column(name = "equipment_code", nullable = false, length = 50, unique = true)
    @NotBlank(message = "Equipment code is required")
    @Size(max = 50, message = "Equipment code must not exceed 50 characters")
    private String equipmentCode;
    
    @Column(name = "equipment_name", nullable = false, length = 100)
    @NotBlank(message = "Equipment name is required")
    @Size(max = 100, message = "Equipment name must not exceed 100 characters")
    private String equipmentName;
    
    @Column(name = "serial_number", length = 100)
    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNumber;
    
    @Column(name = "model_number", length = 100)
    @Size(max = 100, message = "Model number must not exceed 100 characters")
    private String modelNumber;
    
    @Column(name = "asset_tag", length = 50)
    @Size(max = 50, message = "Asset tag must not exceed 50 characters")
    private String assetTag;

    // Location Information
    @Column(name = "warehouse_id")
    private Long warehouseId;
    
    @Column(name = "vendor_id")
    private Long vendorId;
    
    @Column(name = "current_zone_id")
    private Long currentZoneId;
    
    @Column(name = "current_location", length = 200)
    @Size(max = 200, message = "Current location must not exceed 200 characters")
    private String currentLocation;

    // Equipment Type and Status
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false, length = 30)
    @NotNull(message = "Equipment type is required")
    private EquipmentType equipmentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Equipment status is required")
    private EquipmentStatus status;
    
    @Column(name = "capacity", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Capacity must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid capacity format")
    private BigDecimal capacity;
    
    @Column(name = "capacity_unit", length = 30)
    @Size(max = 30, message = "Capacity unit must not exceed 30 characters")
    private String capacityUnit;

    // Acquisition and Lifecycle Information
    @Column(name = "manufacturer", length = 100)
    @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
    private String manufacturer;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiry;
    
    @Column(name = "acquisition_cost", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Acquisition cost must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid cost format")
    private BigDecimal acquisitionCost;
    
    @Column(name = "expected_lifespan_years")
    @Min(value = 0, message = "Expected lifespan must be non-negative")
    private Integer expectedLifespanYears;
    
    @Column(name = "current_age_years", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Current age must be non-negative")
    @Digits(integer = 3, fraction = 2, message = "Invalid age format")
    private BigDecimal currentAgeYears;
    
    @Column(name = "retirement_date")
    private LocalDate retirementDate;

    // Operational Data
    @Column(name = "runtime_hours", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Runtime hours must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid runtime hours format")
    private BigDecimal runtimeHours;
    
    @Column(name = "operating_cost_per_hour", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Operating cost must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid cost format")
    private BigDecimal operatingCostPerHour;
    
    @Column(name = "last_used_date")
    private LocalDateTime lastUsedDate;
    
    @Column(name = "current_user_id")
    private Long currentUserId;
    
    @Column(name = "current_user_name", length = 100)
    @Size(max = 100, message = "Current user name must not exceed 100 characters")
    private String currentUserName;
    
    @Column(name = "is_checked_out", nullable = false)
    private Boolean isCheckedOut = false;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    // Maintenance Information
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_frequency", length = 30)
    private MaintenanceFrequency maintenanceFrequency;
    
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;
    
    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;
    
    @Column(name = "maintenance_cost_to_date", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "Maintenance cost must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid cost format")
    private BigDecimal maintenanceCostToDate;
    
    @Column(name = "last_maintenance_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Last maintenance cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid cost format")
    private BigDecimal lastMaintenanceCost;
    
    @Column(name = "is_maintenance_overdue", nullable = false)
    private Boolean isMaintenanceOverdue = false;
    
    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

    // Safety and Compliance
    @Column(name = "requires_certification", nullable = false)
    private Boolean requiresCertification = false;
    
    @Column(name = "certification_expiry")
    private LocalDate certificationExpiry;
    
    @Column(name = "safety_inspection_date")
    private LocalDate safetyInspectionDate;
    
    @Column(name = "is_safety_compliant", nullable = false)
    private Boolean isSafetyCompliant = true;
    
    @Column(name = "safety_notes", columnDefinition = "TEXT")
    private String safetyNotes;
    
    @Column(name = "has_incident_history", nullable = false)
    private Boolean hasIncidentHistory = false;
    
    @Column(name = "incident_count")
    @Min(value = 0, message = "Incident count must be non-negative")
    private Integer incidentCount = 0;

    // Configuration and Features
    @Column(name = "power_source", length = 50)
    @Size(max = 50, message = "Power source must not exceed 50 characters")
    private String powerSource;
    
    @Column(name = "battery_level_percent")
    @DecimalMin(value = "0.0", message = "Battery level must be non-negative")
    @DecimalMax(value = "100.0", message = "Battery level must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid level format")
    private BigDecimal batteryLevelPercent;
    
    @Column(name = "weight_capacity_lbs", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Weight capacity must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid weight capacity format")
    private BigDecimal weightCapacityLbs;
    
    @Column(name = "height_reach_ft", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Height reach must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid height format")
    private BigDecimal heightReachFt;
    
    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    // Relationships
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    // Business Methods
    public void checkOut(Long userId, String userName) {
        this.isCheckedOut = true;
        this.checkOutTime = LocalDateTime.now();
        this.currentUserId = userId;
        this.currentUserName = userName;
        this.status = EquipmentStatus.IN_USE;
        this.lastUsedDate = LocalDateTime.now();
    }

    public void checkIn(String notes) {
        this.isCheckedOut = false;
        this.currentUserId = null;
        this.currentUserName = null;
        this.status = EquipmentStatus.AVAILABLE;
        
        if (notes != null && !notes.isEmpty()) {
            this.maintenanceNotes = (this.maintenanceNotes != null ? this.maintenanceNotes + "\n" : "") 
                                   + "Check-in note: " + notes;
        }
    }

    public void markForMaintenance(String reason) {
        this.status = EquipmentStatus.MAINTENANCE;
        this.maintenanceNotes = (this.maintenanceNotes != null ? this.maintenanceNotes + "\n" : "") 
                               + "Maintenance required: " + reason;
    }

    public void completeMaintenance(LocalDate completionDate, BigDecimal cost, String notes) {
        this.status = EquipmentStatus.AVAILABLE;
        this.lastMaintenanceDate = completionDate;
        this.isMaintenanceOverdue = false;
        
        if (cost != null) {
            this.lastMaintenanceCost = cost;
            this.maintenanceCostToDate = this.maintenanceCostToDate != null ? 
                    this.maintenanceCostToDate.add(cost) : cost;
        }
        
        // Schedule next maintenance based on frequency
        if (maintenanceFrequency != null && completionDate != null) {
            switch (maintenanceFrequency) {
                case DAILY: this.nextMaintenanceDate = completionDate.plusDays(1); break;
                case WEEKLY: this.nextMaintenanceDate = completionDate.plusWeeks(1); break;
                case BIWEEKLY: this.nextMaintenanceDate = completionDate.plusWeeks(2); break;
                case MONTHLY: this.nextMaintenanceDate = completionDate.plusMonths(1); break;
                case QUARTERLY: this.nextMaintenanceDate = completionDate.plusMonths(3); break;
                case SEMIANNUAL: this.nextMaintenanceDate = completionDate.plusMonths(6); break;
                case ANNUAL: this.nextMaintenanceDate = completionDate.plusYears(1); break;
                default: this.nextMaintenanceDate = null;
            }
        }
        
        if (notes != null && !notes.isEmpty()) {
            this.maintenanceNotes = (this.maintenanceNotes != null ? this.maintenanceNotes + "\n" : "") 
                                   + completionDate + " - Maintenance completed: " + notes;
        }
        
        // Create maintenance record
        MaintenanceRecord record = MaintenanceRecord.builder()
            .equipment(this)
            .maintenanceDate(completionDate)
            .maintenanceCost(cost)
            .maintenanceNotes(notes)
            .build();
            
        addMaintenanceRecord(record);
    }

    public void markOutOfService(String reason) {
        this.status = EquipmentStatus.OUT_OF_SERVICE;
        this.maintenanceNotes = (this.maintenanceNotes != null ? this.maintenanceNotes + "\n" : "") 
                               + "Out of service: " + reason;
    }

    public void reportIncident(String description) {
        this.hasIncidentHistory = true;
        this.incidentCount = this.incidentCount != null ? this.incidentCount + 1 : 1;
        this.safetyNotes = (this.safetyNotes != null ? this.safetyNotes + "\n" : "") 
                           + "Incident reported: " + description;
    }

    public void updateLocation(Long zoneId, String location) {
        this.currentZoneId = zoneId;
        this.currentLocation = location;
    }

    public void updateBatteryLevel(Double percentage) {
        if (percentage != null && percentage >= 0 && percentage <= 100) {
            this.batteryLevelPercent = BigDecimal.valueOf(percentage);
            
            // If battery is low, automatically update status
            if (percentage < 20) {
                this.maintenanceNotes = (this.maintenanceNotes != null ? this.maintenanceNotes + "\n" : "") 
                                       + "Low battery alert: " + percentage + "%";
            }
        }
    }

    public void addRuntimeHours(double hours) {
        if (hours > 0) {
            this.runtimeHours = this.runtimeHours != null ? 
                    this.runtimeHours.add(BigDecimal.valueOf(hours)) : 
                    BigDecimal.valueOf(hours);
            this.lastUsedDate = LocalDateTime.now();
        }
    }

    public void retire(String reason) {
        this.status = EquipmentStatus.RETIRED;
        this.retirementDate = LocalDate.now();
        this.maintenanceNotes = (this.maintenanceNotes != null ? this.maintenanceNotes + "\n" : "") 
                               + "Equipment retired: " + reason;
    }

    public boolean needsMaintenance() {
        if (nextMaintenanceDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(nextMaintenanceDate) || isMaintenanceOverdue;
    }

    public boolean isAvailable() {
        return status == EquipmentStatus.AVAILABLE && !isCheckedOut && !isMaintenanceOverdue;
    }

    public boolean isOperational() {
        return status != EquipmentStatus.OUT_OF_SERVICE && 
               status != EquipmentStatus.RETIRED;
    }

    public double getMaintenanceCompliancePercentage() {
        if (lastMaintenanceDate == null || nextMaintenanceDate == null) {
            return 0.0;
        }
        
        if (LocalDate.now().isAfter(nextMaintenanceDate)) {
            return 0.0;
        }
        
        // Calculate days since last maintenance and total maintenance window
        long daysSinceLastMaintenance = java.time.Period.between(lastMaintenanceDate, LocalDate.now()).getDays();
        long totalMaintenanceWindow = java.time.Period.between(lastMaintenanceDate, nextMaintenanceDate).getDays();
        
        if (totalMaintenanceWindow == 0) {
            return 100.0;
        }
        
        // Calculate percentage until next maintenance is due
        double percentage = 100.0 * (1.0 - ((double) daysSinceLastMaintenance / totalMaintenanceWindow));
        return Math.max(0, Math.min(100, percentage));
    }

    public BigDecimal calculateTotalCostOfOwnership() {
        BigDecimal totalCost = acquisitionCost != null ? acquisitionCost : BigDecimal.ZERO;
        
        if (maintenanceCostToDate != null) {
            totalCost = totalCost.add(maintenanceCostToDate);
        }
        
        if (operatingCostPerHour != null && runtimeHours != null) {
            totalCost = totalCost.add(operatingCostPerHour.multiply(runtimeHours));
        }
        
        return totalCost;
    }

    public void calculateCurrentAge() {
        if (purchaseDate != null) {
            long yearsBetween = java.time.Period.between(purchaseDate, LocalDate.now()).getYears();
            long monthsBetween = java.time.Period.between(purchaseDate, LocalDate.now()).getMonths();
            this.currentAgeYears = BigDecimal.valueOf(yearsBetween + (monthsBetween / 12.0));
        }
    }

    public void updateMaintenanceStatus() {
        if (nextMaintenanceDate != null) {
            this.isMaintenanceOverdue = LocalDate.now().isAfter(nextMaintenanceDate);
        }
    }

    public void addMaintenanceRecord(MaintenanceRecord record) {
        maintenanceRecords.add(record);
        record.setEquipment(this);
    }
}
