package com.exalt.warehousing.onboarding.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Compliance Check Entity
 * 
 * Represents regulatory and compliance checks performed during onboarding
 */
@Entity
@Table(name = "compliance_checks")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String checkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_request_id", nullable = false)
    private PartnerOnboardingRequest onboardingRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceType complianceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(nullable = false)
    private String jurisdiction; // Country/Region where compliance is checked

    @Column
    private String regulatoryBody; // Name of regulatory authority

    @Column
    private String licenseNumber; // License or permit number if applicable

    @Column
    private LocalDateTime licenseExpiryDate;

    @Column(columnDefinition = "TEXT")
    private String checkDetails; // Details of what was checked

    @Column(columnDefinition = "TEXT")
    private String findings; // Findings from the compliance check

    @Column(columnDefinition = "TEXT")
    private String requirements; // Additional requirements identified

    @Column(columnDefinition = "TEXT")
    private String remedialActions; // Actions needed to achieve compliance

    @Column
    private String riskRating; // CRITICAL, HIGH, MEDIUM, LOW

    @Column
    private Boolean isCompliant;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime nextReviewDate; // When next review is due

    @Column
    private String performedBy; // Who performed the check

    @Column
    private String approvedBy; // Who approved the check

    @Column
    private LocalDateTime approvedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Audit Fields
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, updatable = false)
    private String initiatedBy;

    /**
     * Check if compliance check is complete
     */
    public boolean isComplete() {
        return status == VerificationStatus.COMPLETED || status == VerificationStatus.FAILED;
    }

    /**
     * Check if compliance check passed
     */
    public boolean isPassed() {
        return status == VerificationStatus.COMPLETED && 
               (isCompliant == null || isCompliant);
    }

    /**
     * Check if license is expired
     */
    public boolean isLicenseExpired() {
        return licenseExpiryDate != null && licenseExpiryDate.isBefore(LocalDateTime.now());
    }

    /**
     * Check if review is due
     */
    public boolean isReviewDue() {
        return nextReviewDate != null && nextReviewDate.isBefore(LocalDateTime.now());
    }

    /**
     * Check if compliance check is critical
     */
    public boolean isCritical() {
        return "CRITICAL".equalsIgnoreCase(riskRating) || 
               complianceType.isMandatory();
    }
}