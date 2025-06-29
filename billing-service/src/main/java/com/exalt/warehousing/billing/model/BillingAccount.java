package com.exalt.warehousing.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * Entity representing a billing account for warehouse partners
 */
@Entity
@Table(name = "billing_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "warehouse_partner_id", nullable = false, unique = true)
    private UUID warehousePartnerId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @NotBlank
    @Size(max = 255)
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Email
    @NotBlank
    @Size(max = 255)
    @Column(name = "billing_email", nullable = false)
    private String billingEmail;

    @Size(max = 20)
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank
    @Size(max = 500)
    @Column(name = "billing_address", nullable = false)
    private String billingAddress;

    @NotBlank
    @Size(max = 100)
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Size(max = 100)
    @Column(name = "state_province", nullable = false)
    private String stateProvince;

    @NotBlank
    @Size(max = 20)
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "country", nullable = false)
    private String country;

    @Size(max = 50)
    @Column(name = "tax_id")
    private String taxId;

    @Size(max = 50)
    @Column(name = "vat_number")
    private String vatNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private BillingAccountStatus accountStatus;

    @NotBlank
    @Size(max = 3)
    @Column(name = "preferred_currency", nullable = false)
    private String preferredCurrency;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Builder.Default
    @Column(name = "current_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "outstanding_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Size(max = 100)
    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "auto_pay_enabled")
    private Boolean autoPayEnabled;

    @Column(name = "billing_cycle_day")
    private Integer billingCycleDay;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;

    @Column(name = "account_opened_date")
    private LocalDateTime accountOpenedDate;

    @Column(name = "account_closed_date")
    private LocalDateTime accountClosedDate;

    @Size(max = 1000)
    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Invoice> invoices = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculate total credit used
     */
    @Transient
    public BigDecimal getCreditUsed() {
        if (creditLimit == null) {
            return BigDecimal.ZERO;
        }
        return creditLimit.subtract(currentBalance);
    }

    /**
     * Check if account is over credit limit
     */
    @Transient
    public boolean isOverCreditLimit() {
        if (creditLimit == null) {
            return false;
        }
        return currentBalance.compareTo(creditLimit.negate()) < 0;
    }

    /**
     * Check if account has outstanding payments
     */
    @Transient
    public boolean hasOutstandingBalance() {
        return outstandingBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Add a subscription to this account
     */
    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.setBillingAccount(this);
    }

    /**
     * Remove a subscription from this account
     */
    public void removeSubscription(Subscription subscription) {
        subscriptions.remove(subscription);
        subscription.setBillingAccount(null);
    }

    /**
     * Add an invoice to this account
     */
    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
        invoice.setBillingAccount(this);
    }

    /**
     * Remove an invoice from this account
     */
    public void removeInvoice(Invoice invoice) {
        invoices.remove(invoice);
        invoice.setBillingAccount(null);
    }

    /**
     * Update the account balance
     */
    public void updateBalance(BigDecimal amount) {
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        currentBalance = currentBalance.add(amount);
    }

    /**
     * Update the outstanding balance
     */
    public void updateOutstandingBalance(BigDecimal amount) {
        if (outstandingBalance == null) {
            outstandingBalance = BigDecimal.ZERO;
        }
        outstandingBalance = outstandingBalance.add(amount);
    }
}