package com.gogidix.warehousing.billing.repository;

import com.gogidix.warehousing.billing.model.Subscription;
import com.gogidix.warehousing.billing.model.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Subscription entity
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /**
     * Find subscriptions by billing account
     */
    List<Subscription> findByBillingAccount_Id(UUID billingAccountId);

    /**
     * Find subscription by warehouse ID
     */
    Optional<Subscription> findByWarehouseId(UUID warehouseId);

    /**
     * Find active subscription by warehouse ID
     */
    @Query("SELECT s FROM Subscription s WHERE s.warehouseId = :warehouseId AND s.subscriptionStatus = 'ACTIVE'")
    Optional<Subscription> findActiveSubscriptionByWarehouseId(@Param("warehouseId") UUID warehouseId);

    /**
     * Find subscriptions by status
     */
    Page<Subscription> findBySubscriptionStatus(SubscriptionStatus status, Pageable pageable);

    /**
     * Find subscriptions by plan type
     */
    List<Subscription> findByPlanType(String planType);

    /**
     * Find subscriptions by plan name
     */
    List<Subscription> findByPlanName(String planName);

    /**
     * Find subscriptions due for billing
     */
    List<Subscription> findByNextBillingDateBeforeAndSubscriptionStatus(
            LocalDateTime date, SubscriptionStatus status);

    /**
     * Find subscriptions in trial period
     */
    @Query("SELECT s FROM Subscription s WHERE s.trialEndDate IS NOT NULL AND s.trialEndDate > CURRENT_TIMESTAMP")
    List<Subscription> findSubscriptionsInTrial();

    /**
     * Find subscriptions with trial ending soon
     */
    @Query("SELECT s FROM Subscription s WHERE s.trialEndDate IS NOT NULL AND " +
           "s.trialEndDate BETWEEN CURRENT_TIMESTAMP AND :endDate")
    List<Subscription> findSubscriptionsWithTrialEndingSoon(@Param("endDate") LocalDateTime endDate);

    /**
     * Find expired subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.endDate IS NOT NULL AND s.endDate < CURRENT_TIMESTAMP")
    List<Subscription> findExpiredSubscriptions();

    /**
     * Find subscriptions due for renewal
     */
    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = true AND s.nextBillingDate < :date")
    List<Subscription> findSubscriptionsDueForRenewal(@Param("date") LocalDateTime date);

    /**
     * Find subscriptions by currency
     */
    List<Subscription> findByCurrency(String currency);

    /**
     * Find subscriptions created in date range
     */
    List<Subscription> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find subscriptions ending in date range
     */
    List<Subscription> findByEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get count of subscriptions by status
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.subscriptionStatus = :status")
    Long getCountByStatus(@Param("status") SubscriptionStatus status);

    /**
     * Get count of active subscriptions
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.subscriptionStatus = 'ACTIVE'")
    Long getActiveSubscriptionCount();

    /**
     * Find subscriptions with auto-renew enabled
     */
    List<Subscription> findByAutoRenewTrue();

    /**
     * Find subscriptions with auto-renew disabled
     */
    List<Subscription> findByAutoRenewFalse();

    /**
     * Find subscriptions by billing account and status
     */
    List<Subscription> findByBillingAccountIdAndSubscriptionStatus(
            UUID billingAccountId, SubscriptionStatus status);

    /**
     * Search subscriptions by multiple criteria
     */
    @Query("SELECT s FROM Subscription s WHERE " +
           "(:planType IS NULL OR s.planType = :planType) AND " +
           "(:status IS NULL OR s.subscriptionStatus = :status) AND " +
           "(:currency IS NULL OR s.currency = :currency) AND " +
           "(:warehouseId IS NULL OR s.warehouseId = :warehouseId)")
    Page<Subscription> searchSubscriptions(
            @Param("planType") String planType,
            @Param("status") SubscriptionStatus status,
            @Param("currency") String currency,
            @Param("warehouseId") UUID warehouseId,
            Pageable pageable);

    /**
     * Find subscriptions needing billing reminder
     */
    @Query("SELECT s FROM Subscription s WHERE " +
           "s.subscriptionStatus = 'ACTIVE' AND " +
           "s.nextBillingDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsNeedingBillingReminder(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find past due subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE " +
           "s.subscriptionStatus = 'PAST_DUE' AND " +
           "s.nextBillingDate < :cutoffDate")
    List<Subscription> findPastDueSubscriptions(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Update subscription status
     */
    @Query("UPDATE Subscription s SET s.subscriptionStatus = :status WHERE s.id = :id")
    void updateSubscriptionStatus(@Param("id") UUID id, @Param("status") SubscriptionStatus status);

    /**
     * Update next billing date
     */
    @Query("UPDATE Subscription s SET s.nextBillingDate = :nextBillingDate, s.lastBillingDate = :lastBillingDate WHERE s.id = :id")
    void updateBillingDates(@Param("id") UUID id, 
                           @Param("nextBillingDate") LocalDateTime nextBillingDate,
                           @Param("lastBillingDate") LocalDateTime lastBillingDate);

    /**
     * Find subscriptions by features enabled
     */
    @Query("SELECT s FROM Subscription s WHERE " +
           "(:analyticsEnabled IS NULL OR s.analyticsEnabled = :analyticsEnabled) AND " +
           "(:prioritySupport IS NULL OR s.prioritySupport = :prioritySupport) AND " +
           "(:customIntegrations IS NULL OR s.customIntegrations = :customIntegrations) AND " +
           "(:whiteLabel IS NULL OR s.whiteLabel = :whiteLabel) AND " +
           "(:multiWarehouse IS NULL OR s.multiWarehouse = :multiWarehouse)")
    List<Subscription> findByFeatures(
            @Param("analyticsEnabled") Boolean analyticsEnabled,
            @Param("prioritySupport") Boolean prioritySupport,
            @Param("customIntegrations") Boolean customIntegrations,
            @Param("whiteLabel") Boolean whiteLabel,
            @Param("multiWarehouse") Boolean multiWarehouse);

    /**
     * Get subscriptions by billing account with pagination
     */
    Page<Subscription> findByBillingAccountId(UUID billingAccountId, Pageable pageable);

    /**
     * Find recently cancelled subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.subscriptionStatus = 'CANCELLED' AND " +
           "s.cancellationDate >= :cutoffDate")
    List<Subscription> findRecentlyCancelledSubscriptions(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Get subscription revenue by period
     */
    @Query("SELECT COALESCE(SUM(s.monthlyFee), 0) FROM Subscription s WHERE " +
           "s.subscriptionStatus = 'ACTIVE' AND s.currency = :currency")
    java.math.BigDecimal getActiveSubscriptionRevenue(@Param("currency") String currency);
}