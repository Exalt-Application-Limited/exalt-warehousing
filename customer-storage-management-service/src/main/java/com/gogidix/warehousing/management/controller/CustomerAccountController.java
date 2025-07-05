package com.gogidix.warehousing.management.controller;

import com.gogidix.warehousing.management.entity.CustomerAccount;
import com.gogidix.warehousing.management.entity.CustomerNotification;
import com.gogidix.warehousing.management.entity.PaymentTransaction;
import com.gogidix.warehousing.management.entity.RentalAgreement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Customer Account Controller
 * 
 * REST API endpoints for customer account management operations.
 */
@RestController
@RequestMapping("/api/v1/customer-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Account Management", description = "Customer account lifecycle and management APIs")
public class CustomerAccountController {

    @Operation(summary = "Create new customer account", 
               description = "Register a new customer account with KYC verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid account data"),
        @ApiResponse(responseCode = "409", description = "Account already exists")
    })
    @PostMapping
    public ResponseEntity<CustomerAccount> createAccount(
            @RequestBody CustomerAccountCreateRequest request) {
        
        log.info("Creating new customer account for: {} {}", 
                request.getFirstName(), request.getLastName());
        
        // Implementation would create account and trigger KYC
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get customer account by ID", 
               description = "Retrieve detailed customer account information")
    @GetMapping("/{accountId}")
    public ResponseEntity<CustomerAccount> getAccount(
            @Parameter(description = "Customer account ID", required = true)
            @PathVariable Long accountId) {
        
        log.info("Retrieving customer account: {}", accountId);
        
        // Implementation would fetch account details
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get customer account by customer ID", 
               description = "Retrieve account using external customer ID")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerAccount> getAccountByCustomerId(
            @Parameter(description = "External customer ID", required = true)
            @PathVariable String customerId) {
        
        log.info("Retrieving account for customer ID: {}", customerId);
        
        // Implementation would fetch account by customer ID
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update customer account", 
               description = "Update customer account information")
    @PutMapping("/{accountId}")
    public ResponseEntity<CustomerAccount> updateAccount(
            @PathVariable Long accountId,
            @RequestBody CustomerAccountUpdateRequest request) {
        
        log.info("Updating customer account: {}", accountId);
        
        // Implementation would update account
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get customer dashboard data", 
               description = "Retrieve comprehensive dashboard data for customer")
    @GetMapping("/{accountId}/dashboard")
    public ResponseEntity<CustomerDashboard> getCustomerDashboard(
            @PathVariable Long accountId) {
        
        log.info("Retrieving dashboard for account: {}", accountId);
        
        // Implementation would aggregate dashboard data
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get customer rental history", 
               description = "Retrieve all rental agreements for a customer")
    @GetMapping("/{accountId}/rentals")
    public ResponseEntity<List<RentalAgreement>> getRentalHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Retrieving rental history for account: {}", accountId);
        
        // Implementation would fetch rental history
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get active rentals", 
               description = "Retrieve currently active rental agreements")
    @GetMapping("/{accountId}/rentals/active")
    public ResponseEntity<List<RentalAgreement>> getActiveRentals(
            @PathVariable Long accountId) {
        
        log.info("Retrieving active rentals for account: {}", accountId);
        
        // Implementation would fetch active rentals
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Verify customer account", 
               description = "Complete KYC verification for customer account")
    @PostMapping("/{accountId}/verify")
    public ResponseEntity<Map<String, Object>> verifyAccount(
            @PathVariable Long accountId,
            @RequestBody VerificationRequest request) {
        
        log.info("Verifying customer account: {}", accountId);
        
        // Implementation would process KYC verification
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update notification preferences", 
               description = "Update customer communication preferences")
    @PutMapping("/{accountId}/preferences")
    public ResponseEntity<CustomerAccount> updatePreferences(
            @PathVariable Long accountId,
            @RequestBody PreferencesUpdateRequest request) {
        
        log.info("Updating preferences for account: {}", accountId);
        
        // Implementation would update preferences
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get loyalty status", 
               description = "Retrieve customer loyalty program status and benefits")
    @GetMapping("/{accountId}/loyalty")
    public ResponseEntity<LoyaltyStatus> getLoyaltyStatus(
            @PathVariable Long accountId) {
        
        log.info("Retrieving loyalty status for account: {}", accountId);
        
        // Implementation would fetch loyalty data
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Suspend customer account", 
               description = "Temporarily suspend a customer account")
    @PostMapping("/{accountId}/suspend")
    public ResponseEntity<Void> suspendAccount(
            @PathVariable Long accountId,
            @RequestBody SuspensionRequest request) {
        
        log.info("Suspending customer account: {} - Reason: {}", 
                accountId, request.getReason());
        
        // Implementation would suspend account
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivate customer account", 
               description = "Reactivate a suspended customer account")
    @PostMapping("/{accountId}/reactivate")
    public ResponseEntity<CustomerAccount> reactivateAccount(
            @PathVariable Long accountId) {
        
        log.info("Reactivating customer account: {}", accountId);
        
        // Implementation would reactivate account
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get account statistics", 
               description = "Retrieve comprehensive account statistics and metrics")
    @GetMapping("/{accountId}/statistics")
    public ResponseEntity<AccountStatistics> getAccountStatistics(
            @PathVariable Long accountId) {
        
        log.info("Retrieving statistics for account: {}", accountId);
        
        // Implementation would calculate statistics
        return ResponseEntity.ok().build();
    }

    // Request/Response DTOs
    @Data
    public static class CustomerAccountCreateRequest {
        private String customerId;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String billingAddress;
        private String billingCity;
        private String billingState;
        private String billingZipCode;
        private String billingCountry;
        private CustomerAccount.AccountType accountType;
        private String companyName;
        private String taxIdNumber;
        private String referralSource;
        private String referralCode;
    }

    @Data
    public static class CustomerAccountUpdateRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String alternatePhone;
        private String billingAddress;
        private String billingCity;
        private String billingState;
        private String billingZipCode;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String emergencyContactRelation;
    }

    @Data
    public static class VerificationRequest {
        private String identityDocumentType;
        private String identityDocumentNumber;
        private String documentImageUrl;
        private Map<String, String> additionalInfo;
    }

    @Data
    public static class PreferencesUpdateRequest {
        private Boolean autoPayEnabled;
        private Boolean paperlessEnabled;
        private Boolean smsNotificationsEnabled;
        private Boolean emailNotificationsEnabled;
        private CustomerAccount.PreferredContactMethod preferredContactMethod;
        private String preferredContactTime;
    }

    @Data
    public static class SuspensionRequest {
        private String reason;
        private String notes;
    }

    @Data
    public static class CustomerDashboard {
        private CustomerAccount account;
        private List<RentalAgreement> activeRentals;
        private BigDecimal totalMonthlyCharges;
        private BigDecimal outstandingBalance;
        private Integer loyaltyPoints;
        private List<CustomerNotification> recentNotifications;
        private List<PaymentTransaction> recentTransactions;
        private AccountStatistics statistics;
    }

    @Data
    public static class LoyaltyStatus {
        private CustomerAccount.LoyaltyTier currentTier;
        private Integer currentPoints;
        private Integer pointsToNextTier;
        private LocalDateTime tierExpiryDate;
        private List<LoyaltyBenefit> benefits;
        private List<LoyaltyTransaction> recentTransactions;
    }

    @Data
    public static class LoyaltyBenefit {
        private String name;
        private String description;
        private BigDecimal value;
    }

    @Data
    public static class LoyaltyTransaction {
        private LocalDateTime date;
        private String description;
        private Integer points;
        private String type; // EARNED, REDEEMED
    }

    @Data
    public static class AccountStatistics {
        private Integer totalRentals;
        private Integer activeRentals;
        private BigDecimal totalSpent;
        private BigDecimal averageMonthlySpend;
        private LocalDateTime customerSince;
        private Integer paymentRating;
        private BigDecimal lifetimeValue;
        private Map<String, Integer> rentalsByUnitType;
    }
}