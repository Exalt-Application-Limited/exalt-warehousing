package com.gogidix.warehousing.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking subscription usage metrics
 */
@Entity
@Table(name = "subscription_usage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @NotNull
    @Column(name = "usage_date", nullable = false)
    private LocalDateTime usageDate;

    @NotNull
    @Column(name = "billing_period_start", nullable = false)
    private LocalDateTime billingPeriodStart;

    @NotNull
    @Column(name = "billing_period_end", nullable = false)
    private LocalDateTime billingPeriodEnd;

    // Storage Usage
    @PositiveOrZero
    @Column(name = "storage_used_cubic_meters", precision = 10, scale = 2)
    private BigDecimal storageUsedCubicMeters;

    @PositiveOrZero
    @Column(name = "storage_used_weight_kg", precision = 10, scale = 2)
    private BigDecimal storageUsedWeightKg;

    @PositiveOrZero
    @Column(name = "peak_storage_cubic_meters", precision = 10, scale = 2)
    private BigDecimal peakStorageCubicMeters;

    @PositiveOrZero
    @Column(name = "peak_storage_weight_kg", precision = 10, scale = 2)
    private BigDecimal peakStorageWeightKg;

    // Transaction Usage
    @PositiveOrZero
    @Column(name = "transactions_count")
    private Integer transactionsCount;

    @PositiveOrZero
    @Column(name = "inbound_transactions")
    private Integer inboundTransactions;

    @PositiveOrZero
    @Column(name = "outbound_transactions")
    private Integer outboundTransactions;

    @PositiveOrZero
    @Column(name = "transfer_transactions")
    private Integer transferTransactions;

    // API Usage
    @PositiveOrZero
    @Column(name = "api_calls_count")
    private Integer apiCallsCount;

    @PositiveOrZero
    @Column(name = "peak_api_calls_per_minute")
    private Integer peakApiCallsPerMinute;

    // Bandwidth Usage
    @PositiveOrZero
    @Column(name = "bandwidth_used_gb", precision = 10, scale = 3)
    private BigDecimal bandwidthUsedGb;

    // Storage Cost Calculation
    @PositiveOrZero
    @Column(name = "storage_cost", precision = 15, scale = 2)
    private BigDecimal storageCost;

    @PositiveOrZero
    @Column(name = "transaction_cost", precision = 15, scale = 2)
    private BigDecimal transactionCost;

    @PositiveOrZero
    @Column(name = "api_cost", precision = 15, scale = 2)
    private BigDecimal apiCost;

    @PositiveOrZero
    @Column(name = "bandwidth_cost", precision = 15, scale = 2)
    private BigDecimal bandwidthCost;

    @PositiveOrZero
    @Column(name = "total_usage_cost", precision = 15, scale = 2)
    private BigDecimal totalUsageCost;

    // Overage Flags
    @Builder.Default
    @Column(name = "storage_limit_exceeded", nullable = false)
    private Boolean storageLimitExceeded = false;

    @Builder.Default
    @Column(name = "transaction_limit_exceeded", nullable = false)
    private Boolean transactionLimitExceeded = false;

    @Builder.Default
    @Column(name = "api_limit_exceeded", nullable = false)
    private Boolean apiLimitExceeded = false;

    // Overage Costs
    @PositiveOrZero
    @Column(name = "storage_overage_cost", precision = 15, scale = 2)
    private BigDecimal storageOverageCost;

    @PositiveOrZero
    @Column(name = "transaction_overage_cost", precision = 15, scale = 2)
    private BigDecimal transactionOverageCost;

    @PositiveOrZero
    @Column(name = "api_overage_cost", precision = 15, scale = 2)
    private BigDecimal apiOverageCost;

    @PositiveOrZero
    @Column(name = "total_overage_cost", precision = 15, scale = 2)
    private BigDecimal totalOverageCost;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Calculate total cost including base usage and overages
     */
    @Transient
    public BigDecimal getTotalCost() {
        BigDecimal total = totalUsageCost != null ? totalUsageCost : BigDecimal.ZERO;
        BigDecimal overage = totalOverageCost != null ? totalOverageCost : BigDecimal.ZERO;
        return total.add(overage);
    }

    /**
     * Check if any limits were exceeded
     */
    @Transient
    public boolean hasOverages() {
        return storageLimitExceeded || transactionLimitExceeded || apiLimitExceeded;
    }

    /**
     * Calculate storage utilization percentage
     */
    @Transient
    public BigDecimal getStorageUtilizationPercentage(BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) == 0 || storageUsedCubicMeters == null) {
            return BigDecimal.ZERO;
        }
        return storageUsedCubicMeters.divide(limit, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate transaction utilization percentage
     */
    @Transient
    public BigDecimal getTransactionUtilizationPercentage(Integer limit) {
        if (limit == null || limit == 0 || transactionsCount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(transactionsCount)
                .divide(BigDecimal.valueOf(limit), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Update total usage cost
     */
    public void calculateTotalUsageCost() {
        BigDecimal storage = storageCost != null ? storageCost : BigDecimal.ZERO;
        BigDecimal transaction = transactionCost != null ? transactionCost : BigDecimal.ZERO;
        BigDecimal api = apiCost != null ? apiCost : BigDecimal.ZERO;
        BigDecimal bandwidth = bandwidthCost != null ? bandwidthCost : BigDecimal.ZERO;
        
        this.totalUsageCost = storage.add(transaction).add(api).add(bandwidth);
    }

    /**
     * Update total overage cost
     */
    public void calculateTotalOverageCost() {
        BigDecimal storage = storageOverageCost != null ? storageOverageCost : BigDecimal.ZERO;
        BigDecimal transaction = transactionOverageCost != null ? transactionOverageCost : BigDecimal.ZERO;
        BigDecimal api = apiOverageCost != null ? apiOverageCost : BigDecimal.ZERO;
        
        this.totalOverageCost = storage.add(transaction).add(api);
    }
}