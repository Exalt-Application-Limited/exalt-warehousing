package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanUpgradeRequest {
    
    @NotBlank(message = "New plan ID is required")
    private String newPlanId;
    
    private boolean prorateBilling = true;
    private LocalDateTime effectiveDate;
    private String reason;
    private String notes;
}
