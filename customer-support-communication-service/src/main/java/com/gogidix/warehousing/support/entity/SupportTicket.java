package com.gogidix.warehousing.support.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Support Ticket Entity
 * 
 * Represents a customer support ticket with full lifecycle management.
 */
@Entity
@Table(name = "support_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String ticketNumber;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private String customerEmail;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime resolvedAt;
    
    private Long assignedToAgentId;
    
    private String assignedToAgentName;
    
    private Integer customerSatisfactionRating;
    
    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @OneToMany(mappedBy = "supportTicket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketMessage> messages;
    
    @OneToMany(mappedBy = "supportTicket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketAttachment> attachments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (ticketNumber == null) {
            ticketNumber = generateTicketNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateTicketNumber() {
        return "CST-" + System.currentTimeMillis();
    }
    
    public enum TicketStatus {
        OPEN, IN_PROGRESS, PENDING_CUSTOMER, RESOLVED, CLOSED, ESCALATED
    }
    
    public enum TicketPriority {
        LOW, MEDIUM, HIGH, URGENT, CRITICAL
    }
    
    public enum TicketCategory {
        GENERAL_INQUIRY, BILLING, TECHNICAL_SUPPORT, ACCOUNT_MANAGEMENT,
        STORAGE_ISSUE, ACCESS_PROBLEM, INSURANCE_CLAIM, COMPLAINT, FEATURE_REQUEST
    }
}