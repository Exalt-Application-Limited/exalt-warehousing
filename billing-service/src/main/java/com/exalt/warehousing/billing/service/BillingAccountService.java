package com.exalt.warehousing.billing.service;

import com.exalt.warehousing.billing.model.BillingAccount;
import com.exalt.warehousing.billing.model.BillingAccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for billing account operations
 */
public interface BillingAccountService {

    /**
     * Create a new billing account
     */
    BillingAccount createBillingAccount(BillingAccount billingAccount);

    /**
     * Get billing account by ID
     */
    BillingAccount getBillingAccountById(UUID id);

    /**
     * Get billing account by warehouse partner ID
     */
    BillingAccount getBillingAccountByWarehousePartnerId(UUID warehousePartnerId);

    /**
     * Update billing account
     */
    BillingAccount updateBillingAccount(UUID id, BillingAccount billingAccount);

    /**
     * Delete billing account
     */
    void deleteBillingAccount(UUID id);

    /**
     * Get all billing accounts with pagination
     */
    Page<BillingAccount> getAllBillingAccounts(Pageable pageable);

    /**
     * Get billing accounts by status
     */
    Page<BillingAccount> getBillingAccountsByStatus(BillingAccountStatus status, Pageable pageable);

    /**
     * Search billing accounts
     */
    Page<BillingAccount> searchBillingAccounts(String companyName, String country, 
                                             BillingAccountStatus status, String currency, 
                                             Pageable pageable);

    /**
     * Get accounts with outstanding balance
     */
    List<BillingAccount> getAccountsWithOutstandingBalance(BigDecimal minimumAmount);

    /**
     * Get accounts over credit limit
     */
    List<BillingAccount> getAccountsOverCreditLimit();

    /**
     * Get accounts due for billing
     */
    List<BillingAccount> getAccountsDueForBilling(LocalDateTime date);

    /**
     * Update account status
     */
    BillingAccount updateAccountStatus(UUID id, BillingAccountStatus status);

    /**
     * Update account balance
     */
    BillingAccount updateAccountBalance(UUID id, BigDecimal amount, String reason);

    /**
     * Update outstanding balance
     */
    BillingAccount updateOutstandingBalance(UUID id, BigDecimal amount);

    /**
     * Set credit limit
     */
    BillingAccount setCreditLimit(UUID id, BigDecimal creditLimit);

    /**
     * Enable auto-pay
     */
    BillingAccount enableAutoPay(UUID id, String paymentReference);

    /**
     * Disable auto-pay
     */
    BillingAccount disableAutoPay(UUID id);

    /**
     * Update billing dates
     */
    BillingAccount updateBillingDates(UUID id, LocalDateTime nextBillingDate, LocalDateTime lastBillingDate);

    /**
     * Close account
     */
    BillingAccount closeAccount(UUID id, String reason);

    /**
     * Suspend account
     */
    BillingAccount suspendAccount(UUID id, String reason);

    /**
     * Activate account
     */
    BillingAccount activateAccount(UUID id);

    /**
     * Get total outstanding balance across all accounts
     */
    BigDecimal getTotalOutstandingBalance();

    /**
     * Get active account count
     */
    Long getActiveAccountCount();

    /**
     * Check if account exists by warehouse partner ID
     */
    boolean existsByWarehousePartnerId(UUID warehousePartnerId);

    /**
     * Check if account exists by billing email
     */
    boolean existsByBillingEmail(String billingEmail);

    /**
     * Get accounts with payment issues
     */
    List<BillingAccount> getAccountsWithPaymentIssues();

    /**
     * Calculate credit utilization
     */
    BigDecimal calculateCreditUtilization(UUID id);

    /**
     * Get high utilization accounts
     */
    List<BillingAccount> getHighUtilizationAccounts(BigDecimal threshold);

    /**
     * Process account aging
     */
    void processAccountAging();

    /**
     * Send billing reminders
     */
    void sendBillingReminders();

    /**
     * Generate account statements
     */
    void generateAccountStatements(UUID billingAccountId, LocalDateTime periodStart, LocalDateTime periodEnd);
}