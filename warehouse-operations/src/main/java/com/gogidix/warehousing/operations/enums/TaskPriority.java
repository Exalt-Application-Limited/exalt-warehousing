package com.gogidix.warehousing.operations.enums;

import lombok.Getter;

/**
 * Task Priority Enumeration
 * 
 * Defines the priority levels for operational tasks within warehouse
 * and vendor facility operations.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum TaskPriority {

    CRITICAL("Critical", "Requires immediate attention", 1),
    HIGH("High", "High priority task", 2),
    MEDIUM("Medium", "Standard priority task", 3),
    LOW("Low", "Low priority task", 4),
    BACKGROUND("Background", "Background task with no urgency", 5);

    private final String displayName;
    private final String description;
    private final int priorityLevel;

    TaskPriority(String displayName, String description, int priorityLevel) {
        this.displayName = displayName;
        this.description = description;
        this.priorityLevel = priorityLevel;
    }

    public boolean isUrgent() {
        return this == CRITICAL || this == HIGH;
    }

    public boolean canBeDelayed() {
        return this == LOW || this == BACKGROUND;
    }

    public boolean isStandard() {
        return this == MEDIUM;
    }

    public int getProcessingSortOrder() {
        return priorityLevel;
    }

    public static TaskPriority fromPriorityLevel(int level) {
        if (level <= 1) return CRITICAL;
        if (level == 2) return HIGH;
        if (level == 3) return MEDIUM;
        if (level == 4) return LOW;
        return BACKGROUND;
    }
}
