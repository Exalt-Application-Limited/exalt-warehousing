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
 * Onboarding Document Entity
 * 
 * Represents documents uploaded as part of the onboarding process
 */
@Entity
@Table(name = "onboarding_documents")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_request_id", nullable = false)
    private PartnerOnboardingRequest onboardingRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String fileSize;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private String storageLocation; // S3 key or file path

    @Column
    private String documentHash; // SHA-256 hash for integrity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column
    private String verificationResult;

    @Column
    private String verificationNotes;

    @Column
    private LocalDateTime verifiedAt;

    @Column
    private String verifiedBy;

    @Column
    private LocalDateTime expiryDate; // For documents with expiry dates

    @Column(nullable = false)
    private Boolean isRequired;

    @Column(nullable = false)
    private Boolean isConfidential;

    // Audit Fields
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, updatable = false)
    private String uploadedBy;

    /**
     * Check if document is expired
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    /**
     * Check if document is verified
     */
    public boolean isVerified() {
        return status == DocumentStatus.VERIFIED;
    }

    /**
     * Check if document needs attention
     */
    public boolean needsAttention() {
        return status == DocumentStatus.REJECTED || status == DocumentStatus.REQUIRES_REVIEW || isExpired();
    }
}