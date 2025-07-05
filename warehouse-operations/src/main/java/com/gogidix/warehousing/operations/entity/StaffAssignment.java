package com.gogidix.warehousing.operations.entity;

import com.gogidix.warehousing.operations.enums.AssignmentStatus;
import com.gogidix.warehousing.operations.enums.AssignmentType;
import com.gogidix.warehousing.operations.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Staff Assignment Entity
 * 
 * Represents staff scheduling, assignment and task allocation
 * for warehouse operations and vendor facility management.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "staff_assignments", indexes = {
    @Index(name = "idx_assignment_staff_id", columnList = "staffMemberId"),
    @Index(name = "idx_assignment_warehouse_id", columnList = "warehouseId"),
    @Index(name = "idx_assignment_vendor_id", columnList = "vendorId"),
    @Index(name = "idx_assignment_start_date", columnList = "assignmentStartTime"),
    @Index(name = "idx_assignment_end_date", columnList = "assignmentEndTime"),
    @Index(name = "idx_assignment_status", columnList = "status"),
    @Index(name = "idx_assignment_date_status", columnList = "assignmentDate, status")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StaffAssignment extends BaseEntity {

    // Core Identification
    @Column(name = "staff_member_id", nullable = false)
    @NotNull(message = "Staff member ID is required")
    private Long staffMemberId;
    
    @Column(name = "staff_name", nullable = false, length = 100)
    @NotBlank(message = "Staff name is required")
    @Size(max = 100, message = "Staff name must not exceed 100 characters")
    private String staffName;
    
    @Column(name = "warehouse_id")
    private Long warehouseId;
    
    @Column(name = "vendor_id")
    private Long vendorId;

    // Assignment Type and Status
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false, length = 30)
    @NotNull(message = "Assignment type is required")
    private AssignmentType assignmentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Assignment status is required")
    private AssignmentStatus status;
    
    @Column(name = "shift_code", length = 20)
    @Size(max = 20, message = "Shift code must not exceed 20 characters")
    private String shiftCode;

    // Assignment Location
    @Column(name = "zone_id")
    private Long zoneId;
    
    @Column(name = "zone_name", length = 100)
    @Size(max = 100, message = "Zone name must not exceed 100 characters")
    private String zoneName;
    
    @Column(name = "location_detail", length = 200)
    @Size(max = 200, message = "Location detail must not exceed 200 characters")
    private String locationDetail;

    // Time Information
    @Column(name = "assignment_date", nullable = false)
    @NotNull(message = "Assignment date is required")
    private LocalDateTime assignmentDate;
    
    @Column(name = "assignment_start_time", nullable = false)
    @NotNull(message = "Assignment start time is required")
    private LocalDateTime assignmentStartTime;
    
    @Column(name = "assignment_end_time")
    private LocalDateTime assignmentEndTime;
    
    @Column(name = "break_start_time")
    private LocalTime breakStartTime;
    
    @Column(name = "break_duration_minutes")
    @Min(value = 0, message = "Break duration must be non-negative")
    private Integer breakDurationMinutes;
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    // Assignment Details
    @Column(name = "expected_duration_minutes")
    @Min(value = 0, message = "Expected duration must be non-negative")
    private Integer expectedDurationMinutes;
    
    @Column(name = "actual_duration_minutes")
    @Min(value = 0, message = "Actual duration must be non-negative")
    private Integer actualDurationMinutes;
    
    @Column(name = "priority", nullable = false)
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 10, message = "Priority must not exceed 10")
    private Integer priority = 5;

    // Performance Metrics
    @Column(name = "efficiency_percent")
    @DecimalMin(value = "0.0", message = "Efficiency must be non-negative")
    @DecimalMax(value = "200.0", message = "Efficiency must not exceed 200")
    @Digits(integer = 3, fraction = 2, message = "Invalid efficiency format")
    private Double efficiencyPercent;
    
    @Column(name = "quality_score")
    @DecimalMin(value = "0.0", message = "Quality score must be non-negative")
    @DecimalMax(value = "100.0", message = "Quality score must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid score format")
    private Double qualityScore;
    
    @Column(name = "productivity")
    @DecimalMin(value = "0.0", message = "Productivity must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid productivity format")
    private Double productivity;
    
    @Column(name = "items_processed")
    @Min(value = 0, message = "Items processed must be non-negative")
    private Integer itemsProcessed;
    
    @Column(name = "orders_completed")
    @Min(value = 0, message = "Orders completed must be non-negative")
    private Integer ordersCompleted;
    
    @Column(name = "error_count")
    @Min(value = 0, message = "Error count must be non-negative")
    private Integer errorCount = 0;

    // Assignment Configuration
    @Column(name = "requires_certification", nullable = false)
    private Boolean requiresCertification = false;
    
    @Column(name = "is_overtime", nullable = false)
    private Boolean isOvertime = false;
    
    @Column(name = "is_training", nullable = false)
    private Boolean isTraining = false;
    
    @Column(name = "has_special_instructions", nullable = false)
    private Boolean hasSpecialInstructions = false;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // Relationships
    @OneToMany(mappedBy = "staffAssignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskAssignment> assignedTasks = new ArrayList<>();

    // Business Methods
    public void startAssignment() {
        this.status = AssignmentStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }

    public void completeAssignment() {
        this.status = AssignmentStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
        
        if (actualStartTime != null && actualEndTime != null) {
            this.actualDurationMinutes = (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
    }

    public void cancelAssignment(String reason) {
        this.status = AssignmentStatus.CANCELLED;
        this.specialInstructions = (this.specialInstructions != null ? this.specialInstructions + "\n" : "") 
                                  + "Cancelled: " + reason;
    }

    public boolean isActive() {
        return status == AssignmentStatus.SCHEDULED || status == AssignmentStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == AssignmentStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == AssignmentStatus.CANCELLED;
    }

    public boolean isWarehouseAssignment() {
        return warehouseId != null;
    }

    public boolean isVendorAssignment() {
        return vendorId != null;
    }

    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == AssignmentStatus.IN_PROGRESS &&
               actualStartTime != null && actualStartTime.isBefore(now) &&
               (actualEndTime == null || actualEndTime.isAfter(now));
    }

    public boolean isScheduledForToday() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime tomorrow = today.plusDays(1);
        return status == AssignmentStatus.SCHEDULED &&
               assignmentStartTime != null &&
               assignmentStartTime.isAfter(today) &&
               assignmentStartTime.isBefore(tomorrow);
    }

    public boolean isOverdue() {
        return status == AssignmentStatus.SCHEDULED &&
               assignmentStartTime != null && assignmentStartTime.isBefore(LocalDateTime.now());
    }

    public void addTask(TaskAssignment task) {
        assignedTasks.add(task);
        // TODO: Uncomment when Lombok processing is fixed
        // task.setStaffAssignment(this);
    }

    public void removeTask(TaskAssignment task) {
        assignedTasks.remove(task);
        // TODO: Uncomment when Lombok processing is fixed
        // task.setStaffAssignment(null);
    }

    public void calculateMetrics() {
        // Calculate efficiency based on expected vs actual duration
        if (expectedDurationMinutes != null && actualDurationMinutes != null && expectedDurationMinutes > 0) {
            this.efficiencyPercent = (double) expectedDurationMinutes / actualDurationMinutes * 100.0;
        }
        
        // Calculate quality score based on errors and items processed
        if (itemsProcessed != null && itemsProcessed > 0) {
            double errorRate = errorCount != null ? (double) errorCount / itemsProcessed : 0.0;
            this.qualityScore = 100.0 * (1.0 - errorRate);
        }
        
        // Calculate productivity based on items processed per hour
        if (actualDurationMinutes != null && actualDurationMinutes > 0 && itemsProcessed != null) {
            this.productivity = (double) itemsProcessed / (actualDurationMinutes / 60.0);
        }
    }
}
