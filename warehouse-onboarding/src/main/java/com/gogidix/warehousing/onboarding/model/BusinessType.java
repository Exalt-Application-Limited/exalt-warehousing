package com.gogidix.warehousing.onboarding.model;

/**
 * Business Type Enumeration
 * 
 * Represents different types of business entities
 */
public enum BusinessType {
    CORPORATION("Corporation"),
    LIMITED_LIABILITY_COMPANY("Limited Liability Company (LLC)"),
    PARTNERSHIP("Partnership"),
    SOLE_PROPRIETORSHIP("Sole Proprietorship"),
    PUBLIC_LIMITED_COMPANY("Public Limited Company"),
    PRIVATE_LIMITED_COMPANY("Private Limited Company"),
    COOPERATIVE("Cooperative"),
    NON_PROFIT("Non-Profit Organization"),
    TRUST("Trust"),
    GOVERNMENT_ENTITY("Government Entity"),
    OTHER("Other");

    private final String displayName;

    BusinessType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if business type requires enhanced KYC
     */
    public boolean requiresEnhancedKYC() {
        return this == CORPORATION || this == PUBLIC_LIMITED_COMPANY || this == TRUST || this == GOVERNMENT_ENTITY;
    }

    /**
     * Check if business type requires board resolutions
     */
    public boolean requiresBoardResolution() {
        return this == CORPORATION || this == PUBLIC_LIMITED_COMPANY || this == PRIVATE_LIMITED_COMPANY;
    }
}