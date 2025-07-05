package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageSummary {
    
    private UUID subscriptionId;
    private BigDecimal storageUsedGB;
    private BigDecimal storageAllowedGB;
    private int ordersProcessed;
    private int ordersAllowed;
    private int activeUsers;
    private int usersAllowed;
    private int apiCalls;
    private int apiCallsAllowed;
    private BigDecimal utilizationPercentage;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
