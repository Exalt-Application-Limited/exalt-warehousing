package com.exalt.warehousing.billing.model;

/**
 * Enumeration for billing account status
 */
public enum BillingAccountStatus {
    ACTIVE("Active - Account is operational"),
    SUSPENDED("Suspended - Account temporarily disabled"),
    CLOSED("Closed - Account permanently closed"),
    PENDING_ACTIVATION("Pending Activation - Account created but not yet active"),
    PAYMENT_OVERDUE("Payment Overdue - Account has overdue payments"),
    CREDIT_HOLD("Credit Hold - Account on hold due to credit issues"),
    UNDER_REVIEW("Under Review - Account being reviewed for compliance");

    private final String description;

    BillingAccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if the status allows billing operations
     */
    public boolean isBillable() {
        return this == ACTIVE || this == PAYMENT_OVERDUE;
    }

    /**
     * Check if the status is active
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Check if the status is suspended or closed
     */
    public boolean isInactive() {
        return this == SUSPENDED || this == CLOSED;
    }

    /**
     * Check if the status indicates payment issues
     */
    public boolean hasPaymentIssues() {
        return this == PAYMENT_OVERDUE || this == CREDIT_HOLD;
    }
}