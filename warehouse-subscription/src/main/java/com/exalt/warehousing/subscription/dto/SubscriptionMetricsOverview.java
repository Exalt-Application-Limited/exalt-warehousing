package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionMetricsOverview {
    
    private RevenueAnalytics revenueMetrics;
    private ChurnAnalytics churnMetrics;
    private SubscriptionAnalytics subscriptionMetrics;
    private LocalDateTime generatedAt;
    private String period; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
}
