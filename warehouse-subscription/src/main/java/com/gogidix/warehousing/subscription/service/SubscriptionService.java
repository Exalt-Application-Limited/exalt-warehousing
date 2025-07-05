package com.gogidix.warehousing.subscription.service;

import com.gogidix.warehousing.subscription.model.BillingRecord;
import com.gogidix.warehousing.subscription.model.UsageRecord;
import com.gogidix.warehousing.subscription.model.WarehouseSubscription;
import com.gogidix.warehousing.subscription.model.enums.SubscriptionPlan;
import com.gogidix.warehousing.subscription.model.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for warehouse subscription management
 * 
 * This service provides comprehensive subscription lifecycle management including
 * plan management, billing, usage tracking, payment processing, and analytics.
 */
public interface SubscriptionService {

    // ========== Subscription Management ==========

    /**
     * Create a new subscription for a warehouse
     * 
     * @param warehouseId the warehouse ID
     * @param plan the subscription plan
     * @param billingEmail billing email address
     * @param isAnnualBilling whether to bill annually
     * @param createdBy user creating the subscription
     * @return created subscription
     */
    WarehouseSubscription createSubscription(UUID warehouseId, SubscriptionPlan plan, 
                                           String billingEmail, boolean isAnnualBilling, 
                                           String createdBy);

    /**
     * Start a trial subscription
     * 
     * @param warehouseId the warehouse ID
     * @param billingEmail billing email address
     * @param trialDays number of trial days
     * @param createdBy user creating the subscription
     * @return trial subscription
     */
    WarehouseSubscription startTrial(UUID warehouseId, String billingEmail, 
                                   int trialDays, String createdBy);

    /**
     * Get subscription by ID
     * 
     * @param subscriptionId the subscription ID
     * @return subscription
     */
    WarehouseSubscription getSubscription(UUID subscriptionId);

    /**
     * Get subscription by warehouse ID
     * 
     * @param warehouseId the warehouse ID
     * @return subscription if exists
     */
    WarehouseSubscription getSubscriptionByWarehouse(UUID warehouseId);

    /**
     * Get active subscription by warehouse ID
     * 
     * @param warehouseId the warehouse ID
     * @return active subscription if exists
     */
    WarehouseSubscription getActiveSubscriptionByWarehouse(UUID warehouseId);

    /**
     * Update subscription plan
     * 
     * @param subscriptionId the subscription ID
     * @param newPlan the new subscription plan
     * @param updatedBy user updating the subscription
     * @return updated subscription
     */
    WarehouseSubscription upgradePlan(UUID subscriptionId, SubscriptionPlan newPlan, String updatedBy);

    /**
     * Change billing frequency
     * 
     * @param subscriptionId the subscription ID
     * @param isAnnualBilling whether to bill annually
     * @param updatedBy user updating the subscription
     * @return updated subscription
     */
    WarehouseSubscription changeBillingFrequency(UUID subscriptionId, boolean isAnnualBilling, String updatedBy);

    /**
     * Cancel subscription
     * 
     * @param subscriptionId the subscription ID
     * @param reason cancellation reason
     * @param cancelAtPeriodEnd whether to cancel at period end
     * @param cancelledBy user cancelling the subscription
     * @return cancelled subscription
     */
    WarehouseSubscription cancelSubscription(UUID subscriptionId, String reason, 
                                           boolean cancelAtPeriodEnd, String cancelledBy);

    /**
     * Reactivate cancelled subscription
     * 
     * @param subscriptionId the subscription ID
     * @param reactivatedBy user reactivating the subscription
     * @return reactivated subscription
     */
    WarehouseSubscription reactivateSubscription(UUID subscriptionId, String reactivatedBy);

    /**
     * Pause subscription
     * 
     * @param subscriptionId the subscription ID
     * @param pausedBy user pausing the subscription
     * @return paused subscription
     */
    WarehouseSubscription pauseSubscription(UUID subscriptionId, String pausedBy);

    /**
     * Resume paused subscription
     * 
     * @param subscriptionId the subscription ID
     * @param resumedBy user resuming the subscription
     * @return resumed subscription
     */
    WarehouseSubscription resumeSubscription(UUID subscriptionId, String resumedBy);

    // ========== Usage Tracking ==========

    /**
     * Record usage for a subscription
     * 
     * @param subscriptionId the subscription ID
     * @param usageType type of usage
     * @param quantity usage quantity
     * @param unit unit of measurement
     * @param description usage description
     * @return usage record
     */
    UsageRecord recordUsage(UUID subscriptionId, UsageRecord.UsageType usageType, 
                          BigDecimal quantity, String unit, String description);

    /**
     * Update subscription usage counters
     * 
     * @param warehouseId the warehouse ID
     * @param storageUsed storage used (cubic meters)
     * @param ordersProcessed orders processed
     * @param apiRequests API requests made
     */
    void updateUsageCounters(UUID warehouseId, int storageUsed, int ordersProcessed, long apiRequests);

    /**
     * Get usage records for subscription
     * 
     * @param subscriptionId the subscription ID
     * @param startDate start date (optional)
     * @param endDate end date (optional)
     * @param pageable pagination
     * @return page of usage records
     */
    Page<UsageRecord> getUsageRecords(UUID subscriptionId, LocalDateTime startDate, 
                                    LocalDateTime endDate, Pageable pageable);

    /**
     * Get current usage summary for subscription
     * 
     * @param subscriptionId the subscription ID
     * @return usage summary map
     */
    Map<String, Object> getCurrentUsageSummary(UUID subscriptionId);

    /**
     * Check if subscription usage limits are exceeded
     * 
     * @param subscriptionId the subscription ID
     * @return usage limits status
     */
    Map<String, Boolean> checkUsageLimits(UUID subscriptionId);

    // ========== Billing Management ==========

    /**
     * Process billing for subscription
     * 
     * @param subscriptionId the subscription ID
     * @return billing record
     */
    BillingRecord processBilling(UUID subscriptionId);

    /**
     * Process billing for all due subscriptions
     * 
     * @return number of subscriptions billed
     */
    int processDueBilling();

    /**
     * Generate invoice for subscription
     * 
     * @param subscriptionId the subscription ID
     * @param billingPeriodStart billing period start
     * @param billingPeriodEnd billing period end
     * @return billing record
     */
    BillingRecord generateInvoice(UUID subscriptionId, LocalDateTime billingPeriodStart, 
                                LocalDateTime billingPeriodEnd);

    /**
     * Calculate overage charges for subscription
     * 
     * @param subscriptionId the subscription ID
     * @return overage amount
     */
    BigDecimal calculateOverageCharges(UUID subscriptionId);

    /**
     * Get billing records for subscription
     * 
     * @param subscriptionId the subscription ID
     * @param pageable pagination
     * @return page of billing records
     */
    Page<BillingRecord> getBillingRecords(UUID subscriptionId, Pageable pageable);

    /**
     * Mark invoice as paid
     * 
     * @param billingRecordId billing record ID
     * @param amount amount paid
     * @param transactionId transaction ID
     */
    void markInvoiceAsPaid(UUID billingRecordId, BigDecimal amount, String transactionId);

    /**
     * Mark invoice as failed
     * 
     * @param billingRecordId billing record ID
     * @param failureReason failure reason
     */
    void markInvoiceAsFailed(UUID billingRecordId, String failureReason);

    // ========== Payment Processing ==========

    /**
     * Setup payment method for subscription
     * 
     * @param subscriptionId the subscription ID
     * @param paymentMethodId payment method ID
     * @return updated subscription
     */
    WarehouseSubscription setupPaymentMethod(UUID subscriptionId, String paymentMethodId);

    /**
     * Process payment for billing record
     * 
     * @param billingRecordId billing record ID
     * @return payment success status
     */
    boolean processPayment(UUID billingRecordId);

    /**
     * Retry failed payment
     * 
     * @param billingRecordId billing record ID
     * @return payment success status
     */
    boolean retryFailedPayment(UUID billingRecordId);

    /**
     * Process refund
     * 
     * @param billingRecordId billing record ID
     * @param refundAmount amount to refund
     * @param reason refund reason
     * @return refund success status
     */
    boolean processRefund(UUID billingRecordId, BigDecimal refundAmount, String reason);

    // ========== Discount Management ==========

    /**
     * Apply discount to subscription
     * 
     * @param subscriptionId the subscription ID
     * @param discountPercentage discount percentage
     * @param endDate discount end date
     * @param appliedBy user applying the discount
     * @return updated subscription
     */
    WarehouseSubscription applyDiscount(UUID subscriptionId, BigDecimal discountPercentage, 
                                      LocalDateTime endDate, String appliedBy);

    /**
     * Remove discount from subscription
     * 
     * @param subscriptionId the subscription ID
     * @param removedBy user removing the discount
     * @return updated subscription
     */
    WarehouseSubscription removeDiscount(UUID subscriptionId, String removedBy);

    // ========== Analytics and Reporting ==========

    /**
     * Get subscription analytics
     * 
     * @param startDate start date
     * @param endDate end date
     * @return analytics data
     */
    Map<String, Object> getSubscriptionAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculate monthly recurring revenue
     * 
     * @return MRR amount
     */
    BigDecimal calculateMRR();

    /**
     * Calculate annual recurring revenue
     * 
     * @return ARR amount
     */
    BigDecimal calculateARR();

    /**
     * Get churn rate for period
     * 
     * @param startDate start date
     * @param endDate end date
     * @return churn rate percentage
     */
    BigDecimal getChurnRate(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get revenue metrics
     * 
     * @param startDate start date
     * @param endDate end date
     * @return revenue metrics
     */
    Map<String, BigDecimal> getRevenueMetrics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get subscription distribution by plan
     * 
     * @return plan distribution map
     */
    Map<SubscriptionPlan, Long> getSubscriptionDistributionByPlan();

    /**
     * Get subscription distribution by status
     * 
     * @return status distribution map
     */
    Map<SubscriptionStatus, Long> getSubscriptionDistributionByStatus();

    // ========== Subscription Lifecycle ==========

    /**
     * Process expired trials
     * 
     * @return number of trials processed
     */
    int processExpiredTrials();

    /**
     * Send billing reminders
     * 
     * @param daysBefore days before due date
     * @return number of reminders sent
     */
    int sendBillingReminders(int daysBefore);

    /**
     * Process overdue payments
     * 
     * @return number of overdue subscriptions processed
     */
    int processOverduePayments();

    /**
     * Send renewal reminders
     * 
     * @param daysBefore days before renewal
     * @return number of reminders sent
     */
    int sendRenewalReminders(int daysBefore);

    /**
     * Suggest plan upgrades based on usage
     * 
     * @param usageThreshold usage threshold percentage
     * @return list of subscriptions eligible for upgrade
     */
    List<WarehouseSubscription> suggestPlanUpgrades(double usageThreshold);

    // ========== Search and Filtering ==========

    /**
     * Search subscriptions with filters
     * 
     * @param status subscription status (optional)
     * @param plan subscription plan (optional)
     * @param startDate created after date (optional)
     * @param endDate created before date (optional)
     * @param pageable pagination
     * @return page of filtered subscriptions
     */
    Page<WarehouseSubscription> searchSubscriptions(SubscriptionStatus status, SubscriptionPlan plan,
                                                   LocalDateTime startDate, LocalDateTime endDate,
                                                   Pageable pageable);

    /**
     * Get subscriptions requiring attention
     * 
     * @return list of subscriptions needing attention
     */
    List<WarehouseSubscription> getSubscriptionsRequiringAttention();

    /**
     * Get upcoming renewals
     * 
     * @param days number of days to look ahead
     * @return list of upcoming renewals
     */
    List<WarehouseSubscription> getUpcomingRenewals(int days);

    /**
     * Get high usage subscriptions
     * 
     * @param usageThreshold usage threshold percentage
     * @return list of high usage subscriptions
     */
    List<WarehouseSubscription> getHighUsageSubscriptions(double usageThreshold);

    // ========== Tenant Management ==========

    /**
     * Get subscriptions for tenant
     * 
     * @param tenantId tenant ID
     * @param pageable pagination
     * @return page of subscriptions
     */
    Page<WarehouseSubscription> getSubscriptionsForTenant(UUID tenantId, Pageable pageable);

    /**
     * Get tenant usage summary
     * 
     * @param tenantId tenant ID
     * @return tenant usage summary
     */
    Map<String, Object> getTenantUsageSummary(UUID tenantId);

    // ========== Controller-Compatible Methods ==========
    // Methods with String IDs and DTO parameters to match the REST controller

    /**
     * Create subscription using DTO
     */
    WarehouseSubscription createSubscription(com.gogidix.warehousing.subscription.dto.SubscriptionCreateRequest request);
    
    /**
     * Get subscription details as DTO
     */
    com.gogidix.warehousing.subscription.dto.SubscriptionDetailDTO getSubscriptionDetails(String subscriptionId);
    
    /**
     * Get warehouse subscriptions with String ID
     */
    Page<WarehouseSubscription> getWarehouseSubscriptions(String warehouseId, Pageable pageable);
    
    /**
     * Search subscriptions using DTO
     */
    Page<WarehouseSubscription> searchSubscriptions(com.gogidix.warehousing.subscription.dto.SubscriptionSearchRequest searchRequest, Pageable pageable);
    
    /**
     * Update subscription using DTO
     */
    WarehouseSubscription updateSubscription(String subscriptionId, com.gogidix.warehousing.subscription.dto.SubscriptionUpdateRequest request);
    
    /**
     * Cancel subscription with String ID
     */
    void cancelSubscriptionByStringId(String subscriptionId, String reason);
    
    /**
     * Upgrade plan using DTO
     */
    com.gogidix.warehousing.subscription.dto.PlanChangeResult upgradePlan(String subscriptionId, com.gogidix.warehousing.subscription.dto.PlanUpgradeRequest request);
    
    /**
     * Downgrade plan using DTO
     */
    com.gogidix.warehousing.subscription.dto.PlanChangeResult downgradePlan(String subscriptionId, com.gogidix.warehousing.subscription.dto.PlanDowngradeRequest request);
    
    /**
     * Get available plans as DTOs
     */
    List<com.gogidix.warehousing.subscription.dto.SubscriptionPlanDTO> getAvailablePlans();
    
    /**
     * Get plan recommendations
     */
    List<com.gogidix.warehousing.subscription.dto.PlanRecommendation> getPlanRecommendations(String subscriptionId);
    
    /**
     * Record usage using DTO with String ID
     */
    UsageRecord recordUsageWithStringId(String subscriptionId, com.gogidix.warehousing.subscription.dto.UsageRecordRequest request);
    
    /**
     * Get usage history with String ID
     */
    Page<UsageRecord> getUsageHistory(String subscriptionId, java.time.LocalDate startDate, java.time.LocalDate endDate, Pageable pageable);
    
    /**
     * Get usage summary with String ID
     */
    com.gogidix.warehousing.subscription.dto.UsageSummary getCurrentUsageSummary(String subscriptionId);
    
    /**
     * Process billing with String ID
     */
    com.gogidix.warehousing.subscription.dto.BillingResult processBillingWithStringId(String subscriptionId);
    
    /**
     * Get invoices with String ID
     */
    Page<com.gogidix.warehousing.subscription.dto.InvoiceDTO> getInvoices(String subscriptionId, java.time.LocalDate startDate, java.time.LocalDate endDate, Pageable pageable);
    
    /**
     * Process payment using DTO
     */
    com.gogidix.warehousing.subscription.dto.PaymentResult processPayment(String subscriptionId, com.gogidix.warehousing.subscription.dto.PaymentRequest request);
    
    /**
     * Get payment history with String ID
     */
    Page<com.gogidix.warehousing.subscription.dto.PaymentRecord> getPaymentHistory(String subscriptionId, Pageable pageable);
    
    /**
     * Get revenue analytics
     */
    com.gogidix.warehousing.subscription.dto.RevenueAnalytics getRevenueAnalytics(java.time.LocalDate startDate, java.time.LocalDate endDate, String groupBy);
    
    /**
     * Get churn analytics
     */
    com.gogidix.warehousing.subscription.dto.ChurnAnalytics getChurnAnalytics(java.time.LocalDate startDate, java.time.LocalDate endDate);
    
    /**
     * Get subscription analytics with String ID
     */
    com.gogidix.warehousing.subscription.dto.SubscriptionAnalytics getSubscriptionAnalytics(String subscriptionId, int days);
    
    /**
     * Get subscription metrics overview
     */
    com.gogidix.warehousing.subscription.dto.SubscriptionMetricsOverview getSubscriptionMetricsOverview();
    
    /**
     * Send payment reminder
     */
    void sendPaymentReminder(String subscriptionId);
    
    /**
     * Send usage alert
     */
    void sendUsageAlert(String subscriptionId, String alertType, BigDecimal threshold);
    
    /**
     * Bulk process billing
     */
    com.gogidix.warehousing.subscription.dto.BulkBillingResult bulkProcessBilling(List<String> subscriptionIds);
    
    /**
     * Bulk send notifications
     */
    com.gogidix.warehousing.subscription.dto.BulkNotificationResult bulkSendNotifications(com.gogidix.warehousing.subscription.dto.BulkNotificationRequest request);
    
    /**
     * Get service health
     */
    com.gogidix.warehousing.subscription.dto.ServiceHealthStatus getServiceHealth();
    
    /**
     * Get subscription status
     */
    com.gogidix.warehousing.subscription.dto.SubscriptionStatusDTO getSubscriptionStatus(String subscriptionId);
}
