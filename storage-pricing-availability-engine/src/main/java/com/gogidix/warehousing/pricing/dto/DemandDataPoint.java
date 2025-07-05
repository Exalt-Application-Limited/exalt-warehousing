package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandDataPoint {
    private LocalDate date;
    private Integer demandScore;
    private BigDecimal expectedOccupancy;
    private BigDecimal recommendedPrice;
}