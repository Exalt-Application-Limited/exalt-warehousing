package com.gogidix.warehousing.support.dto;

import com.gogidix.warehousing.support.entity.SupportTicket;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Support Ticket Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketResponse {
    
    private Long id;
    private String ticketNumber;
    private Long customerId;
    private String customerEmail;
    private String customerName;
    private String subject;
    private String description;
    private SupportTicket.TicketStatus status;
    private SupportTicket.TicketPriority priority;
    private SupportTicket.TicketCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private Long assignedToAgentId;
    private String assignedToAgentName;
    private Integer customerSatisfactionRating;
    private String resolutionNotes;
    private List<TicketMessageResponse> recentMessages;
    private Integer unreadMessageCount;
    private List<TicketAttachmentResponse> attachments;
}