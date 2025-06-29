package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreateRequest {
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    @NotBlank(message = "Plan ID is required")
    private String planId;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Valid email is required")
    private String customerEmail;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    private String companyName;
    
    @NotNull(message = "Billing period is required")
    private String billingPeriod; // MONTHLY, QUARTERLY, YEARLY
    
    private BigDecimal customPrice;
    
    private String paymentMethodId;
    
    private String promoCode;
    
    private String notes;
    
    private boolean autoRenewal = true;
}
