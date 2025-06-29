package com.exalt.warehousing.onboarding.model;

import java.util.Set;

/**
 * Document Type Enumeration
 * 
 * Represents different types of documents required for onboarding
 */
public enum DocumentType {
    // Legal and Business Documents
    BUSINESS_REGISTRATION("Business Registration Certificate", true, Set.of("pdf", "jpg", "png")),
    ARTICLES_OF_INCORPORATION("Articles of Incorporation", true, Set.of("pdf", "jpg", "png")),
    TAX_IDENTIFICATION("Tax Identification Document", true, Set.of("pdf", "jpg", "png")),
    BUSINESS_LICENSE("Business License", true, Set.of("pdf", "jpg", "png")),
    OPERATING_AGREEMENT("Operating Agreement", false, Set.of("pdf")),
    BOARD_RESOLUTION("Board Resolution", false, Set.of("pdf", "jpg", "png")),
    
    // Financial Documents
    BANK_STATEMENTS("Bank Statements (Last 3 months)", true, Set.of("pdf")),
    FINANCIAL_STATEMENTS("Financial Statements", true, Set.of("pdf", "xlsx")),
    CREDIT_REPORT("Credit Report", false, Set.of("pdf")),
    INSURANCE_CERTIFICATE("Insurance Certificate", true, Set.of("pdf", "jpg", "png")),
    
    // Identity Documents
    OWNER_ID_PROOF("Owner Identity Proof", true, Set.of("pdf", "jpg", "png")),
    AUTHORIZED_SIGNATORY_ID("Authorized Signatory ID", true, Set.of("pdf", "jpg", "png")),
    PASSPORT("Passport", false, Set.of("pdf", "jpg", "png")),
    DRIVER_LICENSE("Driver's License", false, Set.of("pdf", "jpg", "png")),
    
    // Warehouse and Facility Documents
    WAREHOUSE_LEASE_AGREEMENT("Warehouse Lease Agreement", true, Set.of("pdf")),
    PROPERTY_OWNERSHIP_PROOF("Property Ownership Proof", false, Set.of("pdf", "jpg", "png")),
    FACILITY_PHOTOS("Facility Photos", true, Set.of("jpg", "png", "pdf")),
    FLOOR_PLAN("Warehouse Floor Plan", true, Set.of("pdf", "jpg", "png", "dwg")),
    SECURITY_SYSTEM_CERTIFICATE("Security System Certificate", false, Set.of("pdf", "jpg", "png")),
    FIRE_SAFETY_CERTIFICATE("Fire Safety Certificate", true, Set.of("pdf", "jpg", "png")),
    
    // Compliance and Certifications
    ISO_CERTIFICATION("ISO Certification", false, Set.of("pdf", "jpg", "png")),
    HACCP_CERTIFICATE("HACCP Certificate", false, Set.of("pdf", "jpg", "png")),
    FDA_REGISTRATION("FDA Registration", false, Set.of("pdf", "jpg", "png")),
    PHARMACEUTICAL_LICENSE("Pharmaceutical License", false, Set.of("pdf", "jpg", "png")),
    CUSTOMS_CERTIFICATION("Customs Certification", false, Set.of("pdf", "jpg", "png")),
    ENVIRONMENTAL_PERMIT("Environmental Permit", false, Set.of("pdf", "jpg", "png")),
    
    // Technology and Systems
    WMS_CERTIFICATE("WMS System Certificate", false, Set.of("pdf", "jpg", "png")),
    INVENTORY_SYSTEM_DOCS("Inventory System Documentation", false, Set.of("pdf", "docx")),
    INTEGRATION_SPECS("Integration Specifications", false, Set.of("pdf", "docx", "xlsx")),
    
    // References and Agreements
    CLIENT_REFERENCES("Client References", false, Set.of("pdf", "docx")),
    VENDOR_AGREEMENTS("Vendor Agreements", false, Set.of("pdf")),
    SERVICE_LEVEL_AGREEMENT("Service Level Agreement", false, Set.of("pdf")),
    
    // Other
    ADDITIONAL_DOCUMENTATION("Additional Documentation", false, Set.of("pdf", "jpg", "png", "docx"));

    private final String displayName;
    private final boolean required;
    private final Set<String> allowedFileTypes;

    DocumentType(String displayName, boolean required, Set<String> allowedFileTypes) {
        this.displayName = displayName;
        this.required = required;
        this.allowedFileTypes = allowedFileTypes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequired() {
        return required;
    }

    public Set<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    /**
     * Check if file type is allowed for this document type
     */
    public boolean isFileTypeAllowed(String fileType) {
        return allowedFileTypes.contains(fileType.toLowerCase());
    }

    /**
     * Check if document type is identity-related
     */
    public boolean isIdentityDocument() {
        return this == OWNER_ID_PROOF || this == AUTHORIZED_SIGNATORY_ID || 
               this == PASSPORT || this == DRIVER_LICENSE;
    }

    /**
     * Check if document type is financial-related
     */
    public boolean isFinancialDocument() {
        return this == BANK_STATEMENTS || this == FINANCIAL_STATEMENTS || 
               this == CREDIT_REPORT || this == INSURANCE_CERTIFICATE;
    }

    /**
     * Check if document type is facility-related
     */
    public boolean isFacilityDocument() {
        return this == WAREHOUSE_LEASE_AGREEMENT || this == PROPERTY_OWNERSHIP_PROOF || 
               this == FACILITY_PHOTOS || this == FLOOR_PLAN || 
               this == SECURITY_SYSTEM_CERTIFICATE || this == FIRE_SAFETY_CERTIFICATE;
    }

    /**
     * Check if document type is compliance-related
     */
    public boolean isComplianceDocument() {
        return this == ISO_CERTIFICATION || this == HACCP_CERTIFICATE || 
               this == FDA_REGISTRATION || this == PHARMACEUTICAL_LICENSE || 
               this == CUSTOMS_CERTIFICATION || this == ENVIRONMENTAL_PERMIT;
    }
}