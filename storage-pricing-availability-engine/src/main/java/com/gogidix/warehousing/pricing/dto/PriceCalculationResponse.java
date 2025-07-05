package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for price calculation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceCalculationResponse {

    private Long facilityId;
    private String unitType;
    private Integer duration;
    private BigDecimal calculatedPrice;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private LocalDateTime calculatedAt;
    private Map<String, BigDecimal> priceBreakdown;
    private String currency;
    private Boolean isPromotionalPrice;
    private String validUntil;
}