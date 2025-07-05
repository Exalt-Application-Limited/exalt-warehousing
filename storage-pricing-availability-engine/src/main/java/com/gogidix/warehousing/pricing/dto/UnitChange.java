package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitChange {
    private String unitId;
    private String action; // BOOK, RELEASE, MAINTENANCE, AVAILABLE
    private LocalDateTime effectiveDate;
}