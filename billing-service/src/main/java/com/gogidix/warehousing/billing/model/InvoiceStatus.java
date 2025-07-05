package com.gogidix.warehousing.billing.model;

/**
 * Enumeration for invoice status
 */
public enum InvoiceStatus {
    DRAFT("Draft"),
    PENDING("Pending"),
    SENT("Sent"),
    VIEWED("Viewed"),
    PARTIALLY_PAID("Partially Paid"),
    PAID("Paid"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled"),
    VOID("Void"),
    REFUNDED("Refunded"),
    DISPUTED("Disputed"),
    PROCESSING("Processing"),
    FAILED("Failed");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the status indicates the invoice is collectible
     */
    public boolean isCollectible() {
        return this == SENT || 
               this == VIEWED || 
               this == PARTIALLY_PAID || 
               this == OVERDUE;
    }

    /**
     * Check if the status is final (cannot be changed)
     */
    public boolean isFinal() {
        return this == PAID || 
               this == CANCELLED || 
               this == VOID || 
               this == REFUNDED;
    }

    /**
     * Check if the status indicates payment issues
     */
    public boolean hasPaymentIssues() {
        return this == OVERDUE || 
               this == FAILED || 
               this == DISPUTED;
    }

    /**
     * Check if the status allows modifications
     */
    public boolean allowsModification() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * Check if the status indicates successful payment
     */
    public boolean isPaymentSuccessful() {
        return this == PAID || this == PARTIALLY_PAID;
    }
}