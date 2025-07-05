package com.gogidix.warehousing.support.dto;

import com.gogidix.warehousing.support.entity.TicketMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Add Message to Ticket Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageRequest {
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    @NotNull(message = "Sender type is required")
    private TicketMessage.MessageSender sender;
    
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Boolean isInternal = false;
    private TicketMessage.MessageType messageType = TicketMessage.MessageType.TEXT;
}