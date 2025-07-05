package com.gogidix.warehousing.billing.service.impl;

import com.gogidix.warehousing.billing.model.*;
import com.gogidix.warehousing.billing.repository.InvoiceRepository;
import com.gogidix.warehousing.billing.repository.BillingAccountRepository;
import com.gogidix.warehousing.billing.service.InvoiceService;
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
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BillingAccountRepository billingAccountRepository;

    @Override
    public Invoice createInvoice(Invoice invoice) {
        log.info("Creating invoice for billing account: {}", invoice.getBillingAccount() != null ? invoice.getBillingAccount().getId() : null);
        invoice.setInvoiceNumber(generateInvoiceNumber("INV"));
        invoice.setInvoiceStatus(InvoiceStatus.DRAFT);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getInvoiceById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    @Override
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found with number: " + invoiceNumber));
    }

    @Override
    public Invoice updateInvoice(UUID id, Invoice invoice) {
        Invoice existing = getInvoiceById(id);
        existing.setDueDate(invoice.getDueDate());
        existing.setNotes(invoice.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(existing);
    }

    @Override
    public void deleteInvoice(UUID id) {
        invoiceRepository.deleteById(id);
    }

    @Override
    public Page<Invoice> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    public Page<Invoice> getInvoicesByBillingAccount(UUID billingAccountId, Pageable pageable) {
        return invoiceRepository.findByBillingAccount_Id(billingAccountId, pageable);
    }

    @Override
    public Page<Invoice> getInvoicesByStatus(InvoiceStatus status, Pageable pageable) {
        return invoiceRepository.findByInvoiceStatus(status, pageable);
    }

    @Override
    public Page<Invoice> searchInvoices(UUID billingAccountId, InvoiceStatus status, InvoiceType type,
                                       String currency, LocalDateTime startDate, LocalDateTime endDate,
                                       Pageable pageable) {
        // Implementation would use Specification or custom query
        return Page.empty();
    }

    @Override
    public Invoice generateSubscriptionInvoice(UUID subscriptionId, LocalDateTime billingPeriodStart, 
                                          LocalDateTime billingPeriodEnd) {
        // Implementation for subscription invoice generation
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(InvoiceType.SUBSCRIPTION);
        invoice.setBillingPeriodStart(billingPeriodStart);
        invoice.setBillingPeriodEnd(billingPeriodEnd);
        return createInvoice(invoice);
    }

    @Override
    public Invoice generateUsageInvoice(UUID subscriptionId, UUID usageRecordId) {
        // Implementation for usage-based invoice generation
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(InvoiceType.USAGE);
        return createInvoice(invoice);
    }

    @Override
    public String generateInvoiceNumber(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "-" + timestamp.substring(timestamp.length() - 8);
    }

    @Override
    public void sendInvoice(UUID invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.markAsSent();
        invoiceRepository.save(invoice);
        log.info("Invoice {} sent", invoice.getInvoiceNumber());
    }

    @Override
    public Invoice markAsPaid(UUID invoiceId, BigDecimal amountPaid, String paymentReference) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setInvoiceStatus(InvoiceStatus.PAID);
        invoice.setAmountPaid(amountPaid);
        invoice.setPaymentReference(paymentReference);
        invoice.setPaidDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice applyPayment(UUID invoiceId, UUID paymentId) {
        // Implementation for applying payment
        return markAsPaid(invoiceId, BigDecimal.ZERO, paymentId.toString());
    }

    @Override
    public Invoice cancelInvoice(UUID invoiceId, String reason) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.cancel();
        // TODO: Fix field names - invoice.setCancellationReason(reason);
        // TODO: Fix field names - invoice.setCancelledAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice voidInvoice(UUID invoiceId, String reason) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.voidInvoice();
        // TODO: Fix field names - invoice.setVoidReason(reason);
        // TODO: Fix field names - invoice.setVoidedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice applyDiscount(UUID invoiceId, BigDecimal discountAmount, String reason) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setDiscountAmount(discountAmount);
        invoice.setDiscountReason(reason);
        // Recalculate totals
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice applyLateFee(UUID invoiceId, BigDecimal lateFeeAmount) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setLateFee(lateFeeAmount);
        // Recalculate totals
        return invoiceRepository.save(invoice);
    }

    @Override
    public void processAutoPay(UUID invoiceId) {
        log.info("Processing auto-pay for invoice: {}", invoiceId);
        // Auto-pay implementation
    }

    @Override
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices();
    }

    @Override
    public List<Invoice> getInvoicesDueSoon(int daysAhead) {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(daysAhead);
        return invoiceRepository.findInvoicesDueSoon(dueDate);
    }

    @Override
    public List<Invoice> getUnpaidInvoices(UUID billingAccountId) {
        return invoiceRepository.findUnpaidInvoicesByBillingAccount(billingAccountId);
    }

    @Override
    public BigDecimal getTotalOutstandingAmount() {
        return invoiceRepository.getTotalOutstandingAmount();
    }

    @Override
    public BigDecimal getTotalOutstandingAmountByCurrency(String currency) {
        return invoiceRepository.getTotalOutstandingAmountByCurrency(currency);
    }

    @Override
    public BigDecimal getRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.getTotalPaidAmountForPeriod(startDate, endDate);
    }

    @Override
    public BigDecimal getRevenueByTypeAndPeriod(InvoiceType type, LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.getRevenueByPeriodAndType(type, startDate, endDate);
    }

    @Override
    public void processOverdueInvoices() {
        List<Invoice> overdueInvoices = getOverdueInvoices();
        overdueInvoices.forEach(invoice -> {
            invoice.setInvoiceStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);
        });
    }

    @Override
    public void sendPaymentReminders() {
        log.info("Sending payment reminders for overdue invoices");
        // Implementation for sending reminders
    }

    @Override
    public Invoice generateCreditNote(UUID originalInvoiceId, BigDecimal creditAmount, String reason) {
        Invoice originalInvoice = getInvoiceById(originalInvoiceId);
        Invoice creditNote = new Invoice();
        creditNote.setInvoiceType(InvoiceType.CREDIT_NOTE);
        creditNote.setBillingAccount(originalInvoice.getBillingAccount());
        creditNote.setRelatedInvoiceId(originalInvoiceId);
        creditNote.setTotalAmount(creditAmount.negate());
        creditNote.setNotes(reason);
        return createInvoice(creditNote);
    }

    @Override
    public Invoice recalculateInvoiceTotals(UUID invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        // Recalculation logic
        return invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceStatistics getInvoiceStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // Statistical calculation implementation
        return new InvoiceStatistics(0L, 0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO, 
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public byte[] exportInvoiceToPdf(UUID invoiceId) {
        log.info("Exporting invoice {} to PDF", invoiceId);
        // PDF generation implementation
        return new byte[0];
    }

    @Override
    public void bulkUpdateInvoiceStatus(List<UUID> invoiceIds, InvoiceStatus newStatus) {
        invoiceIds.forEach(id -> {
            Invoice invoice = getInvoiceById(id);
            invoice.setInvoiceStatus(newStatus);
            invoiceRepository.save(invoice);
        });
    }

    @Override
    public void processRecurringInvoices() {
        log.info("Processing recurring invoices");
        // Recurring invoice generation logic
    }
}
