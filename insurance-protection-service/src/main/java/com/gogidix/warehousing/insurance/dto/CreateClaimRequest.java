package com.gogidix.warehousing.insurance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Create Insurance Claim Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClaimRequest {
    
    @NotNull(message = "Policy ID is required")
    private Long policyId;
    
    @NotBlank(message = "Incident type is required")
    private String incidentType;
    
    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    private LocalDate incidentDate;
    
    @NotBlank(message = "Incident description is required")
    @Size(min = 10, max = 5000, message = "Incident description must be between 10 and 5000 characters")
    private String incidentDescription;
    
    @NotNull(message = "Claimed amount is required")
    @DecimalMin(value = "1.00", message = "Claimed amount must be at least $1")
    @DecimalMax(value = "1000000.00", message = "Claimed amount cannot exceed $1,000,000")
    private BigDecimal claimedAmount;
}