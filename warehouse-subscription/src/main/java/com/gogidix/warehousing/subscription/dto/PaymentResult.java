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
public class PaymentResult {
    
    private boolean success;
    private String message;
    private String transactionId;
    private String paymentIntentId;
    private UUID subscriptionId;
    private BigDecimal amount;
    private String currency;
    private String status; // SUCCEEDED, FAILED, PENDING, CANCELLED
    private LocalDateTime processedAt;
    private String failureReason;
    private String receiptUrl;
}
