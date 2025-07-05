package com.gogidix.warehousing.logistics.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Delivery Schedule Entity
 */
@Entity
@Table(name = "delivery_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverySchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moving_request_id", nullable = false)
    private MovingRequest movingRequest;
    
    @Column(nullable = false)
    private LocalDateTime scheduledPickupTime;
    
    @Column(nullable = false)
    private LocalDateTime scheduledDeliveryTime;
    
    private LocalDateTime actualPickupTime;
    
    private LocalDateTime actualDeliveryTime;
    
    @Column(nullable = false)
    private Long assignedDriverId;
    
    @Column(nullable = false)
    private String assignedDriverName;
    
    @Column(nullable = false)
    private String assignedDriverPhone;
    
    @Column(nullable = false)
    private Long assignedVehicleId;
    
    @Column(nullable = false)
    private String vehicleDetails;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ScheduleStatus {
        SCHEDULED, IN_TRANSIT, DELIVERED, DELAYED, CANCELLED
    }
}