package com.exalt.warehousing.operations.enums;

import lombok.Getter;

/**
 * Maintenance Frequency Enumeration
 * 
 * Defines the frequency intervals for equipment maintenance scheduling.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Getter
public enum MaintenanceFrequency {

    DAILY("Daily", "Maintenance required daily", 1),
    WEEKLY("Weekly", "Maintenance required weekly", 7),
    BIWEEKLY("Biweekly", "Maintenance required every two weeks", 14),
    MONTHLY("Monthly", "Maintenance required monthly", 30),
    QUARTERLY("Quarterly", "Maintenance required every three months", 90),
    SEMIANNUAL("Semiannual", "Maintenance required twice a year", 182),
    ANNUAL("Annual", "Maintenance required annually", 365),
    BIENNIAL("Biennial", "Maintenance required every two years", 730),
    USAGE_BASED("Usage Based", "Maintenance based on usage metrics", 0),
    AS_NEEDED("As Needed", "Maintenance performed as needed", 0);

    private final String displayName;
    private final String description;
    private final int standardDaysBetween;

    MaintenanceFrequency(String displayName, String description, int standardDaysBetween) {
        this.displayName = displayName;
        this.description = description;
        this.standardDaysBetween = standardDaysBetween;
    }

    public boolean isRegularSchedule() {
        return this != USAGE_BASED && this != AS_NEEDED;
    }

    public boolean isHighFrequency() {
        return this == DAILY || this == WEEKLY || this == BIWEEKLY;
    }

    public boolean isMediumFrequency() {
        return this == MONTHLY || this == QUARTERLY;
    }

    public boolean isLowFrequency() {
        return this == SEMIANNUAL || this == ANNUAL || this == BIENNIAL;
    }

    public boolean isFlexibleSchedule() {
        return this == USAGE_BASED || this == AS_NEEDED;
    }

    public int getDaysToNextMaintenance(int daysSinceLastMaintenance) {
        if (!isRegularSchedule()) {
            return -1; // Not applicable for flexible schedules
        }
        return Math.max(0, standardDaysBetween - daysSinceLastMaintenance);
    }

    public boolean isMaintenanceOverdue(int daysSinceLastMaintenance) {
        if (!isRegularSchedule()) {
            return false; // Can't be overdue for flexible schedules
        }
        return daysSinceLastMaintenance > standardDaysBetween;
    }

    public double getCompliancePercentage(int daysSinceLastMaintenance) {
        if (!isRegularSchedule() || standardDaysBetween == 0) {
            return 100.0; // Always compliant for flexible schedules
        }
        
        double percentage = 100.0 * (1.0 - ((double) daysSinceLastMaintenance / standardDaysBetween));
        return Math.max(0.0, Math.min(100.0, percentage));
    }
}
