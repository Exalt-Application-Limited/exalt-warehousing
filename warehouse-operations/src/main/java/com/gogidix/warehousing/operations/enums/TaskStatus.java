package com.gogidix.warehousing.operations.enums;

import lombok.Getter;

/**
 * Task Status Enumeration
 * 
 * Defines the possible states for operational tasks within warehouse
 * and vendor facility operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum TaskStatus {

    PENDING("Pending", "Task is created but not yet started"),
    IN_PROGRESS("In Progress", "Task is currently being worked on"),
    PAUSED("Paused", "Task has been temporarily paused"),
    COMPLETED("Completed", "Task has been successfully completed"),
    CANCELLED("Cancelled", "Task has been cancelled before completion"),
    BLOCKED("Blocked", "Task is blocked by dependencies or constraints"),
    FAILED("Failed", "Task could not be completed due to errors"),
    DELEGATED("Delegated", "Task has been reassigned to another staff member"),
    VERIFIED("Verified", "Task has been completed and verified");

    private final String displayName;
    private final String description;

    TaskStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == PAUSED;
    }

    public boolean isCompleted() {
        return this == COMPLETED || this == VERIFIED;
    }

    public boolean isTerminated() {
        return this == CANCELLED || this == FAILED;
    }

    public boolean canStart() {
        return this == PENDING || this == PAUSED;
    }

    public boolean canResume() {
        return this == PAUSED;
    }

    public boolean canPause() {
        return this == IN_PROGRESS;
    }

    public boolean canComplete() {
        return this == IN_PROGRESS;
    }

    public boolean canCancel() {
        return isActive();
    }

    public boolean needsAttention() {
        return this == FAILED || this == BLOCKED;
    }

    public boolean canBeVerified() {
        return this == COMPLETED;
    }

    public boolean canBeReassigned() {
        return isActive();
    }

    public boolean isSuccessful() {
        return this == COMPLETED || this == VERIFIED;
    }
}
