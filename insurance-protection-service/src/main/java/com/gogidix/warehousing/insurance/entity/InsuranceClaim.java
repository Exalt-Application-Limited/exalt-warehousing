package com.gogidix.warehousing.insurance.entity;

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
 * Insurance Claim Entity
 * 
 * Represents insurance claims filed by customers for covered incidents.
 */
@Entity
@Table(name = "insurance_claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceClaim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String claimNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_policy_id", nullable = false)
    private InsurancePolicy insurancePolicy;
    
    @Column(nullable = false)
    private String incidentType;
    
    @Column(nullable = false)
    private LocalDate incidentDate;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String incidentDescription;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal claimedAmount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal assessedAmount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal paidAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.SUBMITTED;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    private LocalDateTime assessedAt;
    
    private LocalDateTime approvedAt;
    
    private LocalDateTime paidAt;
    
    private LocalDateTime rejectedAt;
    
    private String rejectionReason;
    
    private Long assessorId;
    
    private String assessorName;
    
    private Long approverId;
    
    private String approverName;
    
    @Column(columnDefinition = "TEXT")
    private String assessorNotes;
    
    @Column(columnDefinition = "TEXT")
    private String approverNotes;
    
    @OneToMany(mappedBy = "insuranceClaim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClaimDocument> documents;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        submittedAt = LocalDateTime.now();
        if (claimNumber == null) {
            claimNumber = generateClaimNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateClaimNumber() {
        return "CLM-" + System.currentTimeMillis();
    }
    
    public enum ClaimStatus {
        SUBMITTED, UNDER_REVIEW, UNDER_INVESTIGATION, ASSESSED, APPROVED, REJECTED, PAID, CLOSED
    }
}