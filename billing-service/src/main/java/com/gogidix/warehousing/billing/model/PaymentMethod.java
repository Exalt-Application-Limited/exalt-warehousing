package com.gogidix.warehousing.billing.model;

/**
 * Enumeration for payment methods
 */
public enum PaymentMethod {
    BANK_TRANSFER("Bank Transfer"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    DIGITAL_WALLET("Digital Wallet"),
    WIRE_TRANSFER("Wire Transfer"),
    ACH_TRANSFER("ACH Transfer"),
    CHECK("Check"),
    CASH("Cash"),
    CRYPTOCURRENCY("Cryptocurrency"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    SQUARE("Square"),
    INVOICE_FINANCING("Invoice Financing");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the payment method is electronic
     */
    public boolean isElectronic() {
        return this != CHECK && this != CASH;
    }

    /**
     * Check if the payment method supports auto-pay
     */
    public boolean supportsAutoPay() {
        return this == BANK_TRANSFER || 
               this == CREDIT_CARD || 
               this == DEBIT_CARD || 
               this == ACH_TRANSFER ||
               this == DIGITAL_WALLET ||
               this == PAYPAL ||
               this == STRIPE;
    }

    /**
     * Check if the payment method requires manual processing
     */
    public boolean requiresManualProcessing() {
        return this == CHECK || 
               this == CASH || 
               this == WIRE_TRANSFER ||
               this == INVOICE_FINANCING;
    }
}