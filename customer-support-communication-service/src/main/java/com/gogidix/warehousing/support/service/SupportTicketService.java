package com.gogidix.warehousing.support.service;

import com.gogidix.warehousing.support.dto.*;
import com.gogidix.warehousing.support.entity.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Support Ticket Service Interface
 * 
 * Business logic for customer support ticket management.
 */
public interface SupportTicketService {
    
    /**
     * Create a new support ticket
     */
    SupportTicketResponse createTicket(CreateTicketRequest request);
    
    /**
     * Get ticket by ID
     */
    SupportTicketResponse getTicketById(Long ticketId);
    
    /**
     * Get ticket by ticket number
     */
    SupportTicketResponse getTicketByNumber(String ticketNumber);
    
    /**
     * Get tickets for customer
     */
    Page<SupportTicketResponse> getCustomerTickets(Long customerId, Pageable pageable);
    
    /**
     * Get tickets assigned to agent
     */
    Page<SupportTicketResponse> getAgentTickets(Long agentId, Pageable pageable);
    
    /**
     * Get tickets by status
     */
    Page<SupportTicketResponse> getTicketsByStatus(SupportTicket.TicketStatus status, Pageable pageable);
    
    /**
     * Update ticket status
     */
    SupportTicketResponse updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status);
    
    /**
     * Update ticket priority
     */
    SupportTicketResponse updateTicketPriority(Long ticketId, SupportTicket.TicketPriority priority);
    
    /**
     * Assign ticket to agent
     */
    SupportTicketResponse assignTicketToAgent(Long ticketId, Long agentId, String agentName);
    
    /**
     * Add message to ticket
     */
    TicketMessageResponse addMessageToTicket(Long ticketId, AddMessageRequest request);
    
    /**
     * Get ticket messages
     */
    Page<TicketMessageResponse> getTicketMessages(Long ticketId, Pageable pageable);
    
    /**
     * Close ticket with resolution
     */
    SupportTicketResponse closeTicket(Long ticketId, String resolutionNotes);
    
    /**
     * Escalate ticket
     */
    SupportTicketResponse escalateTicket(Long ticketId, String escalationReason);
    
    /**
     * Rate ticket resolution
     */
    SupportTicketResponse rateTicket(Long ticketId, Integer rating);
    
    /**
     * Get unassigned tickets
     */
    Page<SupportTicketResponse> getUnassignedTickets(Pageable pageable);
    
    /**
     * Get overdue tickets
     */
    List<SupportTicketResponse> getOverdueTickets(int overdueHours);
    
    /**
     * Get ticket statistics
     */
    Map<String, Object> getTicketStatistics();
    
    /**
     * Search tickets
     */
    Page<SupportTicketResponse> searchTickets(String searchTerm, Pageable pageable);
    
    /**
     * Mark messages as read
     */
    void markMessagesAsRead(Long ticketId, Long userId);
}