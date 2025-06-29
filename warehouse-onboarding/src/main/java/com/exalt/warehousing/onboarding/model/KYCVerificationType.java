package com.exalt.warehousing.onboarding.model;

/**
 * KYC Verification Type Enumeration
 * 
 * Represents different types of KYC verification checks
 */
public enum KYCVerificationType {
    IDENTITY_VERIFICATION("Identity verification of business owners/directors"),
    BUSINESS_VERIFICATION("Business entity verification and registration check"),
    ADDRESS_VERIFICATION("Business address verification"),
    BANK_ACCOUNT_VERIFICATION("Bank account ownership verification"),
    CREDIT_CHECK("Credit worthiness assessment"),
    SANCTIONS_SCREENING("Sanctions and watchlist screening"),
    PEP_SCREENING("Politically Exposed Person (PEP) screening"),
    ADVERSE_MEDIA_CHECK("Adverse media and negative news screening"),
    UBO_VERIFICATION("Ultimate Beneficial Owner verification"),
    REGULATORY_CHECK("Regulatory compliance and licensing check"),
    TAX_COMPLIANCE_CHECK("Tax compliance and filing status check"),
    FINANCIAL_STANDING_CHECK("Financial standing and stability assessment"),
    BACKGROUND_CHECK("Background check on key personnel"),
    REFERENCE_VERIFICATION("Business reference and client verification"),
    DOCUMENT_AUTHENTICITY("Document authenticity and fraud detection");

    private final String description;

    KYCVerificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if verification type is mandatory
     */
    public boolean isMandatory() {
        return this == IDENTITY_VERIFICATION || this == BUSINESS_VERIFICATION || 
               this == ADDRESS_VERIFICATION || this == SANCTIONS_SCREENING;
    }

    /**
     * Check if verification type requires external provider
     */
    public boolean requiresExternalProvider() {
        return this == CREDIT_CHECK || this == SANCTIONS_SCREENING || 
               this == PEP_SCREENING || this == ADVERSE_MEDIA_CHECK || 
               this == DOCUMENT_AUTHENTICITY;
    }

    /**
     * Check if verification type is high risk
     */
    public boolean isHighRisk() {
        return this == SANCTIONS_SCREENING || this == PEP_SCREENING || 
               this == ADVERSE_MEDIA_CHECK || this == BACKGROUND_CHECK;
    }

    /**
     * Get recommended confidence threshold for this verification type
     */
    public double getRecommendedConfidenceThreshold() {
        return switch (this) {
            case IDENTITY_VERIFICATION, BUSINESS_VERIFICATION -> 0.9;
            case SANCTIONS_SCREENING, PEP_SCREENING -> 0.95;
            case DOCUMENT_AUTHENTICITY -> 0.85;
            case CREDIT_CHECK, FINANCIAL_STANDING_CHECK -> 0.75;
            default -> 0.8;
        };
    }
}