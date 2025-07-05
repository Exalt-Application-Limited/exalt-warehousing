package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payment Method Entity
 * 
 * Stores customer payment methods for recurring and one-time payments.
 */
@Entity
@Table(name = "payment_methods")
@Data
@EqualsAndHashCode(callSuper = false)
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodType methodType;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isVerified = false;

    // Card Information (encrypted/tokenized)
    @Column
    private String cardToken;

    @Column
    private String cardLastFour;

    @Column
    private String cardBrand;

    @Column
    private LocalDate cardExpiryDate;

    @Column
    private String cardholderName;

    // Bank Account Information (encrypted/tokenized)
    @Column
    private String bankAccountToken;

    @Column
    private String bankAccountLastFour;

    @Column
    private String bankName;

    @Column
    private String accountType; // CHECKING, SAVINGS

    @Column
    private String routingNumber;

    // Digital Wallet
    @Column
    private String walletProvider;

    @Column
    private String walletId;

    // Billing Address
    @Column
    private String billingAddress;

    @Column
    private String billingCity;

    @Column
    private String billingState;

    @Column
    private String billingZipCode;

    @Column
    private String billingCountry;

    // Security and Verification
    @Column
    private LocalDateTime verificationDate;

    @Column
    private String verificationMethod;

    @Column
    private Integer failedAttempts = 0;

    @Column
    private LocalDateTime lastFailedAttempt;

    @Column
    private Boolean isLocked = false;

    // Usage Statistics
    @Column
    private LocalDateTime lastUsedDate;

    @Column
    private Integer timesUsed = 0;

    @Column
    private BigDecimal totalProcessed = BigDecimal.ZERO;

    // Metadata
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

    // Business Methods
    public boolean isExpired() {
        if (this.methodType == PaymentMethodType.CREDIT_CARD || 
            this.methodType == PaymentMethodType.DEBIT_CARD) {
            return this.cardExpiryDate != null && 
                   LocalDate.now().isAfter(this.cardExpiryDate);
        }
        return false;
    }

    public boolean isExpiringSoon() {
        if (this.methodType == PaymentMethodType.CREDIT_CARD || 
            this.methodType == PaymentMethodType.DEBIT_CARD) {
            return this.cardExpiryDate != null && 
                   LocalDate.now().plusMonths(1).isAfter(this.cardExpiryDate);
        }
        return false;
    }

    public void recordUsage(BigDecimal amount) {
        this.lastUsedDate = LocalDateTime.now();
        this.timesUsed++;
        this.totalProcessed = this.totalProcessed.add(amount);
    }

    public void recordFailedAttempt() {
        this.failedAttempts++;
        this.lastFailedAttempt = LocalDateTime.now();
        
        if (this.failedAttempts >= 3) {
            this.isLocked = true;
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.lastFailedAttempt = null;
        this.isLocked = false;
    }

    public void deactivate(String reason) {
        this.isActive = false;
        this.deactivatedAt = LocalDateTime.now();
        this.deactivationReason = reason;
        
        // If this was primary, it's no longer primary
        if (this.isPrimary) {
            this.isPrimary = false;
        }
    }

    public String getMaskedDisplay() {
        if (this.methodType == PaymentMethodType.CREDIT_CARD || 
            this.methodType == PaymentMethodType.DEBIT_CARD) {
            return String.format("%s ending in %s", this.cardBrand, this.cardLastFour);
        } else if (this.methodType == PaymentMethodType.BANK_ACCOUNT) {
            return String.format("%s account ending in %s", this.bankName, this.bankAccountLastFour);
        } else if (this.methodType == PaymentMethodType.DIGITAL_WALLET) {
            return String.format("%s wallet", this.walletProvider);
        }
        return this.methodType.toString();
    }

    // Enums
    public enum PaymentMethodType {
        CREDIT_CARD,
        DEBIT_CARD,
        BANK_ACCOUNT,
        DIGITAL_WALLET,
        CASH,
        CHECK
    }
}