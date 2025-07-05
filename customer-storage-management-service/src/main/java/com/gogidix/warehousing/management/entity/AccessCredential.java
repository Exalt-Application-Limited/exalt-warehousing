package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Access Credential Entity
 * 
 * Manages customer access credentials for storage units including
 * gate codes, unit access codes, and mobile app credentials.
 */
@Entity
@Table(name = "access_credentials")
@Data
@EqualsAndHashCode(callSuper = false)
public class AccessCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

    @Column(nullable = false)
    private Long rentalAgreementId;

    @Column(nullable = false)
    private Long facilityId;

    @Column(nullable = false)
    private String unitNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CredentialType credentialType;

    @Column(nullable = false)
    private String credentialValue;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column
    private LocalDateTime validUntil;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    // Usage Tracking
    @Column
    private LocalDateTime lastUsedDate;

    @Column(nullable = false)
    private Integer usageCount = 0;

    @Column
    private String lastAccessLocation;

    // Security
    @Column(nullable = false)
    private Integer accessLevel = 1;

    @Column(nullable = false)
    private Boolean requiresTwoFactor = false;

    @Column
    private LocalDateTime lastRotatedDate;

    @Column(nullable = false)
    private Boolean isTemporary = false;

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private LocalDateTime deactivatedAt;

    @Column
    private String deactivationReason;

    // Business Methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return this.isActive &&
               now.isAfter(this.validFrom) &&
               (this.validUntil == null || now.isBefore(this.validUntil));
    }

    public void recordAccess(String location) {
        this.lastUsedDate = LocalDateTime.now();
        this.usageCount++;
        this.lastAccessLocation = location;
    }

    public void deactivate(String reason) {
        this.isActive = false;
        this.deactivatedAt = LocalDateTime.now();
        this.deactivationReason = reason;
    }

    public void rotate(String newValue) {
        this.credentialValue = newValue;
        this.lastRotatedDate = LocalDateTime.now();
    }

    public boolean requiresRotation() {
        if (this.lastRotatedDate == null) {
            return true;
        }
        // Rotate every 90 days for security
        return this.lastRotatedDate.plusDays(90).isBefore(LocalDateTime.now());
    }

    // Enums
    public enum CredentialType {
        GATE_CODE,
        UNIT_ACCESS_CODE,
        MOBILE_APP_PIN,
        KEY_CARD,
        BIOMETRIC,
        QR_CODE,
        BLUETOOTH_KEY,
        TEMPORARY_CODE
    }
}