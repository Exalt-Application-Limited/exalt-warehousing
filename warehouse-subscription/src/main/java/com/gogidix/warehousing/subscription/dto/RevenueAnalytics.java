package com.gogidix.warehousing.subscription.dto;

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
public class RevenueAnalytics {
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRecurringRevenue;
    private BigDecimal annualRecurringRevenue;
    private BigDecimal averageRevenuePerUser;
    private BigDecimal revenueGrowthRate;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Map<String, BigDecimal> revenueByPlan;
    private Map<String, BigDecimal> revenueByMonth;
    private int totalSubscriptions;
    private int newSubscriptions;
    private int upgrades;
    private int downgrades;
}
