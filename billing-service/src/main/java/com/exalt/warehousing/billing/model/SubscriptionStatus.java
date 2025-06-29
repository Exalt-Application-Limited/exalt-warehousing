package com.exalt.warehousing.billing.model;

/**
 * Enumeration for subscription status
 */
public enum SubscriptionStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired"),
    TRIAL("Trial"),
    PENDING_ACTIVATION("Pending Activation"),
    PENDING_CANCELLATION("Pending Cancellation"),
    PAST_DUE("Past Due"),
    UNPAID("Unpaid");

    private final String displayName;

    SubscriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the status allows service usage
     */
    public boolean allowsServiceUsage() {
        return this == ACTIVE || this == TRIAL;
    }

    /**
     * Check if the status is billable
     */
    public boolean isBillable() {
        return this == ACTIVE || this == TRIAL || this == PAST_DUE;
    }

    /**
     * Check if the status is terminal (cannot be changed)
     */
    public boolean isTerminal() {
        return this == CANCELLED || this == EXPIRED;
    }

    /**
     * Check if the status indicates payment issues
     */
    public boolean hasPaymentIssues() {
        return this == PAST_DUE || this == UNPAID || this == SUSPENDED;
    }

    /**
     * Check if the status is pending
     */
    public boolean isPending() {
        return this == PENDING_ACTIVATION || this == PENDING_CANCELLATION;
    }
}