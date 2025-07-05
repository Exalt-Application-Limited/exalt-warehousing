package com.gogidix.warehousing.subscription.controller;

import com.gogidix.warehousing.subscription.dto.*;
import com.gogidix.warehousing.subscription.model.WarehouseSubscription;
import com.gogidix.warehousing.subscription.model.UsageRecord;
import com.gogidix.warehousing.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Warehouse Subscription Management
 * 
 * Provides comprehensive API endpoints for managing warehouse partner subscriptions,
 * including plan management, billing, usage tracking, and analytics.
 * 
 * Key Endpoints:
 * - Subscription lifecycle management
 * - Plan upgrades and downgrades
 * - Usage tracking and billing
 * - Payment processing and invoicing
 * - Analytics and reporting
 * 
 * Security:
 * - Admin operations require elevated permissions
 * - Warehouse partners can manage their own subscriptions
 * - Billing operations require specific roles
 * 
 * @author Warehousing Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/v1/warehouse-subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Subscriptions", description = "Warehouse subscription management operations")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // ========================================
    // Subscription Management Endpoints
    // ========================================

    @PostMapping
    @Operation(summary = "Create new warehouse subscription", 
               description = "Create a new subscription for a warehouse partner")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<WarehouseSubscription> createSubscription(
            @Valid @RequestBody SubscriptionCreateRequest request) {
        
        log.info("Creating new subscription for warehouse: {}", request.getWarehouseId());
        
        WarehouseSubscription subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription details", 
               description = "Retrieve detailed subscription information")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<SubscriptionDetailDTO> getSubscription(
            @PathVariable String subscriptionId) {
        
        SubscriptionDetailDTO subscription = subscriptionService.getSubscriptionDetails(subscriptionId);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get warehouse subscriptions", 
               description = "Retrieve all subscriptions for a specific warehouse")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Page<WarehouseSubscription>> getWarehouseSubscriptions(
            @PathVariable String warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<WarehouseSubscription> subscriptions = subscriptionService.getWarehouseSubscriptions(
                warehouseId, pageable);
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/search")
    @Operation(summary = "Search subscriptions", 
               description = "Search subscriptions using various criteria")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<WarehouseSubscription>> searchSubscriptions(
            @Valid @RequestBody SubscriptionSearchRequest searchRequest,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<WarehouseSubscription> subscriptions = subscriptionService.searchSubscriptions(
                searchRequest, pageable);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{subscriptionId}")
    @Operation(summary = "Update subscription", 
               description = "Update subscription details")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canModifySubscription(authentication, #subscriptionId)")
    public ResponseEntity<WarehouseSubscription> updateSubscription(
            @PathVariable String subscriptionId,
            @Valid @RequestBody SubscriptionUpdateRequest request) {
        
        log.info("Updating subscription: {}", subscriptionId);
        
        WarehouseSubscription subscription = subscriptionService.updateSubscription(
                subscriptionId, request);
        return ResponseEntity.ok(subscription);
    }

    @DeleteMapping("/{subscriptionId}")
    @Operation(summary = "Cancel subscription", 
               description = "Cancel a warehouse subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canModifySubscription(authentication, #subscriptionId)")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable String subscriptionId,
            @RequestParam(required = false) String reason) {
        
        log.info("Cancelling subscription: {} with reason: {}", subscriptionId, reason);
        
        subscriptionService.cancelSubscriptionByStringId(subscriptionId, reason);
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // Plan Management Endpoints
    // ========================================

    @PostMapping("/{subscriptionId}/upgrade")
    @Operation(summary = "Upgrade subscription plan", 
               description = "Upgrade to a higher tier subscription plan")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canModifySubscription(authentication, #subscriptionId)")
    public ResponseEntity<PlanChangeResult> upgradeSubscription(
            @PathVariable String subscriptionId,
            @Valid @RequestBody PlanUpgradeRequest request) {
        
        log.info("Upgrading subscription {}", subscriptionId); // request.getTargetPlan() - method missing
        
        PlanChangeResult result = subscriptionService.upgradePlan(subscriptionId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{subscriptionId}/downgrade")
    @Operation(summary = "Downgrade subscription plan", 
               description = "Downgrade to a lower tier subscription plan")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canModifySubscription(authentication, #subscriptionId)")
    public ResponseEntity<PlanChangeResult> downgradeSubscription(
            @PathVariable String subscriptionId,
            @Valid @RequestBody PlanDowngradeRequest request) {
        
        log.info("Downgrading subscription {}", subscriptionId); // request.getTargetPlan() - method missing
        
        PlanChangeResult result = subscriptionService.downgradePlan(subscriptionId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/plans")
    @Operation(summary = "Get available subscription plans", 
               description = "Retrieve all available subscription plans with pricing")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAvailablePlans() {
        
        List<SubscriptionPlanDTO> plans = subscriptionService.getAvailablePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{subscriptionId}/plan-recommendations")
    @Operation(summary = "Get plan recommendations", 
               description = "Get recommended plans based on usage patterns")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<List<PlanRecommendation>> getPlanRecommendations(
            @PathVariable String subscriptionId) {
        
        List<PlanRecommendation> recommendations = subscriptionService.getPlanRecommendations(subscriptionId);
        return ResponseEntity.ok(recommendations);
    }

    // ========================================
    // Usage Tracking Endpoints
    // ========================================

    @PostMapping("/{subscriptionId}/usage")
    @Operation(summary = "Record usage", 
               description = "Record usage for billing purposes")
    @PreAuthorize("hasRole('SYSTEM') or hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<UsageRecord> recordUsage(
            @PathVariable String subscriptionId,
            @Valid @RequestBody UsageRecordRequest request) {
        
        log.info("Recording usage for subscription: {}", subscriptionId);
        
        UsageRecord usage = subscriptionService.recordUsageWithStringId(subscriptionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usage);
    }

    @GetMapping("/{subscriptionId}/usage")
    @Operation(summary = "Get usage history", 
               description = "Retrieve usage history for a subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<Page<UsageRecord>> getUsageHistory(
            @PathVariable String subscriptionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 50) Pageable pageable) {
        
        Page<UsageRecord> usage = subscriptionService.getUsageHistory(
                subscriptionId, startDate, endDate, pageable);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/{subscriptionId}/usage/summary")
    @Operation(summary = "Get usage summary", 
               description = "Get current billing period usage summary")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<UsageSummary> getUsageSummary(
            @PathVariable String subscriptionId) {
        
        UsageSummary summary = subscriptionService.getCurrentUsageSummary(subscriptionId);
        return ResponseEntity.ok(summary);
    }

    // ========================================
    // Billing and Payment Endpoints
    // ========================================

    @PostMapping("/{subscriptionId}/process-billing")
    @Operation(summary = "Process billing", 
               description = "Process billing for the current period")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BILLING_MANAGER')")
    public ResponseEntity<BillingResult> processBilling(
            @PathVariable String subscriptionId) {
        
        log.info("Processing billing for subscription: {}", subscriptionId);
        
        BillingResult result = subscriptionService.processBillingWithStringId(subscriptionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{subscriptionId}/invoices")
    @Operation(summary = "Get invoices", 
               description = "Retrieve invoices for a subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<Page<InvoiceDTO>> getInvoices(
            @PathVariable String subscriptionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<InvoiceDTO> invoices = subscriptionService.getInvoices(
                subscriptionId, startDate, endDate, pageable);
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/{subscriptionId}/payments")
    @Operation(summary = "Process payment", 
               description = "Process a payment for a subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canModifySubscription(authentication, #subscriptionId)")
    public ResponseEntity<PaymentResult> processPayment(
            @PathVariable String subscriptionId,
            @Valid @RequestBody PaymentRequest request) {
        
        log.info("Processing payment for subscription: {}", subscriptionId);
        
        PaymentResult result = subscriptionService.processPayment(subscriptionId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{subscriptionId}/payment-history")
    @Operation(summary = "Get payment history", 
               description = "Retrieve payment history for a subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<Page<PaymentRecord>> getPaymentHistory(
            @PathVariable String subscriptionId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<PaymentRecord> payments = subscriptionService.getPaymentHistory(subscriptionId, pageable);
        return ResponseEntity.ok(payments);
    }

    // ========================================
    // Analytics and Reporting Endpoints
    // ========================================

    @GetMapping("/analytics/revenue")
    @Operation(summary = "Get revenue analytics", 
               description = "Retrieve revenue analytics across all subscriptions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_MANAGER')")
    public ResponseEntity<RevenueAnalytics> getRevenueAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String groupBy) {
        
        RevenueAnalytics analytics = subscriptionService.getRevenueAnalytics(startDate, endDate, groupBy);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/churn")
    @Operation(summary = "Get churn analytics", 
               description = "Retrieve subscription churn analytics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYTICS_MANAGER')")
    public ResponseEntity<ChurnAnalytics> getChurnAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ChurnAnalytics analytics = subscriptionService.getChurnAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/{subscriptionId}/analytics")
    @Operation(summary = "Get subscription analytics", 
               description = "Retrieve analytics for a specific subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<SubscriptionAnalytics> getSubscriptionAnalytics(
            @PathVariable String subscriptionId,
            @RequestParam(defaultValue = "30") int days) {
        
        SubscriptionAnalytics analytics = subscriptionService.getSubscriptionAnalytics(subscriptionId, days);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/metrics/overview")
    @Operation(summary = "Get subscription metrics overview", 
               description = "Get high-level subscription metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYTICS_MANAGER')")
    public ResponseEntity<SubscriptionMetricsOverview> getMetricsOverview() {
        
        SubscriptionMetricsOverview metrics = subscriptionService.getSubscriptionMetricsOverview();
        return ResponseEntity.ok(metrics);
    }

    // ========================================
    // Notification and Communication Endpoints
    // ========================================

    @PostMapping("/{subscriptionId}/notifications/payment-reminder")
    @Operation(summary = "Send payment reminder", 
               description = "Send payment reminder notification")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BILLING_MANAGER')")
    public ResponseEntity<Void> sendPaymentReminder(
            @PathVariable String subscriptionId) {
        
        log.info("Sending payment reminder for subscription: {}", subscriptionId);
        
        subscriptionService.sendPaymentReminder(subscriptionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{subscriptionId}/notifications/usage-alert")
    @Operation(summary = "Send usage alert", 
               description = "Send usage threshold alert")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<Void> sendUsageAlert(
            @PathVariable String subscriptionId,
            @RequestParam String alertType,
            @RequestParam BigDecimal threshold) {
        
        log.info("Sending usage alert for subscription: {}, type: {}, threshold: {}", 
                subscriptionId, alertType, threshold);
        
        subscriptionService.sendUsageAlert(subscriptionId, alertType, threshold);
        return ResponseEntity.ok().build();
    }

    // ========================================
    // Bulk Operations Endpoints
    // ========================================

    @PostMapping("/bulk/process-billing")
    @Operation(summary = "Bulk process billing", 
               description = "Process billing for multiple subscriptions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BILLING_MANAGER')")
    public ResponseEntity<BulkBillingResult> bulkProcessBilling(
            @RequestBody List<String> subscriptionIds) {
        
        log.info("Bulk processing billing for {} subscriptions", subscriptionIds.size());
        
        BulkBillingResult result = subscriptionService.bulkProcessBilling(subscriptionIds);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bulk/send-notifications")
    @Operation(summary = "Bulk send notifications", 
               description = "Send notifications to multiple subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BulkNotificationResult> bulkSendNotifications(
            @Valid @RequestBody BulkNotificationRequest request) {
        
        log.info("Bulk sending notifications to {} subscriptions", 
                request.getSubscriptionIds().size());
        
        BulkNotificationResult result = subscriptionService.bulkSendNotifications(request);
        return ResponseEntity.ok(result);
    }

    // ========================================
    // Health and Status Endpoints
    // ========================================

    @GetMapping("/health")
    @Operation(summary = "Service health check", 
               description = "Check the health of the subscription service")
    public ResponseEntity<ServiceHealthStatus> getHealthStatus() {
        
        ServiceHealthStatus health = subscriptionService.getServiceHealth();
        return ResponseEntity.ok(health);
    }

    @GetMapping("/{subscriptionId}/status")
    @Operation(summary = "Get subscription status", 
               description = "Get detailed status of a subscription")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionSecurityService.canAccessSubscription(authentication, #subscriptionId)")
    public ResponseEntity<SubscriptionStatusDTO> getSubscriptionStatus(
            @PathVariable String subscriptionId) {
        
        SubscriptionStatusDTO status = subscriptionService.getSubscriptionStatus(subscriptionId);
        return ResponseEntity.ok(status);
    }
}