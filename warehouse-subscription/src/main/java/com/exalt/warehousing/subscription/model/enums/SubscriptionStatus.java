package com.exalt.warehousing.subscription.model.enums;

import lombok.Getter;

/**
 * Enumeration of subscription statuses
 * 
 * This enum defines the various states a warehouse subscription can be in
 * throughout its lifecycle, from trial through active use to cancellation.
 */
@Getter
public enum SubscriptionStatus {
    
    /**
     * Trial period - free evaluation of services
     * Features: Limited access, trial duration tracking, conversion monitoring
     */
    TRIAL("Trial", "Free trial period with limited access", true, false, true),
    
    /**
     * Pending activation - awaiting payment or approval
     * Features: Payment processing, setup completion, activation pending
     */
    PENDING("Pending", "Subscription pending activation", false, false, false),
    
    /**
     * Active subscription - full service access
     * Features: Complete access, billing active, all features enabled
     */
    ACTIVE("Active", "Active subscription with full access", true, true, true),
    
    /**
     * Suspended - temporarily disabled due to payment issues
     * Features: Limited access, payment retry, grace period
     */
    SUSPENDED("Suspended", "Suspended due to payment or policy issues", false, true, false),
    
    /**
     * Past due - payment failed but still in grace period
     * Features: Limited access, payment reminders, dunning management
     */
    PAST_DUE("Past Due", "Payment overdue but still in grace period", true, true, false),
    
    /**
     * Cancelled - subscription terminated by user
     * Features: Access revoked, data retention period, reactivation options
     */
    CANCELLED("Cancelled", "Cancelled by customer", false, false, false),
    
    /**
     * Expired - subscription term ended without renewal
     * Features: Access revoked, renewal options, data archival
     */
    EXPIRED("Expired", "Subscription term expired", false, false, false),
    
    /**
     * Paused - temporarily suspended by user request
     * Features: Voluntary suspension, billing paused, reactivation ready
     */
    PAUSED("Paused", "Temporarily paused by customer", false, false, false);

    /**
     * Human-readable display name
     */
    private final String displayName;
    
    /**
     * Detailed description of the status
     */
    private final String description;
    
    /**
     * Whether services are accessible in this status
     */
    private final boolean servicesAccessible;
    
    /**
     * Whether billing is active for this status
     */
    private final boolean billingActive;
    
    /**
     * Whether new usage can be accrued in this status
     */
    private final boolean usageTrackingActive;

    /**
     * Constructor for SubscriptionStatus enum
     * 
     * @param displayName human-readable name
     * @param description detailed description
     * @param servicesAccessible whether services can be accessed
     * @param billingActive whether billing is active
     * @param usageTrackingActive whether usage tracking is active
     */
    SubscriptionStatus(String displayName, String description, boolean servicesAccessible, 
                      boolean billingActive, boolean usageTrackingActive) {
        this.displayName = displayName;
        this.description = description;
        this.servicesAccessible = servicesAccessible;
        this.billingActive = billingActive;
        this.usageTrackingActive = usageTrackingActive;
    }

    /**
     * Check if the subscription allows full service access
     * 
     * @return true if full access is allowed
     */
    public boolean allowsFullAccess() {
        return this == ACTIVE;
    }

    /**
     * Check if the subscription has any access restrictions
     * 
     * @return true if access is restricted
     */
    public boolean hasAccessRestrictions() {
        return this == TRIAL || this == PAST_DUE;
    }

    /**
     * Check if the subscription is in a problematic state
     * 
     * @return true if subscription needs attention
     */
    public boolean requiresAttention() {
        return this == SUSPENDED || this == PAST_DUE || this == PENDING;
    }

    /**
     * Check if the subscription is terminated
     * 
     * @return true if subscription is ended
     */
    public boolean isTerminated() {
        return this == CANCELLED || this == EXPIRED;
    }

    /**
     * Check if the subscription can be reactivated
     * 
     * @return true if reactivation is possible
     */
    public boolean canBeReactivated() {
        return this == PAUSED || this == SUSPENDED || this == PAST_DUE || this == EXPIRED;
    }

    /**
     * Get valid transition states from the current status
     * 
     * @return array of valid next statuses
     */
    public SubscriptionStatus[] getValidTransitions() {
        switch (this) {
            case TRIAL:
                return new SubscriptionStatus[]{ACTIVE, CANCELLED, EXPIRED};
            case PENDING:
                return new SubscriptionStatus[]{ACTIVE, CANCELLED, SUSPENDED};
            case ACTIVE:
                return new SubscriptionStatus[]{SUSPENDED, PAST_DUE, CANCELLED, PAUSED, EXPIRED};
            case SUSPENDED:
                return new SubscriptionStatus[]{ACTIVE, CANCELLED, EXPIRED};
            case PAST_DUE:
                return new SubscriptionStatus[]{ACTIVE, SUSPENDED, CANCELLED};
            case CANCELLED:
                return new SubscriptionStatus[]{ACTIVE}; // Reactivation possible
            case EXPIRED:
                return new SubscriptionStatus[]{ACTIVE}; // Renewal possible
            case PAUSED:
                return new SubscriptionStatus[]{ACTIVE, CANCELLED};
            default:
                return new SubscriptionStatus[]{};
        }
    }

    /**
     * Check if transition to another status is valid
     * 
     * @param targetStatus the status to transition to
     * @return true if the transition is valid
     */
    public boolean canTransitionTo(SubscriptionStatus targetStatus) {
        SubscriptionStatus[] validTransitions = getValidTransitions();
        for (SubscriptionStatus validStatus : validTransitions) {
            if (validStatus == targetStatus) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the priority level for this status (for alerts and notifications)
     * 
     * @return priority level (1 = highest, 5 = lowest)
     */
    public int getPriorityLevel() {
        switch (this) {
            case SUSPENDED:
            case PAST_DUE:
                return 1; // Critical
            case PENDING:
                return 2; // High
            case EXPIRED:
                return 3; // Medium
            case TRIAL:
                return 4; // Low
            case ACTIVE:
            case CANCELLED:
            case PAUSED:
            default:
                return 5; // Informational
        }
    }

    /**
     * Get the color code for UI representation
     * 
     * @return color code for status visualization
     */
    public String getColorCode() {
        switch (this) {
            case ACTIVE:
                return "#28a745"; // Green
            case TRIAL:
                return "#17a2b8"; // Blue
            case PENDING:
                return "#ffc107"; // Yellow
            case PAST_DUE:
                return "#fd7e14"; // Orange
            case SUSPENDED:
                return "#dc3545"; // Red
            case PAUSED:
                return "#6f42c1"; // Purple
            case CANCELLED:
            case EXPIRED:
                return "#6c757d"; // Gray
            default:
                return "#6c757d"; // Default gray
        }
    }

    /**
     * Get the service access level for this status
     * 
     * @return service access level percentage (0-100)
     */
    public int getServiceAccessLevel() {
        switch (this) {
            case ACTIVE:
                return 100; // Full access
            case TRIAL:
            case PAST_DUE:
                return 75; // Limited access
            case PENDING:
                return 25; // Minimal access
            case SUSPENDED:
            case PAUSED:
            case CANCELLED:
            case EXPIRED:
            default:
                return 0; // No access
        }
    }

    /**
     * Get billing frequency applicability for this status
     * 
     * @return true if billing frequency applies
     */
    public boolean supportsBillingFrequency() {
        return billingActive;
    }

    /**
     * Get usage limit enforcement for this status
     * 
     * @return true if usage limits should be enforced
     */
    public boolean enforceUsageLimits() {
        return this == TRIAL || this == PAST_DUE;
    }
}