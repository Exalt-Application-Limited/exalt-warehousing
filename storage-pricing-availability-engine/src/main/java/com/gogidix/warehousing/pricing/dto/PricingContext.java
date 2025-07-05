package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * Pricing Context DTO
 * 
 * Standalone pricing context for calculation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingContext {
    private BigDecimal occupancyRate;
    private Integer demandScore;
    private String facilityType;
    private String unitSizeCategory;
    private String geographicRegion;
    private String seasonalPeriod;
    private String dayOfWeek;
    private Integer hourOfDay;
    private BigDecimal competitorPrice;
    private String customerSegment;
    private Integer availableUnits;
    private BigDecimal historicalDemand;
}