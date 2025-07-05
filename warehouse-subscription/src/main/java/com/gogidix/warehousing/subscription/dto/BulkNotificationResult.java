package com.gogidix.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationResult {
    
    private boolean success;
    private String message;
    private int totalTargeted;
    private int successfulNotifications;
    private int failedNotifications;
    private List<String> errors;
    private LocalDateTime processedAt;
    private String batchId;
}
