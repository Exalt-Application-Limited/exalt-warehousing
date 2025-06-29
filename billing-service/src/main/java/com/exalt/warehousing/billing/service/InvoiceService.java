package com.exalt.warehousing.billing.service;

import com.exalt.warehousing.billing.model.Invoice;
import com.exalt.warehousing.billing.model.InvoiceStatus;
import com.exalt.warehousing.billing.model.InvoiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for invoice operations
 */
public interface InvoiceService {

    /**
     * Create a new invoice
     */
    Invoice createInvoice(Invoice invoice);

    /**
     * Get invoice by ID
     */
    Invoice getInvoiceById(UUID id);

    /**
     * Get invoice by invoice number
     */
    Invoice getInvoiceByNumber(String invoiceNumber);

    /**
     * Update invoice
     */
    Invoice updateInvoice(UUID id, Invoice invoice);

    /**
     * Delete invoice
     */
    void deleteInvoice(UUID id);

    /**
     * Get all invoices with pagination
     */
    Page<Invoice> getAllInvoices(Pageable pageable);

    /**
     * Get invoices by billing account
     */
    Page<Invoice> getInvoicesByBillingAccount(UUID billingAccountId, Pageable pageable);

    /**
     * Get invoices by status
     */
    Page<Invoice> getInvoicesByStatus(InvoiceStatus status, Pageable pageable);

    /**
     * Search invoices
     */
    Page<Invoice> searchInvoices(UUID billingAccountId, InvoiceStatus status, InvoiceType type,
                               String currency, LocalDateTime startDate, LocalDateTime endDate,
                               Pageable pageable);

    /**
     * Generate invoice from subscription
     */
    Invoice generateSubscriptionInvoice(UUID subscriptionId, LocalDateTime billingPeriodStart, 
                                      LocalDateTime billingPeriodEnd);

    /**
     * Generate usage-based invoice
     */
    Invoice generateUsageInvoice(UUID subscriptionId, UUID usageRecordId);

    /**
     * Generate next invoice number
     */
    String generateInvoiceNumber(String prefix);

    /**
     * Send invoice
     */
    void sendInvoice(UUID invoiceId);

    /**
     * Mark invoice as paid
     */
    Invoice markAsPaid(UUID invoiceId, BigDecimal amountPaid, String paymentReference);

    /**
     * Apply payment to invoice
     */
    Invoice applyPayment(UUID invoiceId, UUID paymentId);

    /**
     * Cancel invoice
     */
    Invoice cancelInvoice(UUID invoiceId, String reason);

    /**
     * Void invoice
     */
    Invoice voidInvoice(UUID invoiceId, String reason);

    /**
     * Apply discount to invoice
     */
    Invoice applyDiscount(UUID invoiceId, BigDecimal discountAmount, String reason);

    /**
     * Apply late fee to invoice
     */
    Invoice applyLateFee(UUID invoiceId, BigDecimal lateFeeAmount);

    /**
     * Process auto-pay for invoice
     */
    void processAutoPay(UUID invoiceId);

    /**
     * Get overdue invoices
     */
    List<Invoice> getOverdueInvoices();

    /**
     * Get invoices due soon
     */
    List<Invoice> getInvoicesDueSoon(int daysAhead);

    /**
     * Get unpaid invoices for account
     */
    List<Invoice> getUnpaidInvoices(UUID billingAccountId);

    /**
     * Calculate total outstanding amount
     */
    BigDecimal getTotalOutstandingAmount();

    /**
     * Calculate total outstanding amount by currency
     */
    BigDecimal getTotalOutstandingAmountByCurrency(String currency);

    /**
     * Get revenue for period
     */
    BigDecimal getRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get revenue by type and period
     */
    BigDecimal getRevenueByTypeAndPeriod(InvoiceType type, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Process overdue invoices
     */
    void processOverdueInvoices();

    /**
     * Send payment reminders
     */
    void sendPaymentReminders();

    /**
     * Generate credit note
     */
    Invoice generateCreditNote(UUID originalInvoiceId, BigDecimal creditAmount, String reason);

    /**
     * Recalculate invoice totals
     */
    Invoice recalculateInvoiceTotals(UUID invoiceId);

    /**
     * Get invoice statistics
     */
    InvoiceStatistics getInvoiceStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Export invoices to PDF
     */
    byte[] exportInvoiceToPdf(UUID invoiceId);

    /**
     * Bulk update invoice status
     */
    void bulkUpdateInvoiceStatus(List<UUID> invoiceIds, InvoiceStatus newStatus);

    /**
     * Process recurring invoices
     */
    void processRecurringInvoices();

    /**
     * Invoice statistics DTO
     */
    record InvoiceStatistics(
            long totalInvoices,
            long paidInvoices,
            long overdueInvoices,
            BigDecimal totalAmount,
            BigDecimal paidAmount,
            BigDecimal overdueAmount,
            BigDecimal averageInvoiceAmount,
            BigDecimal averagePaymentTime
    ) {}
}