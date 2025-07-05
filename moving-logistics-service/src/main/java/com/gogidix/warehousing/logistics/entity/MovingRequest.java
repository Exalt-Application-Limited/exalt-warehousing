package com.gogidix.warehousing.logistics.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Moving Request Entity
 */
@Entity
@Table(name = "moving_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovingRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String requestNumber;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String customerEmail;
    
    @Column(nullable = false)
    private String customerPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovingType movingType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;
    
    @Column(nullable = false)
    private LocalDate preferredDate;
    
    private LocalDate alternativeDate;
    
    @Column(nullable = false)
    private String pickupAddress;
    
    @Column(nullable = false)
    private String deliveryAddress;
    
    private BigDecimal estimatedWeight;
    
    private Integer numberOfItems;
    
    @Column(columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedCost;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal finalCost;
    
    private Long assignedDriverId;
    
    private String assignedDriverName;
    
    private Long assignedVehicleId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime scheduledAt;
    
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "movingRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovingItem> items;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (requestNumber == null) {
            requestNumber = generateRequestNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateRequestNumber() {
        return "MV-" + System.currentTimeMillis();
    }
    
    public enum MovingType {
        RESIDENTIAL, COMMERCIAL, STORAGE_TO_HOME, HOME_TO_STORAGE, STORAGE_TO_STORAGE
    }
    
    public enum ServiceType {
        BASIC_MOVING, FULL_SERVICE, PACKING_ONLY, UNPACKING_ONLY, FURNITURE_ASSEMBLY
    }
    
    public enum RequestStatus {
        PENDING, QUOTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}