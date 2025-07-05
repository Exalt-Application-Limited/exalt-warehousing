package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer Account Entity
 * 
 * Represents a customer's account in the storage management system.
 * Tracks rental history, payment information, and account status.
 */
@Entity
@Table(name = "customer_accounts")
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String customerId;

    // Personal Information
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private String alternatePhone;

    // Address Information
    @Column(nullable = false)
    private String billingAddress;

    @Column(nullable = false)
    private String billingCity;

    @Column(nullable = false)
    private String billingState;

    @Column(nullable = false)
    private String billingZipCode;

    @Column(nullable = false)
    private String billingCountry;

    // Account Status and Type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSegment customerSegment;

    // Financial Information
    @Column(nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer paymentRating = 5; // 1-5 scale

    @Column
    private LocalDateTime lastPaymentDate;

    // Account Preferences
    @Column(nullable = false)
    private Boolean autoPayEnabled = false;

    @Column(nullable = false)
    private Boolean paperlessEnabled = true;

    @Column(nullable = false)
    private Boolean smsNotificationsEnabled = true;

    @Column(nullable = false)
    private Boolean emailNotificationsEnabled = true;

    @Enumerated(EnumType.STRING)
    private PreferredContactMethod preferredContactMethod;

    @Column
    private String preferredContactTime;

    // Security and Verification
    @Column(nullable = false)
    private Boolean isVerified = false;

    @Column
    private LocalDateTime verificationDate;

    @Column(nullable = false)
    private Boolean kycCompleted = false;

    @Column
    private String identityDocumentType;

    @Column
    private String identityDocumentNumber;

    @Column
    private LocalDateTime identityVerificationDate;

    // Loyalty and Rewards
    @Column(nullable = false)
    private Integer loyaltyPoints = 0;

    @Enumerated(EnumType.STRING)
    private LoyaltyTier loyaltyTier = LoyaltyTier.BRONZE;

    @Column
    private LocalDateTime loyaltyTierExpiryDate;

    @Column(nullable = false)
    private Integer totalRentals = 0;

    @Column(nullable = false)
    private Integer activeRentals = 0;

    // Emergency Contact
    @Column
    private String emergencyContactName;

    @Column
    private String emergencyContactPhone;

    @Column
    private String emergencyContactRelation;

    // Business Account Fields (if applicable)
    @Column
    private String companyName;

    @Column
    private String taxIdNumber;

    @Column
    private String businessLicenseNumber;

    @Column
    private Boolean isBusinessVerified = false;

    // Metadata
    @Column
    private String referralSource;

    @Column
    private String referralCode;

    @Column
    private LocalDateTime firstRentalDate;

    @Column
    private LocalDateTime lastActivityDate;

    @Column(columnDefinition = "TEXT")
    private String accountNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deactivatedAt;

    @Column
    private String deactivationReason;

    // Relationships
    @OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalAgreement> rentalAgreements = new ArrayList<>();

    @OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    @OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccessCredential> accessCredentials = new ArrayList<>();

    @OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerNotification> notifications = new ArrayList<>();

    // Business Methods
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateLoyaltyTier();
    }

    public void deductLoyaltyPoints(int points) {
        this.loyaltyPoints = Math.max(0, this.loyaltyPoints - points);
        updateLoyaltyTier();
    }

    private void updateLoyaltyTier() {
        if (this.loyaltyPoints >= 10000) {
            this.loyaltyTier = LoyaltyTier.PLATINUM;
        } else if (this.loyaltyPoints >= 5000) {
            this.loyaltyTier = LoyaltyTier.GOLD;
        } else if (this.loyaltyPoints >= 2000) {
            this.loyaltyTier = LoyaltyTier.SILVER;
        } else {
            this.loyaltyTier = LoyaltyTier.BRONZE;
        }
    }

    public void incrementRentals() {
        this.totalRentals++;
        this.activeRentals++;
        if (this.firstRentalDate == null) {
            this.firstRentalDate = LocalDateTime.now();
        }
        this.lastActivityDate = LocalDateTime.now();
    }

    public void decrementActiveRentals() {
        this.activeRentals = Math.max(0, this.activeRentals - 1);
        this.lastActivityDate = LocalDateTime.now();
    }

    public void recordPayment(BigDecimal amount) {
        this.totalSpent = this.totalSpent.add(amount);
        this.lastPaymentDate = LocalDateTime.now();
        this.lastActivityDate = LocalDateTime.now();
        
        // Improve payment rating for on-time payments
        if (this.paymentRating < 5) {
            this.paymentRating++;
        }
    }

    public void recordLatePayment() {
        this.paymentRating = Math.max(1, this.paymentRating - 1);
    }

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    public boolean canRent() {
        return this.accountStatus == AccountStatus.ACTIVE &&
               this.isVerified &&
               this.outstandingBalance.compareTo(this.creditLimit) < 0;
    }

    public BigDecimal getAvailableCredit() {
        return this.creditLimit.subtract(this.outstandingBalance);
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public boolean isBusinessAccount() {
        return this.accountType == AccountType.BUSINESS;
    }

    public boolean hasAutoPayEnabled() {
        return this.autoPayEnabled && !this.paymentMethods.isEmpty();
    }

    public void deactivate(String reason) {
        this.accountStatus = AccountStatus.INACTIVE;
        this.deactivatedAt = LocalDateTime.now();
        this.deactivationReason = reason;
    }

    public void reactivate() {
        this.accountStatus = AccountStatus.ACTIVE;
        this.deactivatedAt = null;
        this.deactivationReason = null;
    }

    // Enums
    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        PENDING_VERIFICATION,
        BLACKLISTED,
        CLOSED
    }

    public enum AccountType {
        INDIVIDUAL,
        BUSINESS,
        CORPORATE,
        GOVERNMENT,
        NON_PROFIT
    }

    public enum CustomerSegment {
        RESIDENTIAL,
        COMMERCIAL,
        STUDENT,
        MILITARY,
        SENIOR,
        VIP
    }

    public enum PreferredContactMethod {
        EMAIL,
        SMS,
        PHONE,
        PUSH_NOTIFICATION,
        POSTAL_MAIL
    }

    public enum LoyaltyTier {
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM
    }
}