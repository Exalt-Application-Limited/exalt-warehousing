package com.exalt.warehousing.subscription.model.enums;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Enumeration of subscription plans for warehouse partners
 * 
 * This enum defines the various subscription tiers available to warehouse partners,
 * each with different features, limits, and pricing structures.
 */
@Getter
public enum SubscriptionPlan {
    
    /**
     * Free trial plan - 30-day evaluation
     * Features: Basic access, limited storage, limited orders
     */
    TRIAL(
        "Trial",
        "30-day free trial with basic features",
        BigDecimal.ZERO,
        PlanTier.TRIAL,
        1000,      // Storage limit (cubic meters)
        100,       // Orders per month
        1,         // API requests per second
        false,     // Analytics
        false,     // Priority support
        30         // Trial days
    ),
    
    /**
     * Starter plan - Small warehouse operations
     * Features: Basic features, moderate limits, standard support
     */
    STARTER(
        "Starter",
        "Perfect for small warehouse operations",
        new BigDecimal("99.00"),
        PlanTier.BASIC,
        10000,     // Storage limit (cubic meters)
        1000,      // Orders per month
        5,         // API requests per second
        true,      // Analytics
        false,     // Priority support
        0          // No trial (paid plan)
    ),
    
    /**
     * Professional plan - Medium warehouse operations
     * Features: Enhanced features, higher limits, priority support
     */
    PROFESSIONAL(
        "Professional",
        "Ideal for growing warehouse businesses",
        new BigDecimal("299.00"),
        PlanTier.PROFESSIONAL,
        50000,     // Storage limit (cubic meters)
        5000,      // Orders per month
        15,        // API requests per second
        true,      // Analytics
        true,      // Priority support
        0          // No trial (paid plan)
    ),
    
    /**
     * Business plan - Large warehouse operations
     * Features: Advanced features, high limits, dedicated support
     */
    BUSINESS(
        "Business",
        "Designed for large warehouse operations",
        new BigDecimal("799.00"),
        PlanTier.BUSINESS,
        200000,    // Storage limit (cubic meters)
        20000,     // Orders per month
        50,        // API requests per second
        true,      // Analytics
        true,      // Priority support
        0          // No trial (paid plan)
    ),
    
    /**
     * Enterprise plan - Enterprise-level operations
     * Features: All features, unlimited usage, white-glove support
     */
    ENTERPRISE(
        "Enterprise",
        "Enterprise-grade solution with unlimited capabilities",
        new BigDecimal("1999.00"),
        PlanTier.ENTERPRISE,
        Integer.MAX_VALUE, // Unlimited storage
        Integer.MAX_VALUE, // Unlimited orders
        Integer.MAX_VALUE, // Unlimited API requests
        true,      // Analytics
        true,      // Priority support
        0          // No trial (paid plan)
    ),
    
    /**
     * Custom plan - Tailored solutions
     * Features: Customizable features, negotiated pricing, dedicated account management
     */
    CUSTOM(
        "Custom",
        "Tailored solution for specific business needs",
        BigDecimal.ZERO, // Negotiated pricing
        PlanTier.CUSTOM,
        Integer.MAX_VALUE, // Negotiable limits
        Integer.MAX_VALUE, // Negotiable limits
        Integer.MAX_VALUE, // Negotiable limits
        true,      // Analytics
        true,      // Priority support
        0          // No trial (paid plan)
    );

    /**
     * Plan display name
     */
    private final String displayName;
    
    /**
     * Plan description
     */
    private final String description;
    
    /**
     * Monthly price in USD
     */
    private final BigDecimal monthlyPrice;
    
    /**
     * Plan tier classification
     */
    private final PlanTier tier;
    
    /**
     * Storage limit in cubic meters
     */
    private final int storageLimit;
    
    /**
     * Orders per month limit
     */
    private final int ordersPerMonth;
    
    /**
     * API requests per second limit
     */
    private final int apiRequestsPerSecond;
    
    /**
     * Whether analytics features are included
     */
    private final boolean includesAnalytics;
    
    /**
     * Whether priority support is included
     */
    private final boolean includesPrioritySupport;
    
    /**
     * Trial period in days (0 for paid plans)
     */
    private final int trialDays;

    /**
     * Constructor for SubscriptionPlan enum
     */
    SubscriptionPlan(String displayName, String description, BigDecimal monthlyPrice,
                    PlanTier tier, int storageLimit, int ordersPerMonth, 
                    int apiRequestsPerSecond, boolean includesAnalytics,
                    boolean includesPrioritySupport, int trialDays) {
        this.displayName = displayName;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.tier = tier;
        this.storageLimit = storageLimit;
        this.ordersPerMonth = ordersPerMonth;
        this.apiRequestsPerSecond = apiRequestsPerSecond;
        this.includesAnalytics = includesAnalytics;
        this.includesPrioritySupport = includesPrioritySupport;
        this.trialDays = trialDays;
    }

    /**
     * Get annual price with discount
     * 
     * @return annual price (typically 10% discount)
     */
    public BigDecimal getAnnualPrice() {
        if (monthlyPrice.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        // 10% discount for annual payments
        BigDecimal annualPrice = monthlyPrice.multiply(BigDecimal.valueOf(12));
        BigDecimal discount = annualPrice.multiply(new BigDecimal("0.10"));
        return annualPrice.subtract(discount);
    }

    /**
     * Get setup fee (if applicable)
     * 
     * @return setup fee amount
     */
    public BigDecimal getSetupFee() {
        switch (this) {
            case ENTERPRISE:
            case CUSTOM:
                return new BigDecimal("500.00");
            case BUSINESS:
                return new BigDecimal("200.00");
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Check if plan is a trial plan
     * 
     * @return true if this is a trial plan
     */
    public boolean isTrial() {
        return this == TRIAL;
    }

    /**
     * Check if plan is a paid plan
     * 
     * @return true if this is a paid plan
     */
    public boolean isPaidPlan() {
        return !isTrial() && !monthlyPrice.equals(BigDecimal.ZERO);
    }

    /**
     * Check if plan has unlimited usage
     * 
     * @return true if plan has unlimited usage
     */
    public boolean hasUnlimitedUsage() {
        return this == ENTERPRISE || this == CUSTOM;
    }

    /**
     * Check if plan includes specific feature
     * 
     * @param feature the feature to check
     * @return true if feature is included
     */
    public boolean includesFeature(PlanFeature feature) {
        switch (feature) {
            case ANALYTICS:
                return includesAnalytics;
            case PRIORITY_SUPPORT:
                return includesPrioritySupport;
            case API_ACCESS:
                return true; // All plans include API access
            case MULTI_WAREHOUSE:
                return tier.ordinal() >= PlanTier.PROFESSIONAL.ordinal();
            case CUSTOM_INTEGRATIONS:
                return tier.ordinal() >= PlanTier.BUSINESS.ordinal();
            case WHITE_LABEL:
                return tier.ordinal() >= PlanTier.ENTERPRISE.ordinal();
            case DEDICATED_ACCOUNT_MANAGER:
                return this == ENTERPRISE || this == CUSTOM;
            default:
                return false;
        }
    }

    /**
     * Get recommended plan for upgrade
     * 
     * @return next tier plan for upgrade
     */
    public SubscriptionPlan getUpgradePlan() {
        switch (this) {
            case TRIAL:
                return STARTER;
            case STARTER:
                return PROFESSIONAL;
            case PROFESSIONAL:
                return BUSINESS;
            case BUSINESS:
                return ENTERPRISE;
            default:
                return this; // Already at highest tier
        }
    }

    /**
     * Get overage pricing for exceeding limits
     * 
     * @param overageType the type of overage
     * @return price per unit overage
     */
    public BigDecimal getOveragePrice(OverageType overageType) {
        switch (overageType) {
            case STORAGE:
                switch (tier) {
                    case TRIAL:
                    case BASIC:
                        return new BigDecimal("0.10"); // $0.10 per cubic meter
                    case PROFESSIONAL:
                        return new BigDecimal("0.08");
                    case BUSINESS:
                        return new BigDecimal("0.06");
                    default:
                        return BigDecimal.ZERO; // No overage for unlimited plans
                }
            case ORDERS:
                switch (tier) {
                    case TRIAL:
                    case BASIC:
                        return new BigDecimal("0.50"); // $0.50 per order
                    case PROFESSIONAL:
                        return new BigDecimal("0.40");
                    case BUSINESS:
                        return new BigDecimal("0.30");
                    default:
                        return BigDecimal.ZERO;
                }
            case API_REQUESTS:
                return new BigDecimal("0.01"); // $0.01 per 1000 requests
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Plan tier classification
     */
    public enum PlanTier {
        TRIAL("Trial"),
        BASIC("Basic"),
        PROFESSIONAL("Professional"),
        BUSINESS("Business"),
        ENTERPRISE("Enterprise"),
        CUSTOM("Custom");

        @Getter
        private final String displayName;

        PlanTier(String displayName) {
            this.displayName = displayName;
        }
    }

    /**
     * Plan features enumeration
     */
    public enum PlanFeature {
        ANALYTICS,
        PRIORITY_SUPPORT,
        API_ACCESS,
        MULTI_WAREHOUSE,
        CUSTOM_INTEGRATIONS,
        WHITE_LABEL,
        DEDICATED_ACCOUNT_MANAGER
    }

    /**
     * Overage types enumeration
     */
    public enum OverageType {
        STORAGE,
        ORDERS,
        API_REQUESTS
    }
}