package com.gogidix.warehousing.billing.repository;

import com.gogidix.warehousing.billing.model.BillingAccount;
import com.gogidix.warehousing.billing.model.BillingAccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BillingAccount entity
 */
@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccount, UUID> {

    /**
     * Find billing account by warehouse partner ID
     */
    Optional<BillingAccount> findByWarehousePartnerId(UUID warehousePartnerId);

    /**
     * Find billing account by billing email
     */
    Optional<BillingAccount> findByBillingEmail(String billingEmail);

    /**
     * Find billing accounts by status
     */
    Page<BillingAccount> findByAccountStatus(BillingAccountStatus status, Pageable pageable);

    /**
     * Find billing accounts by company name (case-insensitive)
     */
    Page<BillingAccount> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);

    /**
     * Find billing accounts by country
     */
    List<BillingAccount> findByCountry(String country);

    /**
     * Find billing accounts by preferred currency
     */
    List<BillingAccount> findByPreferredCurrency(String currency);

    /**
     * Find accounts with outstanding balance greater than specified amount
     */
    List<BillingAccount> findByOutstandingBalanceGreaterThan(BigDecimal amount);

    /**
     * Find accounts that are over their credit limit
     */
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.creditLimit IS NOT NULL AND ba.currentBalance < -(ba.creditLimit)")
    List<BillingAccount> findAccountsOverCreditLimit();

    /**
     * Find accounts with next billing date before specified date
     */
    List<BillingAccount> findByNextBillingDateBefore(LocalDateTime date);

    /**
     * Find accounts with auto-pay enabled
     */
    List<BillingAccount> findByAutoPayEnabledTrue();

    /**
     * Find accounts by payment terms
     */
    List<BillingAccount> findByPaymentTermsDays(Integer paymentTermsDays);

    /**
     * Find accounts created after specified date
     */
    List<BillingAccount> findByAccountOpenedDateAfter(LocalDateTime date);

    /**
     * Find accounts that haven't been billed recently
     */
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.lastBillingDate IS NULL OR ba.lastBillingDate < :cutoffDate")
    List<BillingAccount> findAccountsNotBilledSince(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find accounts with high utilization (usage vs credit limit)
     */
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.creditLimit IS NOT NULL AND " +
           "(ba.creditLimit - ba.currentBalance) / ba.creditLimit > :utilizationThreshold")
    List<BillingAccount> findHighUtilizationAccounts(@Param("utilizationThreshold") BigDecimal utilizationThreshold);

    /**
     * Get total outstanding balance across all accounts
     */
    @Query("SELECT COALESCE(SUM(ba.outstandingBalance), 0) FROM BillingAccount ba WHERE ba.accountStatus = 'ACTIVE'")
    BigDecimal calculateTotalOutstandingBalance();

    /**
     * Count by account status
     */
    Long countByAccountStatus(BillingAccountStatus status);

    /**
     * Check if account exists by warehouse partner ID
     */
    boolean existsByWarehousePartnerId(UUID warehousePartnerId);

    /**
     * Check if account exists by billing email
     */
    boolean existsByBillingEmail(String billingEmail);

    /**
     * Find accounts by tax ID
     */
    Optional<BillingAccount> findByTaxId(String taxId);

    /**
     * Find accounts by VAT number
     */
    Optional<BillingAccount> findByVatNumber(String vatNumber);

    /**
     * Find accounts with payment issues
     */
    @Query("SELECT ba FROM BillingAccount ba WHERE ba.accountStatus IN ('PAYMENT_OVERDUE', 'CREDIT_HOLD')")
    List<BillingAccount> findAccountsWithPaymentIssues();

    /**
     * Search accounts by multiple criteria
     */
    @Query("SELECT ba FROM BillingAccount ba WHERE " +
           "(:companyName IS NULL OR LOWER(ba.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:country IS NULL OR ba.country = :country) AND " +
           "(:status IS NULL OR ba.accountStatus = :status) AND " +
           "(:currency IS NULL OR ba.preferredCurrency = :currency)")
    Page<BillingAccount> searchBillingAccounts(
            @Param("companyName") String companyName,
            @Param("country") String country,
            @Param("status") BillingAccountStatus status,
            @Param("currency") String currency,
            Pageable pageable);

    /**
     * Find accounts with billing cycle day
     */
    List<BillingAccount> findByBillingCycleDay(Integer billingCycleDay);

    /**
     * Find accounts due for billing today
     */
    @Query("SELECT ba FROM BillingAccount ba WHERE " +
           "ba.accountStatus = 'ACTIVE' AND " +
           "ba.nextBillingDate IS NOT NULL AND " +
           "DATE(ba.nextBillingDate) = CURRENT_DATE")
    List<BillingAccount> findAccountsDueForBillingToday();

    /**
     * Update next billing date for account
     */
    @Query("UPDATE BillingAccount ba SET ba.nextBillingDate = :nextBillingDate, ba.lastBillingDate = :lastBillingDate WHERE ba.id = :id")
    void updateBillingDates(@Param("id") UUID id, 
                           @Param("nextBillingDate") LocalDateTime nextBillingDate,
                           @Param("lastBillingDate") LocalDateTime lastBillingDate);

    /**
     * Update account balance
     */
    @Query("UPDATE BillingAccount ba SET ba.currentBalance = ba.currentBalance + :amount WHERE ba.id = :id")
    void updateBalance(@Param("id") UUID id, @Param("amount") BigDecimal amount);

    /**
     * Update outstanding balance
     */
    @Query("UPDATE BillingAccount ba SET ba.outstandingBalance = ba.outstandingBalance + :amount WHERE ba.id = :id")
    void updateOutstandingBalance(@Param("id") UUID id, @Param("amount") BigDecimal amount);
}