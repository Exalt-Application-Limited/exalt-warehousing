package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUpdateRequest {
    
    private String planId;
    private String customerEmail;
    private String customerName;
    private String companyName;
    private String billingPeriod;
    private BigDecimal customPrice;
    private Boolean autoRenewal;
    private String paymentMethodId;
    private String notes;
    private String status;
}
