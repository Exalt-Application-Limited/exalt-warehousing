package com.exalt.warehousing.billing.controller;

import com.exalt.warehousing.billing.model.BillingAccount;
import com.exalt.warehousing.billing.model.BillingAccountStatus;
import com.exalt.warehousing.billing.service.BillingAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for billing account operations
 */
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Billing Accounts", description = "Billing account management operations")
public class BillingAccountController {

    private final BillingAccountService billingAccountService;

    @PostMapping
    @Operation(summary = "Create a new billing account")
    public ResponseEntity<BillingAccount> createBillingAccount(
            @Valid @RequestBody BillingAccount billingAccount) {
        log.info("Creating billing account for warehouse partner: {}", 
                billingAccount.getWarehousePartnerId());
        
        BillingAccount created = billingAccountService.createBillingAccount(billingAccount);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get billing account by ID")
    public ResponseEntity<BillingAccount> getBillingAccount(
            @Parameter(description = "Billing account ID") @PathVariable UUID id) {
        log.debug("Retrieving billing account: {}", id);
        
        BillingAccount account = billingAccountService.getBillingAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/warehouse/{warehousePartnerId}")
    @Operation(summary = "Get billing account by warehouse partner ID")
    public ResponseEntity<BillingAccount> getBillingAccountByWarehousePartner(
            @Parameter(description = "Warehouse partner ID") @PathVariable UUID warehousePartnerId) {
        log.debug("Retrieving billing account for warehouse partner: {}", warehousePartnerId);
        
        BillingAccount account = billingAccountService.getBillingAccountByWarehousePartnerId(warehousePartnerId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update billing account")
    public ResponseEntity<BillingAccount> updateBillingAccount(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @Valid @RequestBody BillingAccount billingAccount) {
        log.info("Updating billing account: {}", id);
        
        BillingAccount updated = billingAccountService.updateBillingAccount(id, billingAccount);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete billing account")
    public ResponseEntity<Void> deleteBillingAccount(
            @Parameter(description = "Billing account ID") @PathVariable UUID id) {
        log.info("Deleting billing account: {}", id);
        
        billingAccountService.deleteBillingAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all billing accounts with pagination")
    public ResponseEntity<Page<BillingAccount>> getAllBillingAccounts(Pageable pageable) {
        log.debug("Retrieving billing accounts with pagination: {}", pageable);
        
        Page<BillingAccount> accounts = billingAccountService.getAllBillingAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get billing accounts by status")
    public ResponseEntity<Page<BillingAccount>> getBillingAccountsByStatus(
            @Parameter(description = "Account status") @PathVariable BillingAccountStatus status,
            Pageable pageable) {
        log.debug("Retrieving billing accounts with status: {}", status);
        
        Page<BillingAccount> accounts = billingAccountService.getBillingAccountsByStatus(status, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/search")
    @Operation(summary = "Search billing accounts")
    public ResponseEntity<Page<BillingAccount>> searchBillingAccounts(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) BillingAccountStatus status,
            @RequestParam(required = false) String currency,
            Pageable pageable) {
        log.debug("Searching billing accounts with criteria: companyName={}, country={}, status={}, currency={}", 
                 companyName, country, status, currency);
        
        Page<BillingAccount> accounts = billingAccountService.searchBillingAccounts(
                companyName, country, status, currency, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/outstanding")
    @Operation(summary = "Get accounts with outstanding balance")
    public ResponseEntity<List<BillingAccount>> getAccountsWithOutstandingBalance(
            @RequestParam(defaultValue = "0.01") BigDecimal minimumAmount) {
        log.debug("Retrieving accounts with outstanding balance > {}", minimumAmount);
        
        List<BillingAccount> accounts = billingAccountService.getAccountsWithOutstandingBalance(minimumAmount);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/over-credit-limit")
    @Operation(summary = "Get accounts over credit limit")
    public ResponseEntity<List<BillingAccount>> getAccountsOverCreditLimit() {
        log.debug("Retrieving accounts over credit limit");
        
        List<BillingAccount> accounts = billingAccountService.getAccountsOverCreditLimit();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update account status")
    public ResponseEntity<BillingAccount> updateAccountStatus(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @RequestParam BillingAccountStatus status) {
        log.info("Updating account status for {}: {}", id, status);
        
        BillingAccount updated = billingAccountService.updateAccountStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/balance")
    @Operation(summary = "Update account balance")
    public ResponseEntity<BillingAccount> updateAccountBalance(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @RequestParam BigDecimal amount,
            @RequestParam String reason) {
        log.info("Updating account balance for {}: {} ({})", id, amount, reason);
        
        BillingAccount updated = billingAccountService.updateAccountBalance(id, amount, reason);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/credit-limit")
    @Operation(summary = "Set credit limit")
    public ResponseEntity<BillingAccount> setCreditLimit(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @RequestParam BigDecimal creditLimit) {
        log.info("Setting credit limit for {}: {}", id, creditLimit);
        
        BillingAccount updated = billingAccountService.setCreditLimit(id, creditLimit);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/auto-pay/enable")
    @Operation(summary = "Enable auto-pay")
    public ResponseEntity<BillingAccount> enableAutoPay(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @RequestParam String paymentReference) {
        log.info("Enabling auto-pay for {}: {}", id, paymentReference);
        
        BillingAccount updated = billingAccountService.enableAutoPay(id, paymentReference);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/auto-pay/disable")
    @Operation(summary = "Disable auto-pay")
    public ResponseEntity<BillingAccount> disableAutoPay(
            @Parameter(description = "Billing account ID") @PathVariable UUID id) {
        log.info("Disabling auto-pay for {}", id);
        
        BillingAccount updated = billingAccountService.disableAutoPay(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "Close account")
    public ResponseEntity<BillingAccount> closeAccount(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @RequestParam String reason) {
        log.info("Closing account {}: {}", id, reason);
        
        BillingAccount updated = billingAccountService.closeAccount(id, reason);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/suspend")
    @Operation(summary = "Suspend account")
    public ResponseEntity<BillingAccount> suspendAccount(
            @Parameter(description = "Billing account ID") @PathVariable UUID id,
            @RequestParam String reason) {
        log.info("Suspending account {}: {}", id, reason);
        
        BillingAccount updated = billingAccountService.suspendAccount(id, reason);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate account")
    public ResponseEntity<BillingAccount> activateAccount(
            @Parameter(description = "Billing account ID") @PathVariable UUID id) {
        log.info("Activating account {}", id);
        
        BillingAccount updated = billingAccountService.activateAccount(id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/statistics/outstanding-balance")
    @Operation(summary = "Get total outstanding balance")
    public ResponseEntity<BigDecimal> getTotalOutstandingBalance() {
        BigDecimal total = billingAccountService.getTotalOutstandingBalance();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/statistics/active-count")
    @Operation(summary = "Get active account count")
    public ResponseEntity<Long> getActiveAccountCount() {
        Long count = billingAccountService.getActiveAccountCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/payment-issues")
    @Operation(summary = "Get accounts with payment issues")
    public ResponseEntity<List<BillingAccount>> getAccountsWithPaymentIssues() {
        List<BillingAccount> accounts = billingAccountService.getAccountsWithPaymentIssues();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}/credit-utilization")
    @Operation(summary = "Calculate credit utilization")
    public ResponseEntity<BigDecimal> calculateCreditUtilization(
            @Parameter(description = "Billing account ID") @PathVariable UUID id) {
        BigDecimal utilization = billingAccountService.calculateCreditUtilization(id);
        return ResponseEntity.ok(utilization);
    }
}