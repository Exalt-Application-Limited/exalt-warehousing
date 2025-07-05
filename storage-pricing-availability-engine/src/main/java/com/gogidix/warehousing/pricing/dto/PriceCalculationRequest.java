package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import java.util.Map;

/**
 * Request DTO for price calculation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationRequest {

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    @NotNull(message = "Unit type is required")
    private String unitType;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer duration;

    private Map<String, Object> context;

    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    private String promoCode;
}