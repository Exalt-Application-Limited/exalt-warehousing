package com.exalt.warehousing.billing.model;

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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing an invoice for billing
 */
@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id", nullable = false)
    private BillingAccount billingAccount;

    @NotBlank
    @Size(max = 50)
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false)
    private InvoiceStatus invoiceStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType;

    @NotNull
    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    @NotNull
    @Column(name = "billing_period_start", nullable = false)
    private LocalDateTime billingPeriodStart;

    @NotNull
    @Column(name = "billing_period_end", nullable = false)
    private LocalDateTime billingPeriodEnd;

    @NotBlank
    @Size(max = 3)
    @Column(name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Positive
    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "tax_rate", precision = 5, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Size(max = 500)
    @Column(name = "discount_reason")
    private String discountReason;

    @NotNull
    @Positive
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "amount_due", precision = 15, scale = 2)
    private BigDecimal amountDue;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Size(max = 1000)
    @Column(name = "notes")
    private String notes;

    @Size(max = 100)
    @Column(name = "payment_reference")
    private String paymentReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "late_fee", precision = 15, scale = 2)
    private BigDecimal lateFee;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "parent_invoice_id")
    private UUID parentInvoiceId;

    @Column(name = "related_invoice_id")
    private UUID relatedInvoiceId;

    @Size(max = 500)
    @Column(name = "billing_address")
    private String billingAddress;

    @Size(max = 100)
    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "auto_pay_attempted")
    private Boolean autoPayAttempted;

    @Column(name = "auto_pay_failure_reason")
    private String autoPayFailureReason;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<InvoiceLineItem> lineItems = new HashSet<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Payment> payments = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if invoice is paid
     */
    @Transient
    public boolean isPaid() {
        return invoiceStatus == InvoiceStatus.PAID;
    }

    /**
     * Check if invoice is overdue
     */
    @Transient
    public boolean isOverdue() {
        return !isPaid() && LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Check if invoice is partially paid
     */
    @Transient
    public boolean isPartiallyPaid() {
        return amountPaid != null && 
               amountPaid.compareTo(BigDecimal.ZERO) > 0 && 
               amountPaid.compareTo(totalAmount) < 0;
    }

    /**
     * Calculate days overdue
     */
    @Transient
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
    }

    /**
     * Calculate remaining balance
     */
    @Transient
    public BigDecimal getRemainingBalance() {
        BigDecimal paid = amountPaid != null ? amountPaid : BigDecimal.ZERO;
        return totalAmount.subtract(paid);
    }

    /**
     * Add a line item to this invoice
     */
    public void addLineItem(InvoiceLineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setInvoice(this);
    }

    /**
     * Remove a line item from this invoice
     */
    public void removeLineItem(InvoiceLineItem lineItem) {
        lineItems.remove(lineItem);
        lineItem.setInvoice(null);
    }

    /**
     * Add a payment to this invoice
     */
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setInvoice(this);
    }

    /**
     * Remove a payment from this invoice
     */
    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setInvoice(null);
    }

    /**
     * Calculate and update total amounts
     */
    public void calculateTotals() {
        // Calculate subtotal from line items
        this.subtotal = lineItems.stream()
                .map(InvoiceLineItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply discount
        BigDecimal discountedSubtotal = subtotal;
        if (discountAmount != null) {
            discountedSubtotal = subtotal.subtract(discountAmount);
        } else if (discountPercentage != null) {
            BigDecimal discount = subtotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            discountedSubtotal = subtotal.subtract(discount);
            this.discountAmount = discount;
        }

        // Calculate tax
        if (taxRate != null) {
            this.taxAmount = discountedSubtotal.multiply(taxRate);
        }

        // Calculate total
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        BigDecimal late = lateFee != null ? lateFee : BigDecimal.ZERO;
        this.totalAmount = discountedSubtotal.add(tax).add(late);

        // Update amount due
        BigDecimal paid = amountPaid != null ? amountPaid : BigDecimal.ZERO;
        this.amountDue = totalAmount.subtract(paid);
    }

    /**
     * Update payment status based on payments
     */
    public void updatePaymentStatus() {
        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.amountPaid = totalPaid;
        this.amountDue = totalAmount.subtract(totalPaid);

        if (totalPaid.compareTo(totalAmount) >= 0) {
            this.invoiceStatus = InvoiceStatus.PAID;
            this.paidDate = LocalDateTime.now();
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            this.invoiceStatus = InvoiceStatus.PARTIALLY_PAID;
        } else if (isOverdue()) {
            this.invoiceStatus = InvoiceStatus.OVERDUE;
        }
    }

    /**
     * Mark invoice as sent
     */
    public void markAsSent() {
        this.invoiceStatus = InvoiceStatus.SENT;
    }

    /**
     * Mark invoice as cancelled
     */
    public void cancel() {
        this.invoiceStatus = InvoiceStatus.CANCELLED;
    }

    /**
     * Mark invoice as void
     */
    public void voidInvoice() {
        this.invoiceStatus = InvoiceStatus.VOID;
    }

    /**
     * Get billing account ID
     */
    public UUID getBillingAccountId() {
        return billingAccount != null ? billingAccount.getId() : null;
    }
}