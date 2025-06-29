package com.exalt.warehousing.onboarding.model;

/**
 * Verification Status Enumeration
 * 
 * Represents the status of verification processes
 */
public enum VerificationStatus {
    PENDING("Verification pending initiation"),
    IN_PROGRESS("Verification in progress"),
    COMPLETED("Verification completed successfully"),
    FAILED("Verification failed"),
    EXPIRED("Verification result expired"),
    CANCELLED("Verification cancelled"),
    REQUIRES_REVIEW("Verification requires manual review");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if status is final
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == EXPIRED || this == CANCELLED;
    }

    /**
     * Check if status indicates success
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Check if status requires action
     */
    public boolean requiresAction() {
        return this == REQUIRES_REVIEW || this == FAILED || this == EXPIRED;
    }

    /**
     * Check if verification can be retried
     */
    public boolean canRetry() {
        return this == FAILED || this == EXPIRED || this == CANCELLED;
    }
}