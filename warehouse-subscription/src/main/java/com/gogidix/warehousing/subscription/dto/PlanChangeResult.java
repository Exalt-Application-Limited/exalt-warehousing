package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanChangeResult {
    
    private boolean success;
    private String message;
    private String transactionId;
    private String oldPlanId;
    private String newPlanId;
    private LocalDateTime effectiveDate;
    private BigDecimal proratedAmount;
    private BigDecimal refundAmount;
    private String status;
}
