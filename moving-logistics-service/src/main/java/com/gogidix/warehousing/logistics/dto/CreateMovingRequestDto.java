package com.gogidix.warehousing.logistics.dto;

import com.gogidix.warehousing.logistics.entity.MovingRequest;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Create Moving Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovingRequestDto {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    @NotNull(message = "Moving type is required")
    private MovingRequest.MovingType movingType;
    
    @NotNull(message = "Service type is required")
    private MovingRequest.ServiceType serviceType;
    
    @NotNull(message = "Preferred date is required")
    @Future(message = "Preferred date must be in the future")
    private LocalDate preferredDate;
    
    private LocalDate alternativeDate;
    
    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;
    
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    
    @DecimalMin(value = "0.0", message = "Estimated weight must be positive")
    private BigDecimal estimatedWeight;
    
    @Min(value = 1, message = "Number of items must be at least 1")
    private Integer numberOfItems;
    
    private String specialInstructions;
    
    private List<MovingItemDto> items;
}