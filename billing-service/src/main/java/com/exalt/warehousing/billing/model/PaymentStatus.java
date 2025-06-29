package com.exalt.warehousing.billing.model;

/**
 * Enumeration for payment status
 */
public enum PaymentStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    DECLINED("Declined"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded"),
    PARTIALLY_REFUNDED("Partially Refunded"),
    DISPUTED("Disputed"),
    CHARGEBACK("Chargeback"),
    EXPIRED("Expired"),
    AUTHORIZED("Authorized"),
    CAPTURED("Captured"),
    VOID("Void");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the status indicates successful payment
     */
    public boolean isSuccessful() {
        return this == COMPLETED || 
               this == CAPTURED ||
               this == PARTIALLY_REFUNDED;
    }

    /**
     * Check if the status indicates payment failure
     */
    public boolean isFailed() {
        return this == FAILED || 
               this == DECLINED || 
               this == CANCELLED ||
               this == EXPIRED ||
               this == VOID;
    }

    /**
     * Check if the status is in a pending state
     */
    public boolean isPending() {
        return this == PENDING || 
               this == PROCESSING ||
               this == AUTHORIZED;
    }

    /**
     * Check if the status is final (cannot be changed)
     */
    public boolean isFinal() {
        return this == COMPLETED || 
               this == REFUNDED || 
               this == CANCELLED ||
               this == VOID ||
               this == EXPIRED ||
               this == CHARGEBACK;
    }

    /**
     * Check if the status indicates dispute or chargeback
     */
    public boolean isDisputed() {
        return this == DISPUTED || this == CHARGEBACK;
    }

    /**
     * Check if payment can be captured (for authorized payments)
     */
    public boolean canBeCaptured() {
        return this == AUTHORIZED;
    }

    /**
     * Check if payment can be voided
     */
    public boolean canBeVoided() {
        return this == AUTHORIZED || this == PENDING;
    }

    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return this == COMPLETED || this == CAPTURED;
    }
}