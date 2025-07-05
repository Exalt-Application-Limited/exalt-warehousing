package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionSearchRequest {
    
    private String customerEmail;
    private String customerName;
    private String companyName;
    private String planId;
    private String status;
    private UUID warehouseId;
    private String billingPeriod;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private LocalDateTime endDateFrom;
    private LocalDateTime endDateTo;
    private Boolean autoRenewal;
    private String paymentStatus;
    private List<String> statuses;
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
