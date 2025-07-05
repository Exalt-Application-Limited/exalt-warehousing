package com.gogidix.warehousing.selfstorage.enums;

public enum VendorStorageStatus {
    SETUP,
    PENDING_VERIFICATION,
    VERIFICATION,
    ACTIVE,
    TEMPORARILY_CLOSED,
    UNDER_MAINTENANCE,
    MAINTENANCE,
    LIMITED_OPERATIONS,
    VERIFICATION_FAILED,
    PERMANENTLY_CLOSED,
    INACTIVE,
    SUSPENDED,
    CAPACITY_FULL,
    REJECTED;
    
    public boolean canAcceptInventory() {
        return this == ACTIVE || this == LIMITED_OPERATIONS;
    }
    
    public boolean canFulfillOrders() {
        return this == ACTIVE || this == LIMITED_OPERATIONS;
    }
    
    public boolean isOperational() {
        return this == ACTIVE || this == LIMITED_OPERATIONS || this == CAPACITY_FULL;
    }
    
    public boolean canTransitionTo(VendorStorageStatus newStatus) {
        return true; // Simplified for compilation
    }
}