package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitAvailability {
    private String unitType;
    private String unitSize;
    private Integer total;
    private Integer available;
    private Integer reserved;
    private BigDecimal currentPrice;
    private LocalDateTime nextAvailable;
}