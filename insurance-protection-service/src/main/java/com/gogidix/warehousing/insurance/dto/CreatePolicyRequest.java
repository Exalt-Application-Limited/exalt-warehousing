package com.gogidix.warehousing.insurance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Create Insurance Policy Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePolicyRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotNull(message = "Storage unit ID is required")
    private Long storageUnitId;
    
    @NotBlank(message = "Storage unit number is required")
    private String storageUnitNumber;
    
    @NotNull(message = "Coverage plan ID is required")
    private Long coveragePlanId;
    
    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "100.00", message = "Coverage amount must be at least $100")
    @DecimalMax(value = "1000000.00", message = "Coverage amount cannot exceed $1,000,000")
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Effective date is required")
    @FutureOrPresent(message = "Effective date must be today or in the future")
    private LocalDate effectiveDate;
    
    private LocalDate expirationDate;
}