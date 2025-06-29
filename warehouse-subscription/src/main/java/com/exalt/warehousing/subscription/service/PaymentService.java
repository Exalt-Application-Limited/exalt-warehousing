package com.exalt.warehousing.subscription.service;

import java.math.BigDecimal;

/**
 * Service interface for payment processing operations
 * 
 * This service handles integration with payment processors like Stripe
 * for subscription billing, payment processing, and refund management.
 */
public interface PaymentService {

    /**
     * Create a Stripe customer
     * 
     * @param email customer email
     * @param description customer description
     * @return Stripe customer ID
     */
    String createStripeCustomer(String email, String description);

    /**
     * Process a payment
     * 
     * @param customerId Stripe customer ID
     * @param paymentMethodId payment method ID
     * @param amount amount to charge
     * @param currency currency code
     * @param description payment description
     * @return transaction ID
     */
    String processPayment(String customerId, String paymentMethodId, BigDecimal amount, 
                         String currency, String description);

    /**
     * Process a refund
     * 
     * @param transactionId original transaction ID
     * @param amount amount to refund
     * @param reason refund reason
     * @return refund ID
     */
    String processRefund(String transactionId, BigDecimal amount, String reason);

    /**
     * Cancel a Stripe subscription
     * 
     * @param stripeSubscriptionId Stripe subscription ID
     * @param atPeriodEnd whether to cancel at period end
     */
    void cancelStripeSubscription(String stripeSubscriptionId, boolean atPeriodEnd);

    /**
     * Setup a payment method for a customer
     * 
     * @param customerId Stripe customer ID
     * @param paymentMethodId payment method ID
     * @return success status
     */
    boolean setupPaymentMethod(String customerId, String paymentMethodId);

    /**
     * Verify payment method
     * 
     * @param paymentMethodId payment method ID
     * @return verification status
     */
    boolean verifyPaymentMethod(String paymentMethodId);
}