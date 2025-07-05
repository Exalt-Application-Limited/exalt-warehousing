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
public class SubscriptionDetailDTO {
    
    private UUID id;
    private UUID warehouseId;
    private String warehouseName;
    private String planId;
    private String planName;
    private String status;
    private String customerEmail;
    private String customerName;
    private String companyName;
    private String billingPeriod;
    private BigDecimal monthlyPrice;
    private BigDecimal totalPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextBillingDate;
    private boolean autoRenewal;
    private String paymentStatus;
    private String paymentMethodId;
    private List<String> features;
    private UsageSummary currentUsage;
    private BigDecimal outstandingBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}
