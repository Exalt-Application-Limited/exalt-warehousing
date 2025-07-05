package com.gogidix.warehousing.support.dto;

import com.gogidix.warehousing.support.entity.TicketMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Ticket Message Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketMessageResponse {
    
    private Long id;
    private String content;
    private TicketMessage.MessageSender sender;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private LocalDateTime sentAt;
    private Boolean isInternal;
    private Boolean isRead;
    private TicketMessage.MessageType messageType;
}