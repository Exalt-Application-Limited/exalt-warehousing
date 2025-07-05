package com.gogidix.warehousing.subscription.service;

import com.gogidix.warehousing.subscription.model.WarehouseSubscription;

import java.math.BigDecimal;

/**
 * Service interface for notification operations
 * 
 * This service handles sending notifications related to subscriptions,
 * billing, usage alerts, and other important events.
 */
public interface NotificationService {

    /**
     * Send usage alert when limits are approached
     * 
     * @param subscription the subscription
     * @param usageType type of usage (storage, orders, etc.)
     * @param usagePercentage current usage percentage
     */
    void sendUsageAlert(WarehouseSubscription subscription, String usageType, BigDecimal usagePercentage);

    /**
     * Send billing reminder before due date
     * 
     * @param subscription the subscription
     * @param daysBefore days before billing
     */
    void sendBillingReminder(WarehouseSubscription subscription, int daysBefore);

    /**
     * Send renewal reminder before subscription renewal
     * 
     * @param subscription the subscription
     * @param daysBefore days before renewal
     */
    void sendRenewalReminder(WarehouseSubscription subscription, int daysBefore);

    /**
     * Send payment failure notification
     * 
     * @param subscription the subscription
     * @param failureReason reason for failure
     */
    void sendPaymentFailureNotification(WarehouseSubscription subscription, String failureReason);

    /**
     * Send payment success notification
     * 
     * @param subscription the subscription
     * @param amount amount paid
     */
    void sendPaymentSuccessNotification(WarehouseSubscription subscription, BigDecimal amount);

    /**
     * Send subscription cancellation confirmation
     * 
     * @param subscription the subscription
     */
    void sendCancellationConfirmation(WarehouseSubscription subscription);

    /**
     * Send trial expiration warning
     * 
     * @param subscription the subscription
     * @param daysRemaining days remaining in trial
     */
    void sendTrialExpirationWarning(WarehouseSubscription subscription, int daysRemaining);

    /**
     * Send plan upgrade suggestion
     * 
     * @param subscription the subscription
     * @param suggestedPlan suggested plan name
     */
    void sendUpgradeSuggestion(WarehouseSubscription subscription, String suggestedPlan);
}