package com.gogidix.warehousing.billing.service.impl;

import com.gogidix.warehousing.billing.model.BillingAccount;
import com.gogidix.warehousing.billing.model.BillingAccountStatus;
import com.gogidix.warehousing.billing.repository.BillingAccountRepository;
import com.gogidix.warehousing.billing.service.BillingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BillingAccountServiceImpl implements BillingAccountService {

    private final BillingAccountRepository billingAccountRepository;

    @Override
    public BillingAccount createBillingAccount(BillingAccount billingAccount) {
        log.info("Creating billing account for warehouse partner: {}", billingAccount.getWarehousePartnerId());
        billingAccount.setAccountStatus(BillingAccountStatus.ACTIVE);
        billingAccount.setCreatedAt(LocalDateTime.now());
        billingAccount.setUpdatedAt(LocalDateTime.now());
        billingAccount.setCurrentBalance(BigDecimal.ZERO);
        billingAccount.setOutstandingBalance(BigDecimal.ZERO);
        return billingAccountRepository.save(billingAccount);
    }

    @Override
    public BillingAccount getBillingAccountById(UUID id) {
        return billingAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing account not found: " + id));
    }
    @Override
    public BillingAccount getBillingAccountByWarehousePartnerId(UUID warehousePartnerId) {
        return billingAccountRepository.findByWarehousePartnerId(warehousePartnerId)
                .orElseThrow(() -> new RuntimeException("Billing account not found for warehouse partner: " + warehousePartnerId));
    }

    @Override
    public BillingAccount updateBillingAccount(UUID id, BillingAccount billingAccount) {
        BillingAccount existing = getBillingAccountById(id);
        existing.setAccountName(billingAccount.getAccountName());
        existing.setCompanyName(billingAccount.getCompanyName());
        existing.setBillingEmail(billingAccount.getBillingEmail());
        existing.setPhoneNumber(billingAccount.getPhoneNumber());
        existing.setPaymentTermsDays(billingAccount.getPaymentTermsDays());
        existing.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(existing);
    }

    @Override
    public void deleteBillingAccount(UUID id) {
        billingAccountRepository.deleteById(id);
    }

    @Override
    public Page<BillingAccount> getAllBillingAccounts(Pageable pageable) {
        return billingAccountRepository.findAll(pageable);
    }

    @Override
    public Page<BillingAccount> getBillingAccountsByStatus(BillingAccountStatus status, Pageable pageable) {
        return billingAccountRepository.findByAccountStatus(status, pageable);
    }

    @Override
    public Page<BillingAccount> searchBillingAccounts(String companyName, String country, 
                                                     BillingAccountStatus status, String currency, 
                                                     Pageable pageable) {
        return billingAccountRepository.searchBillingAccounts(companyName, country, status, currency, pageable);
    }
    @Override
    public List<BillingAccount> getAccountsWithOutstandingBalance(BigDecimal minimumAmount) {
        return billingAccountRepository.findByOutstandingBalanceGreaterThan(minimumAmount);
    }

    @Override
    public List<BillingAccount> getAccountsOverCreditLimit() {
        return billingAccountRepository.findAccountsOverCreditLimit();
    }

    @Override
    public List<BillingAccount> getAccountsDueForBilling(LocalDateTime date) {
        return billingAccountRepository.findByNextBillingDateBefore(date);
    }

    @Override
    public BillingAccount updateAccountStatus(UUID id, BillingAccountStatus status) {
        BillingAccount account = getBillingAccountById(id);
        account.setAccountStatus(status);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount updateAccountBalance(UUID id, BigDecimal amount, String reason) {
        BillingAccount account = getBillingAccountById(id);
        account.setCurrentBalance(account.getCurrentBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        log.info("Updated balance for account {}: {} (reason: {})", id, amount, reason);
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount updateOutstandingBalance(UUID id, BigDecimal amount) {
        BillingAccount account = getBillingAccountById(id);
        account.setOutstandingBalance(amount);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }
    @Override
    public BillingAccount setCreditLimit(UUID id, BigDecimal creditLimit) {
        BillingAccount account = getBillingAccountById(id);
        account.setCreditLimit(creditLimit);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount enableAutoPay(UUID id, String paymentReference) {
        BillingAccount account = getBillingAccountById(id);
        account.setAutoPayEnabled(true);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount disableAutoPay(UUID id) {
        BillingAccount account = getBillingAccountById(id);
        account.setAutoPayEnabled(false);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount updateBillingDates(UUID id, LocalDateTime nextBillingDate, LocalDateTime lastBillingDate) {
        BillingAccount account = getBillingAccountById(id);
        account.setNextBillingDate(nextBillingDate);
        account.setLastBillingDate(lastBillingDate);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount closeAccount(UUID id, String reason) {
        BillingAccount account = getBillingAccountById(id);
        account.setAccountStatus(BillingAccountStatus.CLOSED);
        account.setUpdatedAt(LocalDateTime.now());
        log.info("Closed account {}: {}", id, reason);
        return billingAccountRepository.save(account);
    }
    @Override
    public BillingAccount suspendAccount(UUID id, String reason) {
        BillingAccount account = getBillingAccountById(id);
        account.setAccountStatus(BillingAccountStatus.SUSPENDED);
        account.setUpdatedAt(LocalDateTime.now());
        log.info("Suspended account {}: {}", id, reason);
        return billingAccountRepository.save(account);
    }

    @Override
    public BillingAccount activateAccount(UUID id) {
        BillingAccount account = getBillingAccountById(id);
        account.setAccountStatus(BillingAccountStatus.ACTIVE);
        account.setUpdatedAt(LocalDateTime.now());
        return billingAccountRepository.save(account);
    }

    @Override
    public BigDecimal getTotalOutstandingBalance() {
        return billingAccountRepository.calculateTotalOutstandingBalance();
    }

    @Override
    public Long getActiveAccountCount() {
        return billingAccountRepository.countByAccountStatus(BillingAccountStatus.ACTIVE);
    }

    @Override
    public boolean existsByWarehousePartnerId(UUID warehousePartnerId) {
        return billingAccountRepository.existsByWarehousePartnerId(warehousePartnerId);
    }

    @Override
    public boolean existsByBillingEmail(String billingEmail) {
        return billingAccountRepository.existsByBillingEmail(billingEmail);
    }

    @Override
    public List<BillingAccount> getAccountsWithPaymentIssues() {
        return billingAccountRepository.findAccountsWithPaymentIssues();
    }
    @Override
    public BigDecimal calculateCreditUtilization(UUID id) {
        BillingAccount account = getBillingAccountById(id);
        if (account.getCreditLimit() == null || account.getCreditLimit().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return account.getOutstandingBalance().divide(account.getCreditLimit(), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public List<BillingAccount> getHighUtilizationAccounts(BigDecimal threshold) {
        return billingAccountRepository.findHighUtilizationAccounts(threshold);
    }

    @Override
    public void processAccountAging() {
        log.info("Processing account aging...");
        List<BillingAccount> accounts = billingAccountRepository.findAll();
        for (BillingAccount account : accounts) {
            // Process aging logic here
            log.debug("Processing aging for account: {}", account.getId());
        }
    }

    @Override
    public void sendBillingReminders() {
        log.info("Sending billing reminders...");
        List<BillingAccount> accountsDue = getAccountsDueForBilling(LocalDateTime.now().plusDays(7));
        for (BillingAccount account : accountsDue) {
            // Send reminder logic here
            log.info("Sending reminder to account: {}", account.getId());
        }
    }

    @Override
    public void generateAccountStatements(UUID billingAccountId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        log.info("Generating account statement for account {} from {} to {}", 
                billingAccountId, periodStart, periodEnd);
        BillingAccount account = getBillingAccountById(billingAccountId);
        // Generate statement logic here
    }
}