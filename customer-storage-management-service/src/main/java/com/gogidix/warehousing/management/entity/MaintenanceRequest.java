package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Maintenance Request Entity
 * 
 * Tracks customer-reported maintenance issues for their storage units.
 */
@Entity
@Table(name = "maintenance_requests")
@Data
@EqualsAndHashCode(callSuper = false)
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_agreement_id", nullable = false)
    private RentalAgreement rentalAgreement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private String issueDescription;

    @Column(columnDefinition = "TEXT")
    private String detailedNotes;

    @Column
    private String reportedBy;

    @Column
    private LocalDateTime scheduledDate;

    @Column
    private LocalDateTime completedDate;

    @Column
    private String completedBy;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Enums
    public enum RequestType {
        LOCK_ISSUE,
        DOOR_DAMAGE,
        LIGHTING,
        WATER_DAMAGE,
        PEST_CONTROL,
        CLIMATE_CONTROL,
        ELECTRICAL,
        CLEANING,
        SECURITY,
        OTHER
    }

    public enum RequestStatus {
        OPEN,
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        ON_HOLD
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}