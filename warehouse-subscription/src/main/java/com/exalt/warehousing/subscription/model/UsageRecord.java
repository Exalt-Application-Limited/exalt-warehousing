package com.exalt.warehousing.subscription.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing usage tracking records for subscription billing
 * 
 * This entity tracks detailed usage metrics for warehouse subscriptions
 * including storage, orders, API calls, and other metered services.
 */
@Entity
@Table(name = "usage_records", indexes = {
    @Index(name = "idx_usage_subscription_id", columnList = "subscription_id"),
    @Index(name = "idx_usage_date", columnList = "usage_date"),
    @Index(name = "idx_usage_type", columnList = "usage_type"),
    @Index(name = "idx_usage_billing_period", columnList = "billing_period_start, billing_period_end")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageRecord extends BaseEntity {

    /**
     * Reference to the subscription this usage belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usage_subscription"))
    @NotNull(message = "Subscription is required")
    private WarehouseSubscription subscription;

    /**
     * Type of usage being tracked
     */
    @NotNull(message = "Usage type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false, length = 50)
    private UsageType usageType;

    /**
     * Date when the usage occurred
     */
    @NotNull(message = "Usage date is required")
    @Column(name = "usage_date", nullable = false)
    private LocalDateTime usageDate;

    /**
     * Quantity of usage (e.g., number of orders, GB of storage, API calls)
     */
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be non-negative")
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    /**
     * Unit of measurement (e.g., "orders", "GB", "calls")
     */
    @NotNull(message = "Unit is required")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    /**
     * Rate per unit for billing calculation
     */
    @PositiveOrZero(message = "Rate must be non-negative")
    @Column(name = "rate_per_unit", precision = 10, scale = 6)
    private BigDecimal ratePerUnit;

    /**
     * Total amount for this usage record
     */
    @PositiveOrZero(message = "Amount must be non-negative")
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Start of the billing period this usage belongs to
     */
    @Column(name = "billing_period_start")
    private LocalDateTime billingPeriodStart;

    /**
     * End of the billing period this usage belongs to
     */
    @Column(name = "billing_period_end")
    private LocalDateTime billingPeriodEnd;

    /**
     * Whether this usage has been billed
     */
    @Column(name = "is_billed", nullable = false)
    private Boolean isBilled = false;

    /**
     * Date when this usage was billed
     */
    @Column(name = "billed_date")
    private LocalDateTime billedDate;

    /**
     * Additional metadata about the usage
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Description of the usage event
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Source system or service that generated this usage
     */
    @Column(name = "source", length = 100)
    private String source;

    /**
     * External reference ID from the source system
     */
    @Column(name = "external_reference_id", length = 255)
    private String externalReferenceId;

    /**
     * Enumeration of usage types
     */
    public enum UsageType {
        /**
         * Storage usage in cubic meters or GB
         */
        STORAGE("Storage", "Storage space used"),
        
        /**
         * Order processing
         */
        ORDERS("Orders", "Orders processed"),
        
        /**
         * API requests
         */
        API_REQUESTS("API Requests", "API calls made"),
        
        /**
         * Data transfer in GB
         */
        DATA_TRANSFER("Data Transfer", "Data transferred"),
        
        /**
         * User accounts
         */
        USER_ACCOUNTS("User Accounts", "Active user accounts"),
        
        /**
         * Additional services
         */
        ADDITIONAL_SERVICES("Additional Services", "Extra services used"),
        
        /**
         * Custom usage type
         */
        CUSTOM("Custom", "Custom usage metric");

        private final String displayName;
        private final String description;

        UsageType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Calculate the billable amount for this usage record
     * 
     * @return calculated amount
     */
    public BigDecimal calculateAmount() {
        if (ratePerUnit != null && quantity != null) {
            return ratePerUnit.multiply(quantity);
        }
        return amount != null ? amount : BigDecimal.ZERO;
    }

    /**
     * Mark this usage record as billed
     */
    public void markAsBilled() {
        this.isBilled = true;
        this.billedDate = LocalDateTime.now();
    }

    /**
     * Check if this usage is within the current billing period
     * 
     * @return true if within current billing period
     */
    public boolean isInCurrentBillingPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return billingPeriodStart != null && billingPeriodEnd != null &&
               !now.isBefore(billingPeriodStart) && now.isBefore(billingPeriodEnd);
    }

    /**
     * Check if this usage is overagge (exceeds plan limits)
     * 
     * @return true if this represents overage usage
     */
    public boolean isOverage() {
        return metadata != null && metadata.contains("overage=true");
    }

    /**
     * Set overage flag in metadata
     * 
     * @param isOverage whether this is overage usage
     */
    public void setOverage(boolean isOverage) {
        if (metadata == null) {
            metadata = "";
        }
        
        // Remove existing overage flag
        metadata = metadata.replaceAll("overage=(true|false);?", "");
        
        // Add new overage flag
        if (!metadata.isEmpty() && !metadata.endsWith(";")) {
            metadata += ";";
        }
        metadata += "overage=" + isOverage;
    }

    /**
     * Get usage summary description
     * 
     * @return formatted usage summary
     */
    public String getUsageSummary() {
        return String.format("%s %s of %s on %s",
                quantity.stripTrailingZeros().toPlainString(),
                unit,
                usageType.getDisplayName(),
                usageDate.toLocalDate());
    }
}