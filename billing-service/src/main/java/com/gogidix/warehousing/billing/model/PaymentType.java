package com.gogidix.warehousing.billing.model;

/**
 * Enumeration for payment types
 */
public enum PaymentType {
    PAYMENT("Payment"),
    REFUND("Refund"),
    CHARGEBACK("Chargeback"),
    REVERSAL("Reversal"),
    ADJUSTMENT("Adjustment"),
    PARTIAL_PAYMENT("Partial Payment"),
    OVERPAYMENT("Overpayment"),
    PREPAYMENT("Prepayment"),
    DEPOSIT("Deposit"),
    CREDIT("Credit"),
    WRITEOFF("Write-off"),
    SETTLEMENT("Settlement");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this payment type increases the account balance
     */
    public boolean increasesBalance() {
        return this == PAYMENT || 
               this == PARTIAL_PAYMENT ||
               this == OVERPAYMENT ||
               this == PREPAYMENT ||
               this == DEPOSIT ||
               this == CREDIT ||
               this == SETTLEMENT;
    }

    /**
     * Check if this payment type decreases the account balance
     */
    public boolean decreasesBalance() {
        return this == REFUND || 
               this == CHARGEBACK ||
               this == REVERSAL;
    }

    /**
     * Check if this is a standard payment type
     */
    public boolean isStandardPayment() {
        return this == PAYMENT || this == PARTIAL_PAYMENT;
    }

    /**
     * Check if this is a reversal type
     */
    public boolean isReversal() {
        return this == REFUND || 
               this == CHARGEBACK ||
               this == REVERSAL;
    }

    /**
     * Check if this is an adjustment type
     */
    public boolean isAdjustment() {
        return this == ADJUSTMENT || 
               this == CREDIT ||
               this == WRITEOFF;
    }

    /**
     * Check if this payment type requires special handling
     */
    public boolean requiresSpecialHandling() {
        return this == CHARGEBACK || 
               this == REVERSAL ||
               this == WRITEOFF ||
               this == ADJUSTMENT;
    }
}