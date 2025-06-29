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
public class ChurnAnalytics {
    
    private BigDecimal churnRate;
    private BigDecimal churnRateMonthly;
    private BigDecimal churnRateAnnual;
    private int totalChurned;
    private int totalActive;
    private BigDecimal revenueChurnRate;
    private BigDecimal customerLifetimeValue;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Map<String, Integer> churnByPlan;
    private Map<String, String> churnReasons;
    private BigDecimal churnPredictionAccuracy;
    private int atRiskCustomers;
}
