package com.exalt.warehousing.billing.model;

/**
 * Enumeration for subscription cancellation reasons
 */
public enum CancellationReason {
    CUSTOMER_REQUEST("Customer Request"),
    NON_PAYMENT("Non-Payment"),
    DOWNGRADE("Downgrade to Lower Plan"),
    UPGRADE("Upgrade to Higher Plan"),
    BUSINESS_CLOSURE("Business Closure"),
    COMPETITIVE_OFFERING("Switched to Competitor"),
    COST_CONCERNS("Cost Concerns"),
    FEATURE_LIMITATIONS("Feature Limitations"),
    TECHNICAL_ISSUES("Technical Issues"),
    POOR_SUPPORT("Poor Customer Support"),
    COMPLIANCE_VIOLATION("Compliance Violation"),
    FRAUD_SUSPECTED("Fraud Suspected"),
    DUPLICATE_ACCOUNT("Duplicate Account"),
    ADMINISTRATIVE("Administrative Decision"),
    OTHER("Other");

    private final String displayName;

    CancellationReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the cancellation reason indicates a customer satisfaction issue
     */
    public boolean indicatesCustomerSatisfactionIssue() {
        return this == COMPETITIVE_OFFERING ||
               this == COST_CONCERNS ||
               this == FEATURE_LIMITATIONS ||
               this == TECHNICAL_ISSUES ||
               this == POOR_SUPPORT;
    }

    /**
     * Check if the cancellation reason indicates a business issue
     */
    public boolean indicatesBusinessIssue() {
        return this == NON_PAYMENT ||
               this == BUSINESS_CLOSURE ||
               this == COMPLIANCE_VIOLATION ||
               this == FRAUD_SUSPECTED;
    }

    /**
     * Check if the cancellation reason is voluntary (customer initiated)
     */
    public boolean isVoluntary() {
        return this == CUSTOMER_REQUEST ||
               this == DOWNGRADE ||
               this == UPGRADE ||
               this == BUSINESS_CLOSURE ||
               this == COMPETITIVE_OFFERING ||
               this == COST_CONCERNS;
    }

    /**
     * Check if the cancellation reason is involuntary (company initiated)
     */
    public boolean isInvoluntary() {
        return this == NON_PAYMENT ||
               this == COMPLIANCE_VIOLATION ||
               this == FRAUD_SUSPECTED ||
               this == DUPLICATE_ACCOUNT;
    }
}