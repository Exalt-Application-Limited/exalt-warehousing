package com.gogidix.warehousing.subscription.model;

import com.gogidix.warehousing.shared.common.BaseEntity;
import com.gogidix.warehousing.subscription.model.enums.SubscriptionPlan;
import com.gogidix.warehousing.subscription.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a warehouse partner subscription
 * 
 * This entity manages the complete subscription lifecycle including plan details,
 * billing information, usage tracking, and subscription status management.
 */
@Entity
@Table(name = "warehouse_subscriptions", indexes = {
    @Index(name = "idx_subscription_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_subscription_status", columnList = "status"),
    @Index(name = "idx_subscription_plan", columnList = "plan"),
    @Index(name = "idx_subscription_billing_date", columnList = "next_billing_date"),
    @Index(name = "idx_subscription_active", columnList = "is_active"),
    @Index(name = "idx_subscription_tenant", columnList = "tenant_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseSubscription extends BaseEntity {

    /**
     * UUID of the warehouse this subscription belongs to
     */
    @NotNull(message = "Warehouse ID is required")
    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;

    /**
     * Subscription plan type
     */
    @NotNull(message = "Subscription plan is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 50)
    private SubscriptionPlan plan;

    /**
     * Current subscription status
     */
    @NotNull(message = "Subscription status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private SubscriptionStatus status;

    /**
     * Monthly subscription fee
     */
    @NotNull(message = "Monthly fee is required")
    @PositiveOrZero(message = "Monthly fee must be non-negative")
    @Column(name = "monthly_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyFee;

    /**
     * Annual subscription fee (if applicable)
     */
    @PositiveOrZero(message = "Annual fee must be non-negative")
    @Column(name = "annual_fee", precision = 10, scale = 2)
    private BigDecimal annualFee;

    /**
     * Setup fee (one-time charge)
     */
    @PositiveOrZero(message = "Setup fee must be non-negative")
    @Column(name = "setup_fee", precision = 10, scale = 2)
    private BigDecimal setupFee;

    /**
     * Whether the subscription is billed annually
     */
    @Column(name = "is_annual_billing", nullable = false)
    private Boolean isAnnualBilling = false;

    /**
     * Subscription start date
     */
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * Subscription end date (null for ongoing subscriptions)
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Trial end date (if applicable)
     */
    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    /**
     * Next billing date
     */
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    /**
     * Last billing date
     */
    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;

    /**
     * Date when subscription was cancelled (if applicable)
     */
    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    /**
     * Reason for cancellation
     */
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    /**
     * Whether cancellation takes effect at period end
     */
    @Column(name = "cancel_at_period_end", nullable = false)
    private Boolean cancelAtPeriodEnd = false;

    /**
     * Current billing cycle number
     */
    @PositiveOrZero(message = "Billing cycle must be non-negative")
    @Column(name = "billing_cycle", nullable = false)
    private Integer billingCycle = 0;

    /**
     * Storage limit in cubic meters
     */
    @Positive(message = "Storage limit must be positive")
    @Column(name = "storage_limit", nullable = false)
    private Integer storageLimit;

    /**
     * Current storage usage in cubic meters
     */
    @PositiveOrZero(message = "Storage usage must be non-negative")
    @Column(name = "storage_usage", nullable = false)
    private Integer storageUsage = 0;

    /**
     * Orders per month limit
     */
    @Positive(message = "Orders limit must be positive")
    @Column(name = "orders_limit", nullable = false)
    private Integer ordersLimit;

    /**
     * Current month orders count
     */
    @PositiveOrZero(message = "Orders count must be non-negative")
    @Column(name = "orders_count", nullable = false)
    private Integer ordersCount = 0;

    /**
     * API requests per second limit
     */
    @Positive(message = "API limit must be positive")
    @Column(name = "api_requests_limit", nullable = false)
    private Integer apiRequestsLimit;

    /**
     * Current API usage tracking
     */
    @PositiveOrZero(message = "API usage must be non-negative")
    @Column(name = "api_requests_used", nullable = false)
    private Long apiRequestsUsed = 0L;

    /**
     * Stripe customer ID for payment processing
     */
    @Column(name = "stripe_customer_id", length = 255)
    private String stripeCustomerId;

    /**
     * Stripe subscription ID
     */
    @Column(name = "stripe_subscription_id", length = 255)
    private String stripeSubscriptionId;

    /**
     * Payment method token/ID
     */
    @Column(name = "payment_method_id", length = 255)
    private String paymentMethodId;

    /**
     * Billing email address
     */
    @Column(name = "billing_email", length = 255)
    private String billingEmail;

    /**
     * Additional subscription metadata
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Discount percentage applied
     */
    @PositiveOrZero(message = "Discount must be non-negative")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    /**
     * Discount end date
     */
    @Column(name = "discount_end_date")
    private LocalDateTime discountEndDate;

    /**
     * Whether analytics features are enabled
     */
    @Column(name = "analytics_enabled", nullable = false)
    private Boolean analyticsEnabled = false;

    /**
     * Whether priority support is enabled
     */
    @Column(name = "priority_support_enabled", nullable = false)
    private Boolean prioritySupportEnabled = false;

    /**
     * Number of active users
     */
    @Column(name = "active_users", nullable = false)
    private Integer activeUsers = 0;

    /**
     * User limit for the subscription
     */
    @Column(name = "users_limit", nullable = false)
    private Integer usersLimit = 0;

    /**
     * Usage records associated with this subscription
     */
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UsageRecord> usageRecords = new ArrayList<>();

    /**
     * Billing records associated with this subscription
     */
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BillingRecord> billingRecords = new ArrayList<>();

    /**
     * Check if subscription is currently active
     * 
     * @return true if subscription is active
     */
    public boolean isCurrentlyActive() {
        return status == SubscriptionStatus.ACTIVE && isActive();
    }

    /**
     * Check if subscription is in trial period
     * 
     * @return true if in trial
     */
    public boolean isInTrial() {
        return status == SubscriptionStatus.TRIAL && 
               trialEndDate != null && 
               LocalDateTime.now().isBefore(trialEndDate);
    }

    /**
     * Check if trial has expired
     * 
     * @return true if trial has expired
     */
    public boolean isTrialExpired() {
        return status == SubscriptionStatus.TRIAL && 
               trialEndDate != null && 
               LocalDateTime.now().isAfter(trialEndDate);
    }

    /**
     * Check if subscription has usage limits
     * 
     * @return true if usage limits apply
     */
    public boolean hasUsageLimits() {
        return !plan.hasUnlimitedUsage();
    }

    /**
     * Check if storage limit is exceeded
     * 
     * @return true if storage usage exceeds limit
     */
    public boolean isStorageLimitExceeded() {
        return hasUsageLimits() && storageUsage > storageLimit;
    }

    /**
     * Check if orders limit is exceeded
     * 
     * @return true if orders count exceeds limit
     */
    public boolean isOrdersLimitExceeded() {
        return hasUsageLimits() && ordersCount > ordersLimit;
    }

    /**
     * Get storage usage percentage
     * 
     * @return usage percentage (0-100+)
     */
    public BigDecimal getStorageUsagePercentage() {
        if (storageLimit == 0 || !hasUsageLimits()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(storageUsage)
                .divide(BigDecimal.valueOf(storageLimit), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Get orders usage percentage
     * 
     * @return usage percentage (0-100+)
     */
    public BigDecimal getOrdersUsagePercentage() {
        if (ordersLimit == 0 || !hasUsageLimits()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(ordersCount)
                .divide(BigDecimal.valueOf(ordersLimit), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Get remaining days in current billing cycle
     * 
     * @return days remaining
     */
    public long getDaysRemainingInCycle() {
        if (nextBillingDate == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(nextBillingDate)) {
            return 0;
        }
        return java.time.Duration.between(now, nextBillingDate).toDays();
    }

    /**
     * Calculate current cycle overage charges
     * 
     * @return total overage amount
     */
    public BigDecimal calculateOverageCharges() {
        BigDecimal totalOverage = BigDecimal.ZERO;
        
        if (!hasUsageLimits()) {
            return totalOverage;
        }
        
        // Storage overage
        if (storageUsage > storageLimit) {
            int storageOverage = storageUsage - storageLimit;
            BigDecimal storageOveragePrice = plan.getOveragePrice(SubscriptionPlan.OverageType.STORAGE);
            totalOverage = totalOverage.add(storageOveragePrice.multiply(BigDecimal.valueOf(storageOverage)));
        }
        
        // Orders overage
        if (ordersCount > ordersLimit) {
            int ordersOverage = ordersCount - ordersLimit;
            BigDecimal ordersOveragePrice = plan.getOveragePrice(SubscriptionPlan.OverageType.ORDERS);
            totalOverage = totalOverage.add(ordersOveragePrice.multiply(BigDecimal.valueOf(ordersOverage)));
        }
        
        return totalOverage;
    }

    /**
     * Calculate effective monthly fee with discounts
     * 
     * @return effective monthly fee
     */
    public BigDecimal getEffectiveMonthlyFee() {
        BigDecimal effectiveFee = monthlyFee;
        
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            if (discountEndDate == null || LocalDateTime.now().isBefore(discountEndDate)) {
                BigDecimal discount = effectiveFee.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
                effectiveFee = effectiveFee.subtract(discount);
            }
        }
        
        return effectiveFee;
    }

    /**
     * Update usage counters
     * 
     * @param storageUsed storage usage to add
     * @param ordersProcessed orders to add
     * @param apiRequests API requests to add
     */
    public void updateUsage(int storageUsed, int ordersProcessed, long apiRequests) {
        this.storageUsage += storageUsed;
        this.ordersCount += ordersProcessed;
        this.apiRequestsUsed += apiRequests;
    }

    /**
     * Reset monthly usage counters (called at billing cycle)
     */
    public void resetMonthlyUsage() {
        this.ordersCount = 0;
        this.apiRequestsUsed = 0L;
        // Note: Storage usage is cumulative and not reset
    }

    /**
     * Apply discount to subscription
     * 
     * @param percentage discount percentage
     * @param endDate discount end date
     */
    public void applyDiscount(BigDecimal percentage, LocalDateTime endDate) {
        this.discountPercentage = percentage;
        this.discountEndDate = endDate;
    }

    /**
     * Cancel subscription
     * 
     * @param reason cancellation reason
     * @param atPeriodEnd whether to cancel at period end
     */
    public void cancel(String reason, boolean atPeriodEnd) {
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledDate = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelAtPeriodEnd = atPeriodEnd;
        
        if (!atPeriodEnd) {
            this.endDate = LocalDateTime.now();
        }
    }

    /**
     * Reactivate cancelled subscription
     */
    public void reactivate() {
        if (status == SubscriptionStatus.CANCELLED || status == SubscriptionStatus.EXPIRED) {
            this.status = SubscriptionStatus.ACTIVE;
            this.cancelledDate = null;
            this.cancellationReason = null;
            this.cancelAtPeriodEnd = false;
            this.endDate = null;
        }
    }

    /**
     * Upgrade subscription plan
     * 
     * @param newPlan the new subscription plan
     */
    public void upgradePlan(SubscriptionPlan newPlan) {
        this.plan = newPlan;
        this.monthlyFee = newPlan.getMonthlyPrice();
        this.annualFee = newPlan.getAnnualPrice();
        this.storageLimit = newPlan.getStorageLimit();
        this.ordersLimit = newPlan.getOrdersPerMonth();
        this.apiRequestsLimit = newPlan.getApiRequestsPerSecond();
        this.analyticsEnabled = newPlan.isIncludesAnalytics();
        this.prioritySupportEnabled = newPlan.isIncludesPrioritySupport();
    }

    /**
     * Get current billing period start date
     * 
     * @return current period start date
     */
    public LocalDateTime getCurrentPeriodStart() {
        if (lastBillingDate != null) {
            return lastBillingDate;
        }
        return startDate;
    }

    /**
     * Get current billing period end date
     * 
     * @return current period end date
     */
    public LocalDateTime getCurrentPeriodEnd() {
        return nextBillingDate;
    }
}
