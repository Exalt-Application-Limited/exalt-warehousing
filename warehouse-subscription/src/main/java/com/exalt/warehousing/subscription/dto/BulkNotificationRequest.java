package com.exalt.warehousing.subscription.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationRequest {
    
    private List<UUID> subscriptionIds;
    private String notificationType; // RENEWAL_REMINDER, PAYMENT_FAILED, TRIAL_ENDING, etc.
    private String subject;
    private String message;
    private boolean sendEmail = true;
    private boolean sendSms = false;
    private LocalDateTime scheduledAt;
}
