package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rental Agreement Entity
 * 
 * Represents a legal rental agreement between a customer and the storage facility.
 * Manages contract terms, pricing, and lifecycle.
 */
@Entity
@Table(name = "rental_agreements")
@Data
@EqualsAndHashCode(callSuper = false)
public class RentalAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String agreementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

    // Unit and Facility Information
    @Column(nullable = false)
    private Long facilityId;

    @Column(nullable = false)
    private String facilityName;

    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private String unitNumber;

    @Column(nullable = false)
    private String unitSize;

    // Agreement Terms
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementType agreementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementStatus status;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false)
    private Boolean isMonthToMonth = false;

    // Pricing Information
    @Column(nullable = false)
    private BigDecimal monthlyRate;

    @Column(nullable = false)
    private BigDecimal securityDeposit;

    @Column
    private BigDecimal adminFee;

    @Column
    private BigDecimal insuranceRate;

    @Column(nullable = false)
    private BigDecimal totalMonthlyCharge;

    @Column
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column
    private String promotionCode;

    // Payment Schedule
    @Column(nullable = false)
    private Integer billingDayOfMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency paymentFrequency;

    @Column
    private LocalDate nextBillingDate;

    @Column
    private LocalDate lastBillingDate;

    @Column(nullable = false)
    private BigDecimal totalPaidToDate = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    // Auto-renewal Settings
    @Column(nullable = false)
    private Boolean autoRenewEnabled = true;

    @Column
    private LocalDate autoRenewNotificationDate;

    @Column
    private Integer renewalTermMonths;

    @Column
    private BigDecimal renewalRate;

    // Insurance and Protection
    @Column(nullable = false)
    private Boolean insuranceRequired = false;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @Column
    private BigDecimal insuranceCoverage;

    @Column
    private String insurancePolicyNumber;

    // Access Information
    @Column
    private String accessCode;

    @Column
    private String gateCode;

    @Column
    private LocalDateTime accessStartTime;

    @Column
    private LocalDateTime accessEndTime;

    @Column(nullable = false)
    private Boolean hasAfterHoursAccess = false;

    // Move-in/Move-out Information
    @Column
    private LocalDateTime actualMoveInDate;

    @Column
    private LocalDateTime scheduledMoveOutDate;

    @Column
    private LocalDateTime actualMoveOutDate;

    @Column
    private String moveInCondition;

    @Column
    private String moveOutCondition;

    // Special Terms and Conditions
    @Column(columnDefinition = "TEXT")
    private String specialTerms;

    @Column(columnDefinition = "TEXT")
    private String customerNotes;

    @Column(columnDefinition = "TEXT")
    private String internalNotes;

    // Legal and Compliance
    @Column(nullable = false)
    private Boolean termsAccepted = false;

    @Column
    private LocalDateTime termsAcceptedDate;

    @Column
    private String termsAcceptedIpAddress;

    @Column
    private String signedDocumentUrl;

    @Column
    private LocalDateTime signatureDate;

    // Termination Details
    @Column
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING)
    private TerminationReason terminationReason;

    @Column
    private String terminationNotes;

    @Column(nullable = false)
    private Boolean hasOutstandingItems = false;

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
    private String lastModifiedBy;

    // Relationships
    @OneToMany(mappedBy = "rentalAgreement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentTransaction> paymentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "rentalAgreement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AgreementAmendment> amendments = new ArrayList<>();

    @OneToMany(mappedBy = "rentalAgreement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    // Business Methods
    public boolean isActive() {
        return this.status == AgreementStatus.ACTIVE &&
               LocalDate.now().isBefore(this.endDate.plusDays(1));
    }

    public boolean isExpiringSoon() {
        if (!isActive()) return false;
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return this.endDate.isBefore(thirtyDaysFromNow) || this.endDate.isEqual(thirtyDaysFromNow);
    }

    public boolean isOverdue() {
        return this.outstandingBalance.compareTo(BigDecimal.ZERO) > 0 &&
               this.nextBillingDate != null &&
               LocalDate.now().isAfter(this.nextBillingDate.plusDays(5));
    }

    public void recordPayment(BigDecimal amount) {
        this.totalPaidToDate = this.totalPaidToDate.add(amount);
        this.outstandingBalance = this.outstandingBalance.subtract(amount);
        if (this.outstandingBalance.compareTo(BigDecimal.ZERO) < 0) {
            this.outstandingBalance = BigDecimal.ZERO;
        }
    }

    public void addCharge(BigDecimal amount, String description) {
        this.outstandingBalance = this.outstandingBalance.add(amount);
    }

    public BigDecimal calculateTotalContractValue() {
        return this.totalMonthlyCharge.multiply(BigDecimal.valueOf(this.termMonths));
    }

    public BigDecimal calculateEarlyTerminationFee() {
        if (this.agreementType == AgreementType.MONTH_TO_MONTH) {
            return BigDecimal.ZERO;
        }
        
        LocalDate today = LocalDate.now();
        if (today.isAfter(this.endDate)) {
            return BigDecimal.ZERO;
        }
        
        long remainingMonths = java.time.temporal.ChronoUnit.MONTHS.between(today, this.endDate);
        return this.monthlyRate.multiply(BigDecimal.valueOf(Math.min(remainingMonths, 2)));
    }

    public void terminate(TerminationReason reason, String notes) {
        this.status = AgreementStatus.TERMINATED;
        this.terminationDate = LocalDate.now();
        this.terminationReason = reason;
        this.terminationNotes = notes;
        this.actualMoveOutDate = LocalDateTime.now();
    }

    public void renew(Integer termMonths, BigDecimal newRate) {
        this.startDate = this.endDate.plusDays(1);
        this.endDate = this.startDate.plusMonths(termMonths);
        this.termMonths = termMonths;
        this.monthlyRate = newRate;
        this.totalMonthlyCharge = calculateTotalMonthlyCharge();
        this.status = AgreementStatus.RENEWED;
    }

    private BigDecimal calculateTotalMonthlyCharge() {
        BigDecimal total = this.monthlyRate;
        if (this.insuranceRate != null) {
            total = total.add(this.insuranceRate);
        }
        if (this.discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = total.multiply(this.discountPercentage).divide(BigDecimal.valueOf(100));
            total = total.subtract(discount);
        }
        return total;
    }

    public boolean requiresRenewalNotification() {
        if (!this.autoRenewEnabled || !this.isActive()) {
            return false;
        }
        
        LocalDate notificationDate = this.endDate.minusDays(30);
        return LocalDate.now().isEqual(notificationDate) || LocalDate.now().isAfter(notificationDate);
    }

    public void suspend(String reason) {
        this.status = AgreementStatus.SUSPENDED;
        this.internalNotes = (this.internalNotes != null ? this.internalNotes + "\n" : "") + 
                           "Suspended on " + LocalDateTime.now() + ": " + reason;
    }

    public void reactivate() {
        if (this.status == AgreementStatus.SUSPENDED) {
            this.status = AgreementStatus.ACTIVE;
            this.internalNotes = (this.internalNotes != null ? this.internalNotes + "\n" : "") + 
                               "Reactivated on " + LocalDateTime.now();
        }
    }

    // Enums
    public enum AgreementType {
        MONTH_TO_MONTH,
        SHORT_TERM,      // 1-6 months
        LONG_TERM,       // 6+ months
        SEASONAL,
        STUDENT,
        CORPORATE
    }

    public enum AgreementStatus {
        DRAFT,
        PENDING_SIGNATURE,
        ACTIVE,
        RENEWED,
        SUSPENDED,
        TERMINATED,
        EXPIRED,
        CANCELLED
    }

    public enum PaymentFrequency {
        MONTHLY,
        QUARTERLY,
        SEMI_ANNUAL,
        ANNUAL,
        BI_WEEKLY
    }

    public enum InsuranceType {
        BASIC,
        STANDARD,
        PREMIUM,
        CUSTOM,
        THIRD_PARTY,
        NONE
    }

    public enum TerminationReason {
        CUSTOMER_REQUEST,
        NON_PAYMENT,
        VIOLATION_OF_TERMS,
        FACILITY_CLOSURE,
        UNIT_DAMAGE,
        UPGRADE,
        DOWNGRADE,
        RELOCATION,
        DEATH,
        OTHER
    }
}