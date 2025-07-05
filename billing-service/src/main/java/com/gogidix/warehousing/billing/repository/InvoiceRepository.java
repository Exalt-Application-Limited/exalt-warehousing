package com.gogidix.warehousing.billing.repository;

import com.gogidix.warehousing.billing.model.Invoice;
import com.gogidix.warehousing.billing.model.InvoiceStatus;
import com.gogidix.warehousing.billing.model.InvoiceType;
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
 * Repository interface for Invoice entity
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    /**
     * Find invoice by invoice number
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Find invoices by billing account
     */
    Page<Invoice> findByBillingAccount_Id(UUID billingAccountId, Pageable pageable);

    /**
     * Find invoices by status
     */
    Page<Invoice> findByInvoiceStatus(InvoiceStatus status, Pageable pageable);

    /**
     * Find invoices by type
     */
    Page<Invoice> findByInvoiceType(InvoiceType type, Pageable pageable);

    /**
     * Find overdue invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus NOT IN ('PAID', 'CANCELLED', 'VOID') AND i.dueDate < CURRENT_TIMESTAMP")
    List<Invoice> findOverdueInvoices();

    /**
     * Find invoices due soon
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus IN ('SENT', 'VIEWED') AND " +
           "i.dueDate BETWEEN CURRENT_TIMESTAMP AND :endDate")
    List<Invoice> findInvoicesDueSoon(@Param("endDate") LocalDateTime endDate);

    /**
     * Find unpaid invoices for billing account
     */
    @Query("SELECT i FROM Invoice i WHERE i.billingAccount.id = :billingAccountId AND " +
           "i.invoiceStatus NOT IN ('PAID', 'CANCELLED', 'VOID')")
    List<Invoice> findUnpaidInvoicesByBillingAccount(@Param("billingAccountId") UUID billingAccountId);

    /**
     * Find invoices by date range
     */
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find invoices by due date range
     */
    List<Invoice> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find invoices by billing period
     */
    List<Invoice> findByBillingPeriodStartAndBillingPeriodEnd(
            LocalDateTime periodStart, LocalDateTime periodEnd);

    /**
     * Find invoices by currency
     */
    List<Invoice> findByCurrency(String currency);

    /**
     * Get total outstanding amount
     */
    @Query("SELECT COALESCE(SUM(i.amountDue), 0) FROM Invoice i WHERE " +
           "i.invoiceStatus NOT IN ('PAID', 'CANCELLED', 'VOID')")
    BigDecimal getTotalOutstandingAmount();

    /**
     * Get total outstanding amount by currency
     */
    @Query("SELECT COALESCE(SUM(i.amountDue), 0) FROM Invoice i WHERE " +
           "i.currency = :currency AND i.invoiceStatus NOT IN ('PAID', 'CANCELLED', 'VOID')")
    BigDecimal getTotalOutstandingAmountByCurrency(@Param("currency") String currency);

    /**
     * Get total paid amount for period
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE " +
           "i.invoiceStatus = 'PAID' AND i.paidDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPaidAmountForPeriod(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Find invoices with auto-pay attempted
     */
    List<Invoice> findByAutoPayAttemptedTrue();

    /**
     * Find invoices with failed auto-pay
     */
    @Query("SELECT i FROM Invoice i WHERE i.autoPayAttempted = true AND " +
           "i.autoPayFailureReason IS NOT NULL")
    List<Invoice> findInvoicesWithFailedAutoPay();

    /**
     * Search invoices by multiple criteria
     */
    @Query("SELECT i FROM Invoice i WHERE " +
           "(:billingAccountId IS NULL OR i.billingAccount.id = :billingAccountId) AND " +
           "(:status IS NULL OR i.invoiceStatus = :status) AND " +
           "(:type IS NULL OR i.invoiceType = :type) AND " +
           "(:currency IS NULL OR i.currency = :currency) AND " +
           "(:startDate IS NULL OR i.invoiceDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.invoiceDate <= :endDate)")
    Page<Invoice> searchInvoices(
            @Param("billingAccountId") UUID billingAccountId,
            @Param("status") InvoiceStatus status,
            @Param("type") InvoiceType type,
            @Param("currency") String currency,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find invoices by PO number
     */
    List<Invoice> findByPoNumber(String poNumber);

    /**
     * Find recurring invoices
     */
    List<Invoice> findByIsRecurringTrue();

    /**
     * Find invoices by parent invoice
     */
    List<Invoice> findByParentInvoiceId(UUID parentInvoiceId);

    /**
     * Get invoice count by status
     */
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceStatus = :status")
    Long getCountByStatus(@Param("status") InvoiceStatus status);

    /**
     * Get next invoice number sequence
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.invoiceNumber, LENGTH(:prefix) + 2) AS integer)), 0) + 1 " +
           "FROM Invoice i WHERE i.invoiceNumber LIKE CONCAT(:prefix, '-%')")
    Integer getNextInvoiceNumber(@Param("prefix") String prefix);

    /**
     * Find invoices needing late fees
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus = 'OVERDUE' AND " +
           "i.lateFee IS NULL AND i.dueDate < :cutoffDate")
    List<Invoice> findInvoicesNeedingLateFees(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find recently created invoices
     */
    List<Invoice> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find invoices by billing account and date range
     */
    List<Invoice> findByBillingAccount_IdAndInvoiceDateBetween(
            UUID billingAccountId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get revenue by period and type
     */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE " +
           "i.invoiceStatus = 'PAID' AND i.invoiceType = :type AND " +
           "i.paidDate BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByPeriodAndType(@Param("type") InvoiceType type,
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Update invoice status
     */
    @Query("UPDATE Invoice i SET i.invoiceStatus = :status WHERE i.id = :id")
    void updateInvoiceStatus(@Param("id") UUID id, @Param("status") InvoiceStatus status);

    /**
     * Update amount paid
     */
    @Query("UPDATE Invoice i SET i.amountPaid = :amountPaid, i.amountDue = i.totalAmount - :amountPaid WHERE i.id = :id")
    void updateAmountPaid(@Param("id") UUID id, @Param("amountPaid") BigDecimal amountPaid);

    /**
     * Find partially paid invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus = 'PARTIALLY_PAID'")
    List<Invoice> findPartiallyPaidInvoices();

    /**
     * Find invoices with discounts
     */
    @Query("SELECT i FROM Invoice i WHERE i.discountAmount IS NOT NULL AND i.discountAmount > 0")
    List<Invoice> findInvoicesWithDiscounts();

    /**
     * Get average invoice amount by type
     */
    @Query("SELECT AVG(i.totalAmount) FROM Invoice i WHERE i.invoiceType = :type")
    BigDecimal getAverageInvoiceAmountByType(@Param("type") InvoiceType type);
}