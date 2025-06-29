package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAnalytics {
    
    private int totalSubscriptions;
    private int activeSubscriptions;
    private int pausedSubscriptions;
    private int cancelledSubscriptions;
    private int trialSubscriptions;
    private BigDecimal conversionRate;
    private BigDecimal renewalRate;
    private BigDecimal upgradeRate;
    private BigDecimal downgradeRate;
    private Map<String, Integer> subscriptionsByPlan;
    private Map<String, Integer> subscriptionsByStatus;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private BigDecimal averageSubscriptionValue;
    private int newSignups;
    private int reactivations;
}
