package com.gogidix.warehousing.onboarding.model;

/**
 * KYC Status Enumeration
 * 
 * Represents the status of Know Your Customer verification process
 */
public enum KYCStatus {
    NOT_STARTED("KYC verification not started"),
    PENDING("KYC verification pending"),
    IDENTITY_VERIFICATION("Identity verification in progress"),
    BUSINESS_VERIFICATION("Business verification in progress"),
    FINANCIAL_VERIFICATION("Financial verification in progress"),
    REGULATORY_CHECK("Regulatory compliance check in progress"),
    MANUAL_REVIEW("Manual review required"),
    VERIFIED("KYC verification completed successfully"),
    FAILED("KYC verification failed"),
    EXPIRED("KYC verification expired and needs renewal");

    private final String description;

    KYCStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if KYC is in a final state
     */
    public boolean isFinal() {
        return this == VERIFIED || this == FAILED || this == EXPIRED;
    }

    /**
     * Check if KYC is successful
     */
    public boolean isSuccessful() {
        return this == VERIFIED;
    }

    /**
     * Check if KYC requires action
     */
    public boolean requiresAction() {
        return this == MANUAL_REVIEW || this == FAILED || this == EXPIRED;
    }
}