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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a warehouse subscription plan
 */
@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id", nullable = false)
    private BillingAccount billingAccount;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "plan_name", nullable = false)
    private String planName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "plan_type", nullable = false)
    private String planType;

    @Size(max = 500)
    @Column(name = "plan_description")
    private String planDescription;

    @NotNull
    @Positive
    @Column(name = "monthly_fee", precision = 15, scale = 2, nullable = false)
    private BigDecimal monthlyFee;

    @Column(name = "setup_fee", precision = 15, scale = 2)
    private BigDecimal setupFee;

    @NotBlank
    @Size(max = 3)
    @Column(name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_frequency", nullable = false)
    private BillingFrequency billingFrequency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Builder.Default
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    // Storage Limits
    @Column(name = "storage_limit_cubic_meters", precision = 10, scale = 2)
    private BigDecimal storageLimitCubicMeters;

    @Column(name = "storage_limit_weight_kg", precision = 10, scale = 2)
    private BigDecimal storageLimitWeightKg;

    // Transaction Limits
    @Column(name = "monthly_transaction_limit")
    private Integer monthlyTransactionLimit;

    @Column(name = "daily_transaction_limit")
    private Integer dailyTransactionLimit;

    // API Limits
    @Column(name = "api_calls_per_minute")
    private Integer apiCallsPerMinute;

    @Column(name = "api_calls_per_day")
    private Integer apiCallsPerDay;

    // Feature Flags
    @Builder.Default
    @Column(name = "analytics_enabled", nullable = false)
    private Boolean analyticsEnabled = false;

    @Builder.Default
    @Column(name = "priority_support", nullable = false)
    private Boolean prioritySupport = false;

    @Builder.Default
    @Column(name = "custom_integrations", nullable = false)
    private Boolean customIntegrations = false;

    @Builder.Default
    @Column(name = "white_label", nullable = false)
    private Boolean whiteLabel = false;

    @Builder.Default
    @Column(name = "multi_warehouse", nullable = false)
    private Boolean multiWarehouse = false;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_reason")
    private CancellationReason cancellationReason;

    @Size(max = 1000)
    @Column(name = "cancellation_notes")
    private String cancellationNotes;

    @Size(max = 1000)
    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SubscriptionUsage> usageRecords = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if subscription is active
     */
    @Transient
    public boolean isActive() {
        return subscriptionStatus == SubscriptionStatus.ACTIVE;
    }

    /**
     * Check if subscription is in trial period
     */
    @Transient
    public boolean isInTrial() {
        return trialEndDate != null && LocalDateTime.now().isBefore(trialEndDate);
    }

    /**
     * Check if subscription is expired
     */
    @Transient
    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    /**
     * Check if subscription is due for renewal
     */
    @Transient
    public boolean isDueForRenewal() {
        return nextBillingDate != null && LocalDateTime.now().isAfter(nextBillingDate);
    }

    /**
     * Calculate days until next billing
     */
    @Transient
    public long getDaysUntilNextBilling() {
        if (nextBillingDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), nextBillingDate);
    }

    /**
     * Add usage record to this subscription
     */
    public void addUsageRecord(SubscriptionUsage usage) {
        usageRecords.add(usage);
        usage.setSubscription(this);
    }

    /**
     * Remove usage record from this subscription
     */
    public void removeUsageRecord(SubscriptionUsage usage) {
        usageRecords.remove(usage);
        usage.setSubscription(null);
    }

    /**
     * Cancel the subscription
     */
    public void cancel(CancellationReason reason, String notes) {
        this.subscriptionStatus = SubscriptionStatus.CANCELLED;
        this.cancellationDate = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancellationNotes = notes;
        this.autoRenew = false;
    }

    /**
     * Activate the subscription
     */
    public void activate() {
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
    }

    /**
     * Suspend the subscription
     */
    public void suspend() {
        this.subscriptionStatus = SubscriptionStatus.SUSPENDED;
    }

    /**
     * Resume the subscription
     */
    public void resume() {
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
    }
}