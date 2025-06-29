package com.exalt.warehousing.subscription.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing billing records and invoice data for subscriptions
 * 
 * This entity tracks all billing activities including invoices, payments,
 * refunds, and billing adjustments for warehouse subscriptions.
 */
@Entity
@Table(name = "billing_records", indexes = {
    @Index(name = "idx_billing_subscription_id", columnList = "subscription_id"),
    @Index(name = "idx_billing_date", columnList = "billing_date"),
    @Index(name = "idx_billing_type", columnList = "billing_type"),
    @Index(name = "idx_billing_status", columnList = "status"),
    @Index(name = "idx_billing_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_billing_due_date", columnList = "due_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRecord extends BaseEntity {

    /**
     * Reference to the subscription this billing record belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_billing_subscription"))
    @NotNull(message = "Subscription is required")
    private WarehouseSubscription subscription;

    /**
     * Type of billing record
     */
    @NotNull(message = "Billing type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, length = 50)
    private BillingType billingType;

    /**
     * Current status of the billing record
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private BillingStatus status;

    /**
     * Unique invoice number
     */
    @Column(name = "invoice_number", length = 100, unique = true)
    private String invoiceNumber;

    /**
     * Date of billing
     */
    @NotNull(message = "Billing date is required")
    @Column(name = "billing_date", nullable = false)
    private LocalDateTime billingDate;

    /**
     * Due date for payment
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * Billing period start date
     */
    @Column(name = "period_start")
    private LocalDateTime periodStart;

    /**
     * Billing period end date
     */
    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    /**
     * Subtotal amount (before taxes and fees)
     */
    @NotNull(message = "Subtotal is required")
    @PositiveOrZero(message = "Subtotal must be non-negative")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Tax amount
     */
    @PositiveOrZero(message = "Tax amount must be non-negative")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    /**
     * Discount amount applied
     */
    @PositiveOrZero(message = "Discount amount must be non-negative")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    /**
     * Total amount (subtotal + tax - discount)
     */
    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be non-negative")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Amount paid
     */
    @PositiveOrZero(message = "Paid amount must be non-negative")
    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    /**
     * Outstanding balance
     */
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance;

    /**
     * Currency code (ISO 4217)
     */
    @NotNull(message = "Currency is required")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    /**
     * Payment method used
     */
    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    /**
     * External payment processor transaction ID
     */
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    /**
     * Stripe invoice ID
     */
    @Column(name = "stripe_invoice_id", length = 255)
    private String stripeInvoiceId;

    /**
     * Date when payment was received
     */
    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    /**
     * Date when invoice was sent to customer
     */
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    /**
     * Number of payment attempts
     */
    @PositiveOrZero(message = "Payment attempts must be non-negative")
    @Column(name = "payment_attempts", nullable = false)
    private Integer paymentAttempts = 0;

    /**
     * Next retry date for failed payments
     */
    @Column(name = "next_retry_date")
    private LocalDateTime nextRetryDate;

    /**
     * Failure reason for failed payments
     */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    /**
     * Notes or comments about this billing record
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Additional metadata
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Enumeration of billing types
     */
    public enum BillingType {
        /**
         * Regular subscription billing
         */
        SUBSCRIPTION("Subscription", "Regular subscription billing"),
        
        /**
         * One-time setup fee
         */
        SETUP_FEE("Setup Fee", "One-time setup charge"),
        
        /**
         * Usage overage charges
         */
        OVERAGE("Overage", "Usage overage charges"),
        
        /**
         * Additional services
         */
        ADDITIONAL_SERVICES("Additional Services", "Extra services billing"),
        
        /**
         * Adjustment (credit or debit)
         */
        ADJUSTMENT("Adjustment", "Billing adjustment"),
        
        /**
         * Refund
         */
        REFUND("Refund", "Payment refund"),
        
        /**
         * Late fee
         */
        LATE_FEE("Late Fee", "Late payment fee");

        private final String displayName;
        private final String description;

        BillingType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Enumeration of billing statuses
     */
    public enum BillingStatus {
        /**
         * Draft invoice not yet sent
         */
        DRAFT("Draft", "Invoice created but not sent"),
        
        /**
         * Invoice sent to customer
         */
        SENT("Sent", "Invoice sent to customer"),
        
        /**
         * Payment pending
         */
        PENDING("Pending", "Payment in progress"),
        
        /**
         * Successfully paid
         */
        PAID("Paid", "Payment received and processed"),
        
        /**
         * Payment failed
         */
        FAILED("Failed", "Payment attempt failed"),
        
        /**
         * Invoice overdue
         */
        OVERDUE("Overdue", "Payment overdue"),
        
        /**
         * Partially paid
         */
        PARTIAL("Partial", "Partially paid"),
        
        /**
         * Cancelled invoice
         */
        CANCELLED("Cancelled", "Invoice cancelled"),
        
        /**
         * Refunded
         */
        REFUNDED("Refunded", "Payment refunded");

        private final String displayName;
        private final String description;

        BillingStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isSuccessful() {
            return this == PAID;
        }

        public boolean requiresAction() {
            return this == FAILED || this == OVERDUE || this == PENDING;
        }
    }

    /**
     * Calculate balance amount
     * 
     * @return outstanding balance
     */
    public BigDecimal calculateBalance() {
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        return totalAmount.subtract(paid);
    }

    /**
     * Mark as paid
     * 
     * @param amount amount paid
     * @param transactionId payment transaction ID
     */
    public void markAsPaid(BigDecimal amount, String transactionId) {
        this.paidAmount = amount;
        this.balance = calculateBalance();
        this.transactionId = transactionId;
        this.paidDate = LocalDateTime.now();
        
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = BillingStatus.PAID;
        } else {
            this.status = BillingStatus.PARTIAL;
        }
    }

    /**
     * Mark payment as failed
     * 
     * @param reason failure reason
     */
    public void markAsFailed(String reason) {
        this.status = BillingStatus.FAILED;
        this.failureReason = reason;
        this.paymentAttempts++;
        
        // Set next retry date (e.g., 3 days later)
        this.nextRetryDate = LocalDateTime.now().plusDays(3);
    }

    /**
     * Check if invoice is overdue
     * 
     * @return true if invoice is overdue
     */
    public boolean isOverdue() {
        return dueDate != null && 
               LocalDateTime.now().isAfter(dueDate) && 
               status != BillingStatus.PAID && 
               status != BillingStatus.CANCELLED &&
               status != BillingStatus.REFUNDED;
    }

    /**
     * Get days overdue
     * 
     * @return number of days overdue, 0 if not overdue
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.Duration.between(dueDate, LocalDateTime.now()).toDays();
    }

    /**
     * Generate invoice number from UUID
     * 
     * @param subscriptionId subscription ID as UUID
     * @param billingDate billing date
     * @return generated invoice number
     */
    public static String generateInvoiceNumber(java.util.UUID subscriptionId, LocalDateTime billingDate) {
        String yearMonth = billingDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        String subId = subscriptionId.toString().substring(0, 8).toUpperCase();
        return String.format("INV-%s-%s", yearMonth, subId);
    }
    
    /**
     * Generate invoice number from String ID
     * 
     * @param subscriptionId subscription ID as String
     * @param billingDate billing date
     * @return generated invoice number
     */
    public static String generateInvoiceNumber(String subscriptionId, LocalDateTime billingDate) {
        return generateInvoiceNumber(UUID.fromString(subscriptionId), billingDate);
    }
    
    /**
     * Send invoice to customer
     */
    public void markAsSent() {
        this.status = BillingStatus.SENT;
        this.sentDate = LocalDateTime.now();
    }

    /**
     * Cancel invoice
     * 
     * @param reason cancellation reason
     */
    public void cancel(String reason) {
        this.status = BillingStatus.CANCELLED;
        this.notes = reason;
    }

    /**
     * Process refund
     * 
     * @param refundAmount amount to refund
     * @param reason refund reason
     */
    public void processRefund(BigDecimal refundAmount, String reason) {
        this.status = BillingStatus.REFUNDED;
        this.paidAmount = this.paidAmount.subtract(refundAmount);
        this.balance = calculateBalance();
        this.notes = reason;
    }
}