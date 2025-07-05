package com.gogidix.warehousing.subscription.service.impl;

import com.gogidix.warehousing.subscription.model.WarehouseSubscription;
import com.gogidix.warehousing.subscription.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of NotificationService
 * 
 * This implementation provides basic notification functionality.
 * In a production environment, this would integrate with email services,
 * SMS providers, and push notification systems.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendUsageAlert(WarehouseSubscription subscription, String usageType, BigDecimal usagePercentage) {
        log.info("Sending usage alert for subscription {} - {} usage at {}%", 
                subscription.getId(), usageType, usagePercentage);
        
        // In a real implementation, this would send email/SMS/push notification
        String message = String.format(
                "Usage Alert: Your %s usage is at %.1f%% of your plan limit. " +
                "Consider upgrading your plan or contact support.",
                usageType, usagePercentage);
        
        sendNotification(subscription.getBillingEmail(), "Usage Alert", message);
    }

    @Override
    public void sendBillingReminder(WarehouseSubscription subscription, int daysBefore) {
        log.info("Sending billing reminder for subscription {} - {} days before due", 
                subscription.getId(), daysBefore);
        
        String message = String.format(
                "Billing Reminder: Your next payment of $%.2f is due in %d days. " +
                "Please ensure your payment method is up to date.",
                subscription.getEffectiveMonthlyFee(), daysBefore);
        
        sendNotification(subscription.getBillingEmail(), "Billing Reminder", message);
    }

    @Override
    public void sendRenewalReminder(WarehouseSubscription subscription, int daysBefore) {
        log.info("Sending renewal reminder for subscription {} - {} days before renewal", 
                subscription.getId(), daysBefore);
        
        String message = String.format(
                "Renewal Reminder: Your %s subscription will renew in %d days. " +
                "Your next payment will be $%.2f.",
                subscription.getPlan().getDisplayName(), 
                daysBefore, 
                subscription.getEffectiveMonthlyFee());
        
        sendNotification(subscription.getBillingEmail(), "Subscription Renewal Reminder", message);
    }

    @Override
    public void sendPaymentFailureNotification(WarehouseSubscription subscription, String failureReason) {
        log.info("Sending payment failure notification for subscription {} - reason: {}", 
                subscription.getId(), failureReason);
        
        String message = String.format(
                "Payment Failed: We were unable to process your payment for your %s subscription. " +
                "Reason: %s. Please update your payment method to avoid service interruption.",
                subscription.getPlan().getDisplayName(), failureReason);
        
        sendNotification(subscription.getBillingEmail(), "Payment Failed", message);
    }

    @Override
    public void sendPaymentSuccessNotification(WarehouseSubscription subscription, BigDecimal amount) {
        log.info("Sending payment success notification for subscription {} - amount: ${}", 
                subscription.getId(), amount);
        
        String message = String.format(
                "Payment Successful: We have successfully processed your payment of $%.2f " +
                "for your %s subscription. Thank you!",
                amount, subscription.getPlan().getDisplayName());
        
        sendNotification(subscription.getBillingEmail(), "Payment Successful", message);
    }

    @Override
    public void sendCancellationConfirmation(WarehouseSubscription subscription) {
        log.info("Sending cancellation confirmation for subscription {}", subscription.getId());
        
        String message = String.format(
                "Subscription Cancelled: Your %s subscription has been cancelled. " +
                "You will continue to have access until %s. " +
                "We're sorry to see you go!",
                subscription.getPlan().getDisplayName(),
                subscription.getEndDate() != null ? subscription.getEndDate().toLocalDate() : "the end of your current billing period");
        
        sendNotification(subscription.getBillingEmail(), "Subscription Cancelled", message);
    }

    @Override
    public void sendTrialExpirationWarning(WarehouseSubscription subscription, int daysRemaining) {
        log.info("Sending trial expiration warning for subscription {} - {} days remaining", 
                subscription.getId(), daysRemaining);
        
        String message = String.format(
                "Trial Ending Soon: Your free trial expires in %d days. " +
                "Upgrade to a paid plan to continue enjoying our services without interruption. " +
                "Choose from our flexible plans starting at $%.2f/month.",
                daysRemaining, subscription.getPlan().getUpgradePlan().getMonthlyPrice());
        
        sendNotification(subscription.getBillingEmail(), "Trial Ending Soon", message);
    }

    @Override
    public void sendUpgradeSuggestion(WarehouseSubscription subscription, String suggestedPlan) {
        log.info("Sending upgrade suggestion for subscription {} - suggested plan: {}", 
                subscription.getId(), suggestedPlan);
        
        String message = String.format(
                "Upgrade Suggestion: Based on your usage patterns, you might benefit from upgrading " +
                "to our %s plan. This would provide you with more resources and advanced features " +
                "to support your growing business needs.",
                suggestedPlan);
        
        sendNotification(subscription.getBillingEmail(), "Plan Upgrade Suggestion", message);
    }

    /**
     * Send notification via configured channels
     * 
     * @param recipient recipient email
     * @param subject notification subject
     * @param message notification message
     */
    private void sendNotification(String recipient, String subject, String message) {
        // In a real implementation, this would:
        // 1. Send email via email service (SendGrid, SES, etc.)
        // 2. Send SMS if phone number is available
        // 3. Send push notification if mobile app is installed
        // 4. Create in-app notification
        // 5. Log to notification history
        
        log.info("Notification sent to {}: {} - {}", recipient, subject, message);
        
        // For now, just log the notification
        // In production, integrate with actual notification services
    }
}