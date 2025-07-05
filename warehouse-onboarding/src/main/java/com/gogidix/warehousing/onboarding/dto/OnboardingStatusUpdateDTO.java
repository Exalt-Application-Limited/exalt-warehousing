package com.gogidix.warehousing.onboarding.dto;

import com.gogidix.warehousing.onboarding.model.OnboardingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating onboarding request status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStatusUpdateDTO {

    @NotNull(message = "New status is required")
    private OnboardingStatus newStatus;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}