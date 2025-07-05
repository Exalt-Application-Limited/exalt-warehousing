package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingResponse {
    private BigDecimal recommendedPrice;
    private BigDecimal basePrice;
    private BigDecimal adjustment;
    private List<String> appliedRules;
    private BigDecimal confidence;
    private String reasoning;
}