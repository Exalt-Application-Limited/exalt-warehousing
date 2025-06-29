package com.exalt.warehousing.operations.enums;

import lombok.Getter;

/**
 * Warehouse Layout Status Enumeration
 * 
 * Defines the possible states for warehouse and vendor facility layouts.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum LayoutStatus {

    DRAFT("Draft", "Layout is in draft state and not yet validated"),
    PENDING_APPROVAL("Pending Approval", "Layout has been submitted for approval"),
    APPROVED("Approved", "Layout has been approved but not yet active"),
    ACTIVE("Active", "Layout is currently active"),
    INACTIVE("Inactive", "Layout is no longer active"),
    ARCHIVED("Archived", "Layout has been archived for historical reference"),
    SUPERSEDED("Superseded", "Layout has been replaced by a newer version");

    private final String displayName;
    private final String description;

    LayoutStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isEditable() {
        return this == DRAFT;
    }

    public boolean isUsable() {
        return this == ACTIVE || this == APPROVED;
    }

    public boolean isTerminal() {
        return this == ARCHIVED || this == SUPERSEDED;
    }
}
