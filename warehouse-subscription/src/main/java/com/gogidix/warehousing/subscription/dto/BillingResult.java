package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingResult {
    
    private boolean success;
    private String message;
    private UUID invoiceId;
    private BigDecimal amountBilled;
    private BigDecimal amountPaid;
    private BigDecimal outstandingAmount;
    private String status; // PAID, PENDING, FAILED, OVERDUE
    private String transactionId;
    private LocalDateTime billingDate;
    private LocalDateTime dueDate;
    private String errorCode;
    private String errorMessage;
}
