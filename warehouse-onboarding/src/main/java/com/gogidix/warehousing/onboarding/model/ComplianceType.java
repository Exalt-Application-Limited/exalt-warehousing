package com.gogidix.warehousing.onboarding.model;

/**
 * Compliance Type Enumeration
 * 
 * Represents different types of regulatory compliance checks
 */
public enum ComplianceType {
    // General Business Compliance
    BUSINESS_LICENSE("Business License Verification", true),
    TAX_REGISTRATION("Tax Registration Compliance", true),
    CORPORATE_REGISTRATION("Corporate Registration Verification", true),
    
    // Warehouse and Storage Compliance
    WAREHOUSE_LICENSE("Warehouse Operating License", true),
    STORAGE_PERMIT("Storage Facility Permit", true),
    FIRE_SAFETY_COMPLIANCE("Fire Safety Compliance", true),
    BUILDING_CODE_COMPLIANCE("Building Code Compliance", true),
    ZONING_COMPLIANCE("Zoning and Land Use Compliance", true),
    
    // Health and Safety Compliance
    OCCUPATIONAL_SAFETY("Occupational Health and Safety Compliance", true),
    ENVIRONMENTAL_COMPLIANCE("Environmental Regulations Compliance", true),
    WASTE_MANAGEMENT_PERMIT("Waste Management Permit", false),
    
    // Industry-Specific Compliance
    FOOD_SAFETY_CERTIFICATION("Food Safety Certification (HACCP/ISO 22000)", false),
    PHARMACEUTICAL_COMPLIANCE("Pharmaceutical Storage Compliance (GDP)", false),
    HAZMAT_CERTIFICATION("Hazardous Materials Handling Certification", false),
    CUSTOMS_BONDED_STATUS("Customs Bonded Warehouse Status", false),
    FREEPORT_LICENSE("Freeport/Free Zone License", false),
    
    // Quality and Standards Compliance
    ISO_CERTIFICATION("ISO Quality Management Certification", false),
    SECURITY_COMPLIANCE("Security Standards Compliance", false),
    DATA_PROTECTION_COMPLIANCE("Data Protection and Privacy Compliance", true),
    
    // Financial and Insurance Compliance
    INSURANCE_COVERAGE("Liability Insurance Coverage Verification", true),
    FINANCIAL_SERVICES_LICENSE("Financial Services License (if applicable)", false),
    ANTI_MONEY_LAUNDERING("Anti-Money Laundering Compliance", true),
    
    // International Trade Compliance
    EXPORT_CONTROL_COMPLIANCE("Export Control Regulations Compliance", false),
    IMPORT_LICENSE("Import License Verification", false),
    TRADE_SANCTIONS_COMPLIANCE("Trade Sanctions Compliance", true),
    
    // Technology and Cybersecurity Compliance
    CYBERSECURITY_COMPLIANCE("Cybersecurity Standards Compliance", false),
    IT_INFRASTRUCTURE_AUDIT("IT Infrastructure Security Audit", false),
    
    // Labor and Employment Compliance
    EMPLOYMENT_LAW_COMPLIANCE("Employment Law Compliance", true),
    LABOR_STANDARDS_COMPLIANCE("Labor Standards Compliance", true),
    WORKER_SAFETY_TRAINING("Worker Safety Training Compliance", true);

    private final String description;
    private final boolean mandatory;

    ComplianceType(String description, boolean mandatory) {
        this.description = description;
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Check if compliance type is safety-related
     */
    public boolean isSafetyRelated() {
        return this == FIRE_SAFETY_COMPLIANCE || this == OCCUPATIONAL_SAFETY || 
               this == WORKER_SAFETY_TRAINING || this == HAZMAT_CERTIFICATION || 
               this == ENVIRONMENTAL_COMPLIANCE;
    }

    /**
     * Check if compliance type is industry-specific
     */
    public boolean isIndustrySpecific() {
        return this == FOOD_SAFETY_CERTIFICATION || this == PHARMACEUTICAL_COMPLIANCE || 
               this == HAZMAT_CERTIFICATION || this == CUSTOMS_BONDED_STATUS || 
               this == FREEPORT_LICENSE;
    }

    /**
     * Check if compliance type requires periodic renewal
     */
    public boolean requiresPeriodicRenewal() {
        return this == BUSINESS_LICENSE || this == WAREHOUSE_LICENSE || 
               this == FIRE_SAFETY_COMPLIANCE || this == INSURANCE_COVERAGE || 
               this == FOOD_SAFETY_CERTIFICATION || this == PHARMACEUTICAL_COMPLIANCE;
    }

    /**
     * Get typical validity period in months (0 means no expiry)
     */
    public int getTypicalValidityMonths() {
        return switch (this) {
            case BUSINESS_LICENSE, WAREHOUSE_LICENSE -> 12;
            case FIRE_SAFETY_COMPLIANCE, OCCUPATIONAL_SAFETY -> 12;
            case INSURANCE_COVERAGE -> 12;
            case FOOD_SAFETY_CERTIFICATION, PHARMACEUTICAL_COMPLIANCE -> 36;
            case ISO_CERTIFICATION -> 36;
            case WORKER_SAFETY_TRAINING -> 12;
            default -> 0; // No standard expiry
        };
    }
}