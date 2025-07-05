package com.gogidix.warehousing.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a payment against an invoice
 */
@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotNull
    @Column(name = "billing_account_id", nullable = false)
    private UUID billingAccountId;

    @Size(max = 50)
    @Column(name = "payment_reference", unique = true)
    private String paymentReference;

    @Size(max = 100)
    @Column(name = "external_transaction_id")
    private String externalTransactionId;

    @NotNull
    @Positive
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 3)
    @Column(name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "cleared_date")
    private LocalDateTime clearedDate;

    @Column(name = "failed_date")
    private LocalDateTime failedDate;

    @Size(max = 500)
    @Column(name = "failure_reason")
    private String failureReason;

    @Size(max = 100)
    @Column(name = "processor_name")
    private String processorName;

    @Size(max = 100)
    @Column(name = "processor_transaction_id")
    private String processorTransactionId;

    @Column(name = "processor_fee", precision = 15, scale = 2)
    private BigDecimal processorFee;

    @Column(name = "net_amount", precision = 15, scale = 2)
    private BigDecimal netAmount;

    @Size(max = 100)
    @Column(name = "bank_reference")
    private String bankReference;

    @Size(max = 100)
    @Column(name = "check_number")
    private String checkNumber;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "is_refund")
    private Boolean isRefund;

    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Size(max = 500)
    @Column(name = "refund_reason")
    private String refundReason;

    @Size(max = 1000)
    @Column(name = "notes")
    private String notes;

    @Size(max = 500)
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if payment is completed
     */
    @Transient
    public boolean isCompleted() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

    /**
     * Check if payment is pending
     */
    @Transient
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING ||
               paymentStatus == PaymentStatus.PROCESSING;
    }

    /**
     * Check if payment failed
     */
    @Transient
    public boolean isFailed() {
        return paymentStatus == PaymentStatus.FAILED ||
               paymentStatus == PaymentStatus.DECLINED ||
               paymentStatus == PaymentStatus.CANCELLED;
    }

    /**
     * Check if payment can be refunded
     */
    @Transient
    public boolean canBeRefunded() {
        return isCompleted() && 
               (isRefund == null || !isRefund) &&
               (refundAmount == null || refundAmount.compareTo(amount) < 0);
    }

    /**
     * Get remaining refundable amount
     */
    @Transient
    public BigDecimal getRefundableAmount() {
        if (!canBeRefunded()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal refunded = refundAmount != null ? refundAmount : BigDecimal.ZERO;
        return amount.subtract(refunded);
    }

    /**
     * Mark payment as completed
     */
    public void markAsCompleted(String transactionId) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.processedDate = LocalDateTime.now();
        this.processorTransactionId = transactionId;
        
        if (this.paymentDate == null) {
            this.paymentDate = LocalDateTime.now();
        }
        
        if (this.netAmount == null) {
            BigDecimal fees = processorFee != null ? processorFee : BigDecimal.ZERO;
            this.netAmount = amount.subtract(fees);
        }
    }

    /**
     * Mark payment as failed
     */
    public void markAsFailed(String reason) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failedDate = LocalDateTime.now();
        this.failureReason = reason;
    }

    /**
     * Mark payment as declined
     */
    public void markAsDeclined(String reason) {
        this.paymentStatus = PaymentStatus.DECLINED;
        this.failedDate = LocalDateTime.now();
        this.failureReason = reason;
    }

    /**
     * Cancel the payment
     */
    public void cancel(String reason) {
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.failedDate = LocalDateTime.now();
        this.failureReason = reason;
    }

    /**
     * Process a refund
     */
    public void processRefund(BigDecimal refundAmt, String reason) {
        if (refundAmount == null) {
            refundAmount = BigDecimal.ZERO;
        }
        
        this.refundAmount = refundAmount.add(refundAmt);
        this.refundDate = LocalDateTime.now();
        this.refundReason = reason;
        
        if (refundAmount.compareTo(amount) >= 0) {
            this.paymentStatus = PaymentStatus.REFUNDED;
        } else {
            this.paymentStatus = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }
}