package com.exalt.warehousing.subscription.repository;

import com.exalt.warehousing.subscription.model.BillingRecord;
import com.exalt.warehousing.subscription.model.WarehouseSubscription;
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

/**
 * Repository interface for BillingRecord entities
 */
@Repository
public interface BillingRecordRepository extends JpaRepository<BillingRecord, String> {

    /**
     * Find billing records by subscription
     */
    Page<BillingRecord> findBySubscription(WarehouseSubscription subscription, Pageable pageable);

    /**
     * Find billing records by subscription and status
     */
    List<BillingRecord> findBySubscriptionAndStatus(WarehouseSubscription subscription, BillingRecord.BillingStatus status);

    /**
     * Find billing records by status
     */
    List<BillingRecord> findByStatus(BillingRecord.BillingStatus status);

    /**
     * Find billing records by billing type
     */
    List<BillingRecord> findByBillingType(BillingRecord.BillingType billingType);

    /**
     * Find billing record by invoice number
     */
    Optional<BillingRecord> findByInvoiceNumber(String invoiceNumber);

    /**
     * Find billing records by Stripe invoice ID
     */
    Optional<BillingRecord> findByStripeInvoiceId(String stripeInvoiceId);

    /**
     * Find overdue billing records
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.dueDate < :currentDate AND " +
           "b.status NOT IN ('PAID', 'CANCELLED', 'REFUNDED') AND b.dueDate IS NOT NULL")
    List<BillingRecord> findOverdueBillingRecords(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find unpaid billing records
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.status IN ('SENT', 'PENDING', 'FAILED', 'OVERDUE', 'PARTIAL')")
    List<BillingRecord> findUnpaidBillingRecords();

    /**
     * Find billing records due soon
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.dueDate BETWEEN :startDate AND :endDate AND " +
           "b.status NOT IN ('PAID', 'CANCELLED', 'REFUNDED')")
    List<BillingRecord> findBillingRecordsDueSoon(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Find billing records by date range
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.billingDate BETWEEN :startDate AND :endDate")
    List<BillingRecord> findByBillingDateBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find billing records by period
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.periodStart >= :periodStart AND b.periodEnd <= :periodEnd")
    List<BillingRecord> findByPeriod(@Param("periodStart") LocalDateTime periodStart,
                                   @Param("periodEnd") LocalDateTime periodEnd);

    /**
     * Calculate total revenue for period
     */
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM BillingRecord b WHERE " +
           "b.status = 'PAID' AND b.billingDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueForPeriod(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate outstanding balance
     */
    @Query("SELECT COALESCE(SUM(b.balance), 0) FROM BillingRecord b WHERE " +
           "b.status IN ('SENT', 'PENDING', 'FAILED', 'OVERDUE', 'PARTIAL')")
    BigDecimal calculateOutstandingBalance();

    /**
     * Find failed payment records for retry
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.status = 'FAILED' AND " +
           "(b.nextRetryDate IS NULL OR b.nextRetryDate <= :currentDate) AND " +
           "b.paymentAttempts < :maxAttempts")
    List<BillingRecord> findFailedPaymentsForRetry(@Param("currentDate") LocalDateTime currentDate,
                                                  @Param("maxAttempts") int maxAttempts);

    /**
     * Count billing records by status
     */
    long countByStatus(BillingRecord.BillingStatus status);

    /**
     * Count billing records by type
     */
    long countByBillingType(BillingRecord.BillingType billingType);

    /**
     * Find billing records by transaction ID
     */
    Optional<BillingRecord> findByTransactionId(String transactionId);

    /**
     * Find latest billing record for subscription
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.subscription = :subscription " +
           "ORDER BY b.billingDate DESC")
    List<BillingRecord> findLatestBySubscription(@Param("subscription") WarehouseSubscription subscription,
                                               Pageable pageable);

    /**
     * Calculate average payment time
     */
    @Query("SELECT AVG(DATEDIFF(b.paidDate, b.billingDate)) FROM BillingRecord b WHERE " +
           "b.status = 'PAID' AND b.paidDate IS NOT NULL AND b.billingDate >= :since")
    Double calculateAveragePaymentTime(@Param("since") LocalDateTime since);

    /**
     * Find refunded billing records
     */
    List<BillingRecord> findByStatus(BillingRecord.BillingStatus status, Pageable pageable);

    /**
     * Find billing records requiring manual review
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.paymentAttempts >= :maxAttempts OR " +
           "(b.status = 'FAILED' AND b.failureReason LIKE '%fraud%')")
    List<BillingRecord> findRequiringManualReview(@Param("maxAttempts") int maxAttempts);

    /**
     * Calculate success rate for period
     */
    @Query("SELECT (COUNT(b) * 100.0 / (SELECT COUNT(b2) FROM BillingRecord b2 WHERE " +
           "b2.billingDate BETWEEN :startDate AND :endDate)) FROM BillingRecord b WHERE " +
           "b.status = 'PAID' AND b.billingDate BETWEEN :startDate AND :endDate")
    Double calculatePaymentSuccessRate(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find high-value transactions
     */
    @Query("SELECT b FROM BillingRecord b WHERE b.totalAmount >= :threshold ORDER BY b.totalAmount DESC")
    List<BillingRecord> findHighValueTransactions(@Param("threshold") BigDecimal threshold, Pageable pageable);

    /**
     * Delete old billing records
     */
    void deleteByBillingDateBefore(LocalDateTime cutoffDate);
}
