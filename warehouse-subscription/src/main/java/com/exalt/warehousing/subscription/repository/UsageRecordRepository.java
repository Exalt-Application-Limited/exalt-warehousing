package com.exalt.warehousing.subscription.repository;

import com.exalt.warehousing.subscription.model.UsageRecord;
import com.exalt.warehousing.subscription.model.WarehouseSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Repository interface for UsageRecord entities
 */
@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, String> {

    /**
     * Find usage records by subscription
     */
    Page<UsageRecord> findBySubscription(WarehouseSubscription subscription, Pageable pageable);

    /**
     * Find usage records by subscription and date range
     */
    Page<UsageRecord> findBySubscriptionAndUsageDateBetween(WarehouseSubscription subscription,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate,
                                                           Pageable pageable);

    /**
     * Find usage records by subscription and usage type
     */
    List<UsageRecord> findBySubscriptionAndUsageType(WarehouseSubscription subscription,
                                                    UsageRecord.UsageType usageType);

    /**
     * Find unbilled usage records for billing period
     */
    @Query("SELECT u FROM UsageRecord u WHERE u.subscription = :subscription AND " +
           "u.billingPeriodStart = :periodStart AND u.billingPeriodEnd = :periodEnd AND " +
           "u.isBilled = :isBilled")
    List<UsageRecord> findBySubscriptionAndBillingPeriodAndIsBilled(@Param("subscription") WarehouseSubscription subscription,
                                                                   @Param("periodStart") LocalDateTime periodStart,
                                                                   @Param("periodEnd") LocalDateTime periodEnd,
                                                                   @Param("isBilled") boolean isBilled);

    /**
     * Find usage records by billing period
     */
    List<UsageRecord> findByBillingPeriodStartAndBillingPeriodEnd(LocalDateTime periodStart, LocalDateTime periodEnd);

    /**
     * Find unbilled usage records
     */
    List<UsageRecord> findByIsBilledFalse();

    /**
     * Calculate total usage amount for subscription in period
     */
    @Query("SELECT COALESCE(SUM(u.amount), 0) FROM UsageRecord u WHERE u.subscription = :subscription AND " +
           "u.usageDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalUsageAmount(@Param("subscription") WarehouseSubscription subscription,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate total usage quantity by type for subscription
     */
    @Query("SELECT COALESCE(SUM(u.quantity), 0) FROM UsageRecord u WHERE u.subscription = :subscription AND " +
           "u.usageType = :usageType AND u.usageDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalUsageQuantity(@Param("subscription") WarehouseSubscription subscription,
                                         @Param("usageType") UsageRecord.UsageType usageType,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find usage records by external reference
     */
    List<UsageRecord> findByExternalReferenceId(String externalReferenceId);

    /**
     * Find usage records by source
     */
    List<UsageRecord> findBySource(String source);

    /**
     * Count usage records by subscription and type
     */
    long countBySubscriptionAndUsageType(WarehouseSubscription subscription, UsageRecord.UsageType usageType);

    /**
     * Find recent usage records
     */
    @Query("SELECT u FROM UsageRecord u WHERE u.usageDate >= :since ORDER BY u.usageDate DESC")
    List<UsageRecord> findRecentUsage(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find overage usage records
     */
    @Query("SELECT u FROM UsageRecord u WHERE u.metadata LIKE '%overage=true%'")
    List<UsageRecord> findOverageUsage();

    /**
     * Delete old usage records
     */
    void deleteByUsageDateBefore(LocalDateTime cutoffDate);
}
