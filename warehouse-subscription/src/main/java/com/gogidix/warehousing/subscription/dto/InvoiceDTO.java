package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    
    private UUID id;
    private UUID subscriptionId;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private String currency;
    private String status; // DRAFT, SENT, PAID, OVERDUE, CANCELLED
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;
    private String customerEmail;
    private String customerName;
    private String billingAddress;
    private List<InvoiceLineItemDTO> lineItems;
    private String notes;
    private String paymentTerms;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class InvoiceLineItemDTO {
    
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String productCode;
}
