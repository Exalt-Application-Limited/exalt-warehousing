package com.gogidix.warehousing.onboarding.model;

/**
 * Onboarding Status Enumeration
 * 
 * Represents the current status of a partner onboarding request
 */
public enum OnboardingStatus {
    SUBMITTED("Application submitted and pending initial review"),
    UNDER_REVIEW("Application under review by onboarding team"),
    DOCUMENT_VERIFICATION("Documents being verified"),
    KYC_IN_PROGRESS("KYC verification in progress"),
    COMPLIANCE_CHECK("Compliance and regulatory checks in progress"),
    FACILITY_INSPECTION_REQUIRED("Physical facility inspection required"),
    FACILITY_INSPECTION_SCHEDULED("Facility inspection scheduled"),
    FACILITY_INSPECTION_COMPLETED("Facility inspection completed"),
    PENDING_APPROVAL("Pending final approval from management"),
    APPROVED("Application approved - partner onboarded"),
    REJECTED("Application rejected"),
    SUSPENDED("Application suspended - additional information required"),
    WITHDRAWN("Application withdrawn by applicant"),
    EXPIRED("Application expired due to inactivity");

    private final String description;

    OnboardingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if status represents a terminal state
     */
    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED || this == WITHDRAWN || this == EXPIRED;
    }

    /**
     * Check if status allows for updates
     */
    public boolean isEditable() {
        return !isTerminal() && this != UNDER_REVIEW;
    }

    /**
     * Get next possible statuses
     */
    public OnboardingStatus[] getNextPossibleStatuses() {
        return switch (this) {
            case SUBMITTED -> new OnboardingStatus[]{UNDER_REVIEW, REJECTED, WITHDRAWN};
            case UNDER_REVIEW -> new OnboardingStatus[]{DOCUMENT_VERIFICATION, SUSPENDED, REJECTED};
            case DOCUMENT_VERIFICATION -> new OnboardingStatus[]{KYC_IN_PROGRESS, SUSPENDED, REJECTED};
            case KYC_IN_PROGRESS -> new OnboardingStatus[]{COMPLIANCE_CHECK, SUSPENDED, REJECTED};
            case COMPLIANCE_CHECK -> new OnboardingStatus[]{FACILITY_INSPECTION_REQUIRED, PENDING_APPROVAL, SUSPENDED, REJECTED};
            case FACILITY_INSPECTION_REQUIRED -> new OnboardingStatus[]{FACILITY_INSPECTION_SCHEDULED, SUSPENDED, REJECTED};
            case FACILITY_INSPECTION_SCHEDULED -> new OnboardingStatus[]{FACILITY_INSPECTION_COMPLETED, SUSPENDED, REJECTED};
            case FACILITY_INSPECTION_COMPLETED -> new OnboardingStatus[]{PENDING_APPROVAL, SUSPENDED, REJECTED};
            case PENDING_APPROVAL -> new OnboardingStatus[]{APPROVED, REJECTED, SUSPENDED};
            case SUSPENDED -> new OnboardingStatus[]{UNDER_REVIEW, REJECTED, WITHDRAWN};
            default -> new OnboardingStatus[]{};
        };
    }
}