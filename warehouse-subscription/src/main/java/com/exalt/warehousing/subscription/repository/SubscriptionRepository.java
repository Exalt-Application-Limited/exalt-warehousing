package com.exalt.warehousing.subscription.repository;

import com.exalt.warehousing.subscription.model.WarehouseSubscription;
import com.exalt.warehousing.subscription.model.enums.SubscriptionPlan;
import com.exalt.warehousing.subscription.model.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Repository interface for WarehouseSubscription entities
 * 
 * This repository provides data access methods for subscription management
 * including CRUD operations, complex queries, and business-specific searches.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<WarehouseSubscription, String> {

    /**
     * Find subscription by warehouse ID
     * 
     * @param warehouseId the warehouse ID
     * @return optional subscription
     */
    Optional<WarehouseSubscription> findByWarehouseId(String warehouseId);

    /**
     * Find active subscription by warehouse ID
     * 
     * @param warehouseId the warehouse ID
     * @return optional active subscription
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.warehouseId = :warehouseId AND s.status = 'ACTIVE' AND s.isActive = true")
    Optional<WarehouseSubscription> findActiveByWarehouseId(@Param("warehouseId") String warehouseId);

    /**
     * Find subscriptions by status
     * 
     * @param status the subscription status
     * @return list of subscriptions
     */
    List<WarehouseSubscription> findByStatus(SubscriptionStatus status);

    /**
     * Find subscriptions by status with pagination
     * 
     * @param status the subscription status
     * @param pageable pagination information
     * @return page of subscriptions
     */
    Page<WarehouseSubscription> findByStatus(SubscriptionStatus status, Pageable pageable);

    /**
     * Find subscriptions by plan
     * 
     * @param plan the subscription plan
     * @return list of subscriptions
     */
    List<WarehouseSubscription> findByPlan(SubscriptionPlan plan);

    /**
     * Find subscriptions by multiple statuses
     * 
     * @param statuses list of statuses
     * @return list of subscriptions
     */
    List<WarehouseSubscription> findByStatusIn(List<SubscriptionStatus> statuses);

    /**
     * Find subscriptions that are due for billing
     * 
     * @param currentDate current date
     * @return list of subscriptions due for billing
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.nextBillingDate <= :currentDate AND s.status = 'ACTIVE' AND s.isActive = true")
    List<WarehouseSubscription> findDueForBilling(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscriptions with upcoming billing (within next N days)
     * 
     * @param startDate start date
     * @param endDate end date
     * @return list of subscriptions with upcoming billing
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.nextBillingDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE' AND s.isActive = true")
    List<WarehouseSubscription> findUpcomingBilling(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find expired trial subscriptions
     * 
     * @param currentDate current date
     * @return list of expired trial subscriptions
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.status = 'TRIAL' AND s.trialEndDate <= :currentDate AND s.isActive = true")
    List<WarehouseSubscription> findExpiredTrials(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscriptions exceeding storage limits
     * 
     * @return list of subscriptions exceeding storage limits
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.storageUsage > s.storageLimit AND s.plan != 'ENTERPRISE' AND s.plan != 'CUSTOM' AND s.isActive = true")
    List<WarehouseSubscription> findExceedingStorageLimit();

    /**
     * Find subscriptions exceeding order limits
     * 
     * @return list of subscriptions exceeding order limits
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.ordersCount > s.ordersLimit AND s.plan != 'ENTERPRISE' AND s.plan != 'CUSTOM' AND s.isActive = true")
    List<WarehouseSubscription> findExceedingOrderLimit();

    /**
     * Find subscriptions by Stripe customer ID
     * 
     * @param stripeCustomerId Stripe customer ID
     * @return optional subscription
     */
    Optional<WarehouseSubscription> findByStripeCustomerId(String stripeCustomerId);

    /**
     * Find subscriptions by Stripe subscription ID
     * 
     * @param stripeSubscriptionId Stripe subscription ID
     * @return optional subscription
     */
    Optional<WarehouseSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);

    /**
     * Find subscriptions created within date range
     * 
     * @param startDate start date
     * @param endDate end date
     * @return list of subscriptions
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<WarehouseSubscription> findCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find subscriptions with active discounts
     * 
     * @param currentDate current date
     * @return list of subscriptions with active discounts
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.discountPercentage > 0 AND (s.discountEndDate IS NULL OR s.discountEndDate > :currentDate) AND s.isActive = true")
    List<WarehouseSubscription> findWithActiveDiscounts(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscriptions by billing email
     * 
     * @param email billing email
     * @return list of subscriptions
     */
    List<WarehouseSubscription> findByBillingEmail(String email);

    /**
     * Count subscriptions by status
     * 
     * @param status subscription status
     * @return count of subscriptions
     */
    long countByStatus(SubscriptionStatus status);

    /**
     * Count subscriptions by plan
     * 
     * @param plan subscription plan
     * @return count of subscriptions
     */
    long countByPlan(SubscriptionPlan plan);

    /**
     * Count active subscriptions
     * 
     * @return count of active subscriptions
     */
    @Query("SELECT COUNT(s) FROM WarehouseSubscription s WHERE s.status = 'ACTIVE' AND s.isActive = true")
    long countActiveSubscriptions();

    /**
     * Calculate total monthly recurring revenue
     * 
     * @return total MRR
     */
    @Query("SELECT COALESCE(SUM(s.monthlyFee), 0) FROM WarehouseSubscription s WHERE s.status = 'ACTIVE' AND s.isActive = true")
    java.math.BigDecimal calculateMonthlyRecurringRevenue();

    /**
     * Calculate total annual recurring revenue
     * 
     * @return total ARR
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN s.isAnnualBilling = true THEN s.annualFee ELSE s.monthlyFee * 12 END), 0) FROM WarehouseSubscription s WHERE s.status = 'ACTIVE' AND s.isActive = true")
    java.math.BigDecimal calculateAnnualRecurringRevenue();

    /**
     * Find subscriptions requiring attention (past due, suspended, etc.)
     * 
     * @return list of subscriptions requiring attention
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.status IN ('PAST_DUE', 'SUSPENDED', 'PENDING') AND s.isActive = true")
    List<WarehouseSubscription> findRequiringAttention();

    /**
     * Find subscriptions eligible for upgrade suggestions
     * 
     * @param storageThreshold storage usage threshold (percentage)
     * @param ordersThreshold orders usage threshold (percentage)
     * @return list of subscriptions eligible for upgrade
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE " +
           "(s.storageUsage * 100.0 / s.storageLimit >= :storageThreshold OR " +
           "s.ordersCount * 100.0 / s.ordersLimit >= :ordersThreshold) AND " +
           "s.status = 'ACTIVE' AND s.plan != 'ENTERPRISE' AND s.plan != 'CUSTOM' AND s.isActive = true")
    List<WarehouseSubscription> findEligibleForUpgrade(@Param("storageThreshold") double storageThreshold, 
                                                      @Param("ordersThreshold") double ordersThreshold);

    /**
     * Find subscriptions with high usage in current cycle
     * 
     * @param usageThreshold usage threshold percentage
     * @return list of high usage subscriptions
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE " +
           "(s.storageUsage * 100.0 / s.storageLimit >= :usageThreshold OR " +
           "s.ordersCount * 100.0 / s.ordersLimit >= :usageThreshold) AND " +
           "s.status = 'ACTIVE' AND s.isActive = true")
    List<WarehouseSubscription> findHighUsageSubscriptions(@Param("usageThreshold") double usageThreshold);

    /**
     * Find subscriptions by tenant ID
     * 
     * @param tenantId tenant ID
     * @return list of subscriptions
     */
    List<WarehouseSubscription> findByTenantId(String tenantId);

    /**
     * Find subscriptions with upcoming renewals
     * 
     * @param startDate start date
     * @param endDate end date
     * @return list of subscriptions with upcoming renewals
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.endDate BETWEEN :startDate AND :endDate AND s.status = 'ACTIVE' AND s.isActive = true")
    List<WarehouseSubscription> findUpcomingRenewals(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Find subscriptions cancelled in date range
     * 
     * @param startDate start date
     * @param endDate end date
     * @return list of cancelled subscriptions
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE s.cancelledDate BETWEEN :startDate AND :endDate")
    List<WarehouseSubscription> findCancelledBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Custom query to find subscriptions with complex filters
     * 
     * @param status subscription status (optional)
     * @param plan subscription plan (optional)
     * @param startDate created after date (optional)
     * @param endDate created before date (optional)
     * @param pageable pagination information
     * @return page of filtered subscriptions
     */
    @Query("SELECT s FROM WarehouseSubscription s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:plan IS NULL OR s.plan = :plan) AND " +
           "(:startDate IS NULL OR s.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR s.createdAt <= :endDate) AND " +
           "s.isActive = true")
    Page<WarehouseSubscription> findWithFilters(@Param("status") SubscriptionStatus status,
                                               @Param("plan") SubscriptionPlan plan,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               Pageable pageable);
}
