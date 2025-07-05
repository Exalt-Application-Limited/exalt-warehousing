package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTO {
    
    private String id;
    private String name;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private String billingPeriod;
    private List<String> features;
    private int maxWarehouses;
    private int maxUsers;
    private int maxStorage; // in GB
    private int maxOrders;
    private boolean isActive;
    private boolean isPopular;
    private String tier; // BASIC, STANDARD, PREMIUM, ENTERPRISE
}
