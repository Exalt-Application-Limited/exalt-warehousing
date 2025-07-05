package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {
    private Long facilityId;
    private Map<String, UnitAvailability> unitTypes;
    private LocalDateTime lastUpdated;
    private Integer totalUnits;
    private Integer availableUnits;
    private BigDecimal occupancyRate;
}