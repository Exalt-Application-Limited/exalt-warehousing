package com.exalt.warehousing.onboarding.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * KYC Verification Entity
 * 
 * Represents individual KYC verification checks performed during onboarding
 */
@Entity
@Table(name = "kyc_verifications")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KYCVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String verificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_request_id", nullable = false)
    private PartnerOnboardingRequest onboardingRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KYCVerificationType verificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column
    private String externalVerificationId; // ID from external KYC provider

    @Column
    private String providerName; // Name of KYC service provider

    @Column(columnDefinition = "TEXT")
    private String verificationData; // JSON data from verification

    @Column
    private BigDecimal confidenceScore; // Confidence score from 0.0 to 1.0

    @Column
    private String riskLevel; // LOW, MEDIUM, HIGH

    @Column(columnDefinition = "TEXT")
    private String findings; // Detailed findings

    @Column(columnDefinition = "TEXT")
    private String recommendations; // Recommendations for next steps

    @Column
    private String failureReason; // Reason for failure if applicable

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime expiryDate; // When verification expires

    @Column
    private Boolean requiresManualReview;

    @Column
    private String reviewedBy; // Who performed manual review

    @Column
    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "TEXT")
    private String reviewNotes;

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
     * Check if verification is complete
     */
    public boolean isComplete() {
        return status == VerificationStatus.COMPLETED || status == VerificationStatus.FAILED;
    }

    /**
     * Check if verification passed
     */
    public boolean isPassed() {
        return status == VerificationStatus.COMPLETED && 
               (confidenceScore == null || confidenceScore.compareTo(BigDecimal.valueOf(0.7)) >= 0);
    }

    /**
     * Check if verification is expired
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    /**
     * Check if verification needs manual review
     */
    public boolean needsManualReview() {
        return requiresManualReview != null && requiresManualReview;
    }
}