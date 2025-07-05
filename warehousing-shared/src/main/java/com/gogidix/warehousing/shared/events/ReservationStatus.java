package com.gogidix.warehousing.shared.events;

/**
 * Enum representing the status of inventory reservations
 */
public enum ReservationStatus {
    PENDING("Reservation is pending"),
    CONFIRMED("Reservation is confirmed"),
    ALLOCATED("Inventory is allocated for reservation"),
    FULFILLED("Reservation has been fulfilled"),
    PARTIALLY_FULFILLED("Reservation is partially fulfilled"),
    CANCELLED("Reservation has been cancelled"),
    EXPIRED("Reservation has expired"),
    RELEASED("Reservation has been released"),
    FAILED("Reservation has failed");
    
    private final String description;
    
    ReservationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == ALLOCATED;
    }
    
    public boolean isTerminal() {
        return this == FULFILLED || this == CANCELLED || this == EXPIRED || this == FAILED;
    }
}
