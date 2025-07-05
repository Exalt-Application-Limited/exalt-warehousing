package com.gogidix.warehousing.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateRequest {
    private Long facilityId;
    private List<UnitChange> unitChanges;
    private String reason;
}