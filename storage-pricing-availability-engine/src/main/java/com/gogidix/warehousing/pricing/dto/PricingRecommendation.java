package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRecommendation {
    private String unitType;
    private BigDecimal currentPrice;
    private BigDecimal recommendedPrice;
    private BigDecimal potentialRevenue;
    private String reason;
    private Integer confidence;
}