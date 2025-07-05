package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Transaction Entity
 * 
 * Records all financial transactions related to rental agreements.
 */
@Entity
@Table(name = "payment_transactions")
@Data
@EqualsAndHashCode(callSuper = false)
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_agreement_id", nullable = false)
    private RentalAgreement rentalAgreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    // Transaction Details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal netAmount;

    @Column(nullable = false)
    private String currency = "USD";

    // Payment Information
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentChannel paymentChannel;

    @Column
    private String paymentReference;

    @Column
    private String processorTransactionId;

    @Column
    private String authorizationCode;

    // Transaction Dates
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @Column
    private LocalDateTime processedDate;

    @Column
    private LocalDateTime settledDate;

    // Description and Notes
    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column
    private String failureReason;

    // Refund Information (if applicable)
    @Column
    private Boolean isRefund = false;

    @Column
    private String originalTransactionId;

    @Column
    private BigDecimal refundedAmount;

    @Column
    private LocalDateTime refundDate;

    @Column
    private String refundReason;

    // Metadata
    @Column
    private String initiatedBy;

    @Column
    private String ipAddress;

    @Column
    private String deviceInfo;

    // Business Methods
    public boolean isSuccessful() {
        return this.status == TransactionStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.status == TransactionStatus.PENDING ||
               this.status == TransactionStatus.PROCESSING;
    }

    public boolean canBeRefunded() {
        return this.status == TransactionStatus.COMPLETED &&
               !this.isRefund &&
               this.transactionType == TransactionType.PAYMENT;
    }

    public void markAsCompleted(String processorId, String authCode) {
        this.status = TransactionStatus.COMPLETED;
        this.processorTransactionId = processorId;
        this.authorizationCode = authCode;
        this.processedDate = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.processedDate = LocalDateTime.now();
    }

    public void settle() {
        if (this.status == TransactionStatus.COMPLETED) {
            this.status = TransactionStatus.SETTLED;
            this.settledDate = LocalDateTime.now();
        }
    }

    // Enums
    public enum TransactionType {
        PAYMENT,
        DEPOSIT,
        REFUND,
        ADJUSTMENT,
        FEE,
        PENALTY,
        CREDIT,
        CHARGEBACK,
        REVERSAL
    }

    public enum TransactionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        SETTLED,
        REFUNDED,
        DISPUTED
    }

    public enum PaymentChannel {
        ONLINE,
        MOBILE_APP,
        AUTO_PAY,
        IN_PERSON,
        PHONE,
        MAIL,
        KIOSK,
        API
    }
}