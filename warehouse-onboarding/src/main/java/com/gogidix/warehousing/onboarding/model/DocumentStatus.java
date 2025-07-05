package com.gogidix.warehousing.onboarding.model;

/**
 * Document Status Enumeration
 * 
 * Represents the verification status of uploaded documents
 */
public enum DocumentStatus {
    UPLOADED("Document uploaded, pending verification"),
    PROCESSING("Document being processed and validated"),
    REQUIRES_REVIEW("Document requires manual review"),
    VERIFIED("Document verified and approved"),
    REJECTED("Document rejected - resubmission required"),
    EXPIRED("Document has expired and needs renewal");

    private final String description;

    DocumentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if document status is final
     */
    public boolean isFinal() {
        return this == VERIFIED || this == REJECTED || this == EXPIRED;
    }

    /**
     * Check if document status is successful
     */
    public boolean isSuccessful() {
        return this == VERIFIED;
    }

    /**
     * Check if document status requires action
     */
    public boolean requiresAction() {
        return this == REQUIRES_REVIEW || this == REJECTED || this == EXPIRED;
    }

    /**
     * Check if document can be resubmitted
     */
    public boolean canResubmit() {
        return this == REJECTED || this == EXPIRED;
    }
}