package com.exalt.warehousing.subscription.dto;

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
public class UsageRecordRequest {
    
    private UUID subscriptionId;
    private String metricType; // STORAGE_GB, ORDERS_PROCESSED, USERS_ACTIVE, API_CALLS
    private BigDecimal quantity;
    private String unit;
    private LocalDateTime recordedAt;
    private String description;
}
