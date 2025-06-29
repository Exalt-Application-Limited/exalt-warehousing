package com.exalt.warehousing.operations.entity;

import com.exalt.warehousing.operations.enums.MaintenanceType;
import com.exalt.warehousing.operations.entity.base.BaseEntity;
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

/**
 * Maintenance Record Entity
 * 
 * Tracks equipment maintenance history, costs, and service details.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "maintenance_records", indexes = {
    @Index(name = "idx_maintenance_equipment_id", columnList = "equipmentId"),
    @Index(name = "idx_maintenance_date", columnList = "maintenanceDate"),
    @Index(name = "idx_maintenance_type", columnList = "maintenanceType"),
    @Index(name = "idx_maintenance_technician", columnList = "technicianId")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MaintenanceRecord extends BaseEntity {

    // Core Identification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    @NotNull(message = "Equipment is required")
    private Equipment equipment;
    
    @Column(name = "maintenance_reference", length = 50)
    @Size(max = 50, message = "Maintenance reference must not exceed 50 characters")
    private String maintenanceReference;

    // Maintenance Details
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 30)
    @NotNull(message = "Maintenance type is required")
    private MaintenanceType maintenanceType;
    
    @Column(name = "maintenance_date", nullable = false)
    @NotNull(message = "Maintenance date is required")
    private LocalDate maintenanceDate;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes")
    @Min(value = 0, message = "Duration must be non-negative")
    private Integer durationMinutes;

    // Service Information
    @Column(name = "services_performed", columnDefinition = "TEXT")
    private String servicesPerformed;
    
    @Column(name = "parts_replaced", columnDefinition = "TEXT")
    private String partsReplaced;
    
    @Column(name = "technician_id")
    private Long technicianId;
    
    @Column(name = "technician_name", length = 100)
    @Size(max = 100, message = "Technician name must not exceed 100 characters")
    private String technicianName;
    
    @Column(name = "service_provider", length = 100)
    @Size(max = 100, message = "Service provider must not exceed 100 characters")
    private String serviceProvider;
    
    @Column(name = "service_order_number", length = 50)
    @Size(max = 50, message = "Service order number must not exceed 50 characters")
    private String serviceOrderNumber;

    // Results and Recommendations
    @Column(name = "maintenance_result", length = 50)
    @Size(max = 50, message = "Maintenance result must not exceed 50 characters")
    private String maintenanceResult;
    
    @Column(name = "action_taken", columnDefinition = "TEXT")
    private String actionTaken;
    
    @Column(name = "issues_found", columnDefinition = "TEXT")
    private String issuesFound;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @Column(name = "follow_up_required", nullable = false)
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    // Cost Information
    @Column(name = "maintenance_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Maintenance cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid cost format")
    private BigDecimal maintenanceCost;
    
    @Column(name = "parts_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Parts cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid cost format")
    private BigDecimal partsCost;
    
    @Column(name = "labor_cost", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Labor cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid cost format")
    private BigDecimal laborCost;
    
    @Column(name = "invoice_number", length = 50)
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;

    // Documentation and Metrics
    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;
    
    @Column(name = "equipment_runtime_hours_before", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Runtime hours must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid runtime hours format")
    private BigDecimal equipmentRuntimeHoursBefore;
    
    @Column(name = "equipment_condition_before", length = 50)
    @Size(max = 50, message = "Equipment condition must not exceed 50 characters")
    private String equipmentConditionBefore;
    
    @Column(name = "equipment_condition_after", length = 50)
    @Size(max = 50, message = "Equipment condition must not exceed 50 characters")
    private String equipmentConditionAfter;
    
    @Column(name = "documentation_url", length = 500)
    @Size(max = 500, message = "Documentation URL must not exceed 500 characters")
    private String documentationUrl;

    // Business Methods
    public BigDecimal getTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (maintenanceCost != null) {
            total = total.add(maintenanceCost);
        }
        
        if (partsCost != null) {
            total = total.add(partsCost);
        }
        
        if (laborCost != null) {
            total = total.add(laborCost);
        }
        
        return total;
    }

    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    public boolean isPreventiveMaintenance() {
        return maintenanceType == MaintenanceType.PREVENTIVE || 
               maintenanceType == MaintenanceType.SCHEDULED;
    }

    public boolean isCorrectiveMaintenance() {
        return maintenanceType == MaintenanceType.CORRECTIVE || 
               maintenanceType == MaintenanceType.EMERGENCY;
    }

    public boolean isSuccessful() {
        return "COMPLETED".equalsIgnoreCase(maintenanceResult) || 
               "SUCCESS".equalsIgnoreCase(maintenanceResult);
    }

    public boolean isInternalService() {
        return technicianId != null && (serviceProvider == null || serviceProvider.isEmpty());
    }

    public boolean isExternalService() {
        return serviceProvider != null && !serviceProvider.isEmpty();
    }

    public void addMaintenanceNote(String note) {
        if (note == null || note.isEmpty()) {
            return;
        }
        
        if (this.maintenanceNotes == null) {
            this.maintenanceNotes = note;
        } else {
            this.maintenanceNotes += "\n" + note;
        }
    }
}
