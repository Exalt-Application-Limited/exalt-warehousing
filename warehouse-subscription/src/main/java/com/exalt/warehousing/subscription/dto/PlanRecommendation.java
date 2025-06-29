package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanRecommendation {
    
    private String currentPlanId;
    private String recommendedPlanId;
    private String reason;
    private BigDecimal potentialSavings;
    private BigDecimal currentCost;
    private BigDecimal recommendedCost;
    private String confidence; // HIGH, MEDIUM, LOW
}
