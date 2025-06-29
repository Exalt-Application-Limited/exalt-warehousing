package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatusDTO {
    
    private UUID subscriptionId;
    private String status; // ACTIVE, PAUSED, CANCELLED, TRIAL, EXPIRED, SUSPENDED
    private String planId;
    private String planName;
    private LocalDateTime statusChangedAt;
    private LocalDateTime nextBillingDate;
    private LocalDateTime expirationDate;
    private boolean autoRenewal;
    private String paymentStatus;
    private BigDecimal outstandingBalance;
    private int daysUntilExpiration;
    private boolean isOverdue;
    private String statusReason;
    private String customerEmail;
    private String customerName;
}
