package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandForecast {
    private Long facilityId;
    private List<DemandDataPoint> forecast;
    private BigDecimal averageDemand;
    private List<String> peakPeriods;
    private Map<String, BigDecimal> unitTypeDemand;
}