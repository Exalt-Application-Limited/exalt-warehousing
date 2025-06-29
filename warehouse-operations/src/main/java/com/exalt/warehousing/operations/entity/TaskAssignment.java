package com.exalt.warehousing.operations.entity;

import com.exalt.warehousing.operations.enums.TaskPriority;
import com.exalt.warehousing.operations.enums.TaskStatus;
import com.exalt.warehousing.operations.enums.TaskType;
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

/**
 * Task Assignment Entity
 * 
 * Represents individual operational tasks assigned to staff members
 * within warehouse and vendor facilities.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Entity
@Table(name = "task_assignments", indexes = {
    @Index(name = "idx_task_assignment_id", columnList = "staffAssignmentId"),
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_type", columnList = "taskType"),
    @Index(name = "idx_task_priority", columnList = "priority"),
    @Index(name = "idx_task_due_date", columnList = "dueDateTime"),
    @Index(name = "idx_task_zone_id", columnList = "zoneId"),
    @Index(name = "idx_task_order_id", columnList = "orderId")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskAssignment extends BaseEntity {

    // Core Identification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_assignment_id")
    private StaffAssignment staffAssignment;
    
    @Column(name = "task_code", nullable = false, length = 50)
    @NotBlank(message = "Task code is required")
    @Size(max = 50, message = "Task code must not exceed 50 characters")
    private String taskCode;
    
    @Column(name = "task_name", nullable = false, length = 200)
    @NotBlank(message = "Task name is required")
    @Size(max = 200, message = "Task name must not exceed 200 characters")
    private String taskName;

    // Task Properties
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 30)
    @NotNull(message = "Task type is required")
    private TaskType taskType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Task status is required")
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @NotNull(message = "Task priority is required")
    private TaskPriority priority;

    // Task Details
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "expected_duration_minutes")
    @Min(value = 0, message = "Expected duration must be non-negative")
    private Integer expectedDurationMinutes;
    
    @Column(name = "actual_duration_minutes")
    @Min(value = 0, message = "Actual duration must be non-negative")
    private Integer actualDurationMinutes;
    
    @Column(name = "due_date_time")
    private LocalDateTime dueDateTime;
    
    @Column(name = "scheduled_start_time")
    private LocalDateTime scheduledStartTime;
    
    @Column(name = "scheduled_end_time")
    private LocalDateTime scheduledEndTime;

    // Task Execution
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "completion_percentage")
    @DecimalMin(value = "0.0", message = "Completion percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Completion percentage must not exceed 100")
    private Double completionPercentage = 0.0;
    
    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    // Reference Information
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "order_number", length = 50)
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderNumber;
    
    @Column(name = "zone_id")
    private Long zoneId;
    
    @Column(name = "zone_code", length = 20)
    @Size(max = 20, message = "Zone code must not exceed 20 characters")
    private String zoneCode;
    
    @Column(name = "equipment_id")
    private Long equipmentId;
    
    @Column(name = "equipment_code", length = 30)
    @Size(max = 30, message = "Equipment code must not exceed 30 characters")
    private String equipmentCode;
    
    @Column(name = "source_location", length = 200)
    @Size(max = 200, message = "Source location must not exceed 200 characters")
    private String sourceLocation;
    
    @Column(name = "destination_location", length = 200)
    @Size(max = 200, message = "Destination location must not exceed 200 characters")
    private String destinationLocation;

    // Quantitative Data
    @Column(name = "quantity")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
    
    @Column(name = "completed_quantity")
    @Min(value = 0, message = "Completed quantity must be non-negative")
    private Integer completedQuantity = 0;
    
    @Column(name = "error_count")
    @Min(value = 0, message = "Error count must be non-negative")
    private Integer errorCount = 0;

    // Task Configuration
    @Column(name = "requires_verification", nullable = false)
    private Boolean requiresVerification = false;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(name = "verified_by")
    private Long verifiedBy;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "requires_equipment", nullable = false)
    private Boolean requiresEquipment = false;
    
    @Column(name = "is_sequential", nullable = false)
    private Boolean isSequential = false;
    
    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    // Prerequisite Dependencies
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_dependencies",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_task_id")
    )
    private List<TaskAssignment> prerequisiteTasks = new ArrayList<>();

    // Business Methods
    public void startTask() {
        this.status = TaskStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }

    public void completeTask(String notes) {
        this.status = TaskStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
        this.completionNotes = notes;
        this.completionPercentage = 100.0;
        this.completedQuantity = this.quantity;
        
        if (actualStartTime != null && actualEndTime != null) {
            this.actualDurationMinutes = (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
    }

    public void pauseTask(String reason) {
        this.status = TaskStatus.PAUSED;
        this.completionNotes = (this.completionNotes != null ? this.completionNotes + "\n" : "") 
                            + "Paused: " + reason;
    }

    public void resumeTask() {
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void cancelTask(String reason) {
        this.status = TaskStatus.CANCELLED;
        this.completionNotes = (this.completionNotes != null ? this.completionNotes + "\n" : "") 
                            + "Cancelled: " + reason;
    }

    public void updateProgress(double percentage, int completedQty) {
        if (percentage >= 0 && percentage <= 100) {
            this.completionPercentage = percentage;
        }
        
        if (completedQty >= 0 && (quantity == null || completedQty <= quantity)) {
            this.completedQuantity = completedQty;
        }
        
        // Automatically set status to COMPLETED if progress reaches 100%
        if (this.completionPercentage == 100.0) {
            this.status = TaskStatus.COMPLETED;
            this.actualEndTime = LocalDateTime.now();
            
            if (actualStartTime != null && actualEndTime != null) {
                this.actualDurationMinutes = (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
            }
        }
    }

    public void verifyTask(Long verifierId) {
        this.isVerified = true;
        this.verifiedBy = verifierId;
        this.verifiedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == TaskStatus.PENDING || status == TaskStatus.IN_PROGRESS || status == TaskStatus.PAUSED;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == TaskStatus.CANCELLED;
    }

    public boolean isPickingTask() {
        return taskType == TaskType.PICKING;
    }

    public boolean isPackingTask() {
        return taskType == TaskType.PACKING;
    }

    public boolean isOverdue() {
        return status != TaskStatus.COMPLETED &&
               status != TaskStatus.CANCELLED &&
               dueDateTime != null && 
               dueDateTime.isBefore(LocalDateTime.now());
    }

    public boolean isHighPriority() {
        return priority == TaskPriority.CRITICAL || priority == TaskPriority.HIGH;
    }

    public boolean arePrerequisitesComplete() {
        return prerequisiteTasks.stream().allMatch(TaskAssignment::isCompleted);
    }

    public boolean isEligibleToStart() {
        return status == TaskStatus.PENDING && arePrerequisitesComplete();
    }

    public double getEfficiency() {
        if (expectedDurationMinutes != null && actualDurationMinutes != null && expectedDurationMinutes > 0) {
            return (double) expectedDurationMinutes / actualDurationMinutes * 100.0;
        }
        return 0.0;
    }

    public double getErrorRate() {
        if (completedQuantity != null && completedQuantity > 0 && errorCount != null) {
            return (double) errorCount / completedQuantity * 100.0;
        }
        return 0.0;
    }

    public void addPrerequisiteTask(TaskAssignment task) {
        prerequisiteTasks.add(task);
    }

    public void removePrerequisiteTask(TaskAssignment task) {
        prerequisiteTasks.remove(task);
    }
}
