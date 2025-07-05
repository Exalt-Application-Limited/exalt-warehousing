package com.gogidix.warehousing.onboarding.model;

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
import java.util.Set;

/**
 * Partner Onboarding Request Entity
 * 
 * Represents a warehouse partner's application to join the ecosystem
 */
@Entity
@Table(name = "partner_onboarding_requests")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerOnboardingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String requestId;

    // Basic Company Information
    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String legalBusinessName;

    @Column(nullable = false)
    private String businessRegistrationNumber;

    @Column(nullable = false)
    private String taxIdentificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    @Column(nullable = false)
    private String countryOfIncorporation;

    @Column(nullable = false)
    private LocalDateTime dateOfIncorporation;

    // Contact Information
    @Column(nullable = false)
    private String primaryContactName;

    @Column(nullable = false)
    private String primaryContactEmail;

    @Column(nullable = false)
    private String primaryContactPhone;

    @Column(nullable = false)
    private String businessAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postalCode;

    // Warehouse Capabilities
    @Column(nullable = false)
    private BigDecimal totalStorageCapacity; // in cubic meters

    @Column(nullable = false)
    private BigDecimal availableStorageCapacity;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partner_storage_types", joinColumns = @JoinColumn(name = "request_id"))
    private Set<StorageType> supportedStorageTypes;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partner_service_capabilities", joinColumns = @JoinColumn(name = "request_id"))
    private Set<ServiceCapability> serviceCapabilities;

    @Column(nullable = false)
    private Boolean hasTemperatureControl;

    @Column(nullable = false)
    private Boolean hasSecuritySystems;

    @Column(nullable = false)
    private Boolean hasInventoryManagementSystem;

    // Financial Information
    @Column(nullable = false)
    private BigDecimal proposedPricingPerCubicMeter;

    @Column(nullable = false)
    private BigDecimal minimumOrderValue;

    @Column(nullable = false)
    private String preferredPaymentTerms;

    // KYC and Compliance
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KYCStatus kycStatus;

    @Column
    private String workflowInstanceId;

    @Column
    private String rejectionReason;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private String approvedBy;

    // Documents
    @OneToMany(mappedBy = "onboardingRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OnboardingDocument> documents;

    // KYC Verification Results
    @OneToMany(mappedBy = "onboardingRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<KYCVerification> kycVerifications;

    // Compliance Checks
    @OneToMany(mappedBy = "onboardingRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ComplianceCheck> complianceChecks;

    // Audit Fields
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column(nullable = false)
    private String updatedBy;
}