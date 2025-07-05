package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkBillingResult {
    
    private boolean success;
    private String message;
    private int totalProcessed;
    private int successfulBills;
    private int failedBills;
    private BigDecimal totalAmount;
    private BigDecimal amountCollected;
    private List<BillingResult> results;
    private LocalDateTime processedAt;
    private String batchId;
}
