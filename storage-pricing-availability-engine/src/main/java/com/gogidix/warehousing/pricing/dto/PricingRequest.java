package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingRequest {
    private Long facilityId;
    private String unitType;
    private String unitSize;
    private Integer duration; // rental duration in days
    private Integer demandScore;
    private BigDecimal occupancyRate;
    private String customerSegment;
    private String seasonalPeriod;
    private Map<String, Object> additionalFactors;
    private PricingContext context; // pricing context
}