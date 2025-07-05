package com.gogidix.warehousing.billing.model;

/**
 * Enumeration for billing frequency
 */
public enum BillingFrequency {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    SEMI_ANNUALLY("Semi-Annually"),
    ANNUALLY("Annually"),
    ONE_TIME("One Time");

    private final String displayName;

    BillingFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the number of months for the frequency
     */
    public int getMonths() {
        return switch (this) {
            case WEEKLY -> 0; // Special case - less than a month
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            case SEMI_ANNUALLY -> 6;
            case ANNUALLY -> 12;
            case ONE_TIME -> 0; // Special case - no recurring billing
        };
    }

    /**
     * Get the number of days for the frequency
     */
    public int getDays() {
        return switch (this) {
            case WEEKLY -> 7;
            case MONTHLY -> 30;
            case QUARTERLY -> 90;
            case SEMI_ANNUALLY -> 180;
            case ANNUALLY -> 365;
            case ONE_TIME -> 0; // Special case - no recurring billing
        };
    }

    /**
     * Check if this is a recurring frequency
     */
    public boolean isRecurring() {
        return this != ONE_TIME;
    }
}