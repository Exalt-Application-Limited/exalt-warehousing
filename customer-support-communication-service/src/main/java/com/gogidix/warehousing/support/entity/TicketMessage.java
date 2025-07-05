package com.gogidix.warehousing.support.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Ticket Message Entity
 * 
 * Represents individual messages within a support ticket conversation.
 */
@Entity
@Table(name = "ticket_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_ticket_id", nullable = false)
    private SupportTicket supportTicket;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageSender sender;
    
    private Long senderId;
    
    private String senderName;
    
    private String senderEmail;
    
    @Column(nullable = false)
    private LocalDateTime sentAt;
    
    private Boolean isInternal = false;
    
    private Boolean isRead = false;
    
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;
    
    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
    
    public enum MessageSender {
        CUSTOMER, AGENT, SYSTEM, AI_ASSISTANT
    }
    
    public enum MessageType {
        TEXT, EMAIL, CHAT, PHONE_TRANSCRIPT, SYSTEM_NOTE, AI_SUGGESTION
    }
}