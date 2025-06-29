package com.exalt.warehousing.operations.enums;

import lombok.Getter;

/**
 * Staff Assignment Status Enumeration
 * 
 * Defines the possible states for staff assignments in warehouse operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum AssignmentStatus {

    SCHEDULED("Scheduled", "Assignment has been scheduled but not yet started"),
    IN_PROGRESS("In Progress", "Assignment is currently in progress"),
    COMPLETED("Completed", "Assignment has been successfully completed"),
    CANCELLED("Cancelled", "Assignment has been cancelled"),
    PENDING_APPROVAL("Pending Approval", "Assignment requires approval before scheduling"),
    REASSIGNED("Reassigned", "Assignment has been transferred to another staff member"),
    DELAYED("Delayed", "Assignment has been delayed"),
    ON_HOLD("On Hold", "Assignment is temporarily paused");

    private final String displayName;
    private final String description;

    AssignmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == IN_PROGRESS || this == ON_HOLD;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isTerminated() {
        return this == CANCELLED || this == REASSIGNED;
    }

    public boolean isPending() {
        return this == PENDING_APPROVAL || this == SCHEDULED || this == DELAYED;
    }

    public boolean canStart() {
        return this == SCHEDULED;
    }

    public boolean canComplete() {
        return this == IN_PROGRESS;
    }

    public boolean canReassign() {
        return isActive() || isPending();
    }

    public boolean canCancel() {
        return isActive() || isPending();
    }
}
