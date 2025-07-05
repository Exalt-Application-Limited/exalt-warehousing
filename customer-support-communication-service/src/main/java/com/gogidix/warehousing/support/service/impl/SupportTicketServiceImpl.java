package com.gogidix.warehousing.support.service.impl;

import com.gogidix.warehousing.support.dto.*;
import com.gogidix.warehousing.support.entity.SupportTicket;
import com.gogidix.warehousing.support.entity.TicketMessage;
import com.gogidix.warehousing.support.repository.SupportTicketRepository;
import com.gogidix.warehousing.support.repository.TicketMessageRepository;
import com.gogidix.warehousing.support.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Support Ticket Service Implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {
    
    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    
    @Override
    public SupportTicketResponse createTicket(CreateTicketRequest request) {
        log.info("Creating support ticket for customer: {}", request.getCustomerId());
        
        SupportTicket ticket = SupportTicket.builder()
                .customerId(request.getCustomerId())
                .customerEmail(request.getCustomerEmail())
                .customerName(request.getCustomerName())
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(SupportTicket.TicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
        
        ticket = ticketRepository.save(ticket);
        
        // Add initial message with description
        TicketMessage initialMessage = TicketMessage.builder()
                .supportTicket(ticket)
                .content(request.getDescription())
                .sender(TicketMessage.MessageSender.CUSTOMER)
                .senderId(request.getCustomerId())
                .senderName(request.getCustomerName())
                .senderEmail(request.getCustomerEmail())
                .sentAt(LocalDateTime.now())
                .isInternal(false)
                .isRead(false)
                .messageType(TicketMessage.MessageType.TEXT)
                .build();
        
        messageRepository.save(initialMessage);
        
        log.info("Created support ticket: {} for customer: {}", ticket.getTicketNumber(), request.getCustomerId());
        return mapToResponse(ticket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketById(Long ticketId) {
        log.debug("Retrieving ticket by ID: {}", ticketId);
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        return mapToResponse(ticket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketByNumber(String ticketNumber) {
        log.debug("Retrieving ticket by number: {}", ticketNumber);
        SupportTicket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket not found with number: " + ticketNumber));
        return mapToResponse(ticket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getCustomerTickets(Long customerId, Pageable pageable) {
        log.debug("Retrieving tickets for customer: {}", customerId);
        return ticketRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getAgentTickets(Long agentId, Pageable pageable) {
        log.debug("Retrieving tickets for agent: {}", agentId);
        return ticketRepository.findByAssignedToAgentId(agentId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getTicketsByStatus(SupportTicket.TicketStatus status, Pageable pageable) {
        log.debug("Retrieving tickets with status: {}", status);
        return ticketRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    public SupportTicketResponse updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status) {
        log.info("Updating ticket {} status to: {}", ticketId, status);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        SupportTicket.TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (status == SupportTicket.TicketStatus.RESOLVED || status == SupportTicket.TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        
        ticket = ticketRepository.save(ticket);
        
        // Add system message about status change
        addSystemMessage(ticket, String.format("Ticket status changed from %s to %s", oldStatus, status));
        
        log.info("Updated ticket {} status from {} to {}", ticketId, oldStatus, status);
        return mapToResponse(ticket);
    }
    
    @Override
    public SupportTicketResponse updateTicketPriority(Long ticketId, SupportTicket.TicketPriority priority) {
        log.info("Updating ticket {} priority to: {}", ticketId, priority);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        SupportTicket.TicketPriority oldPriority = ticket.getPriority();
        ticket.setPriority(priority);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        
        // Add system message about priority change
        addSystemMessage(ticket, String.format("Ticket priority changed from %s to %s", oldPriority, priority));
        
        log.info("Updated ticket {} priority from {} to {}", ticketId, oldPriority, priority);
        return mapToResponse(ticket);
    }
    
    @Override
    public SupportTicketResponse assignTicketToAgent(Long ticketId, Long agentId, String agentName) {
        log.info("Assigning ticket {} to agent: {} ({})", ticketId, agentName, agentId);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        ticket.setAssignedToAgentId(agentId);
        ticket.setAssignedToAgentName(agentName);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (ticket.getStatus() == SupportTicket.TicketStatus.OPEN) {
            ticket.setStatus(SupportTicket.TicketStatus.IN_PROGRESS);
        }
        
        ticket = ticketRepository.save(ticket);
        
        // Add system message about assignment
        addSystemMessage(ticket, String.format("Ticket assigned to agent: %s", agentName));
        
        log.info("Assigned ticket {} to agent: {} ({})", ticketId, agentName, agentId);
        return mapToResponse(ticket);
    }
    
    @Override
    public TicketMessageResponse addMessageToTicket(Long ticketId, AddMessageRequest request) {
        log.info("Adding message to ticket: {}", ticketId);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        TicketMessage message = TicketMessage.builder()
                .supportTicket(ticket)
                .content(request.getContent())
                .sender(request.getSender())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderEmail(request.getSenderEmail())
                .sentAt(LocalDateTime.now())
                .isInternal(request.getIsInternal())
                .isRead(false)
                .messageType(request.getMessageType())
                .build();
        
        message = messageRepository.save(message);
        
        // Update ticket's updated timestamp
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        log.info("Added message to ticket: {}", ticketId);
        return mapToMessageResponse(message);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TicketMessageResponse> getTicketMessages(Long ticketId, Pageable pageable) {
        log.debug("Retrieving messages for ticket: {}", ticketId);
        return messageRepository.findBySupportTicketIdOrderBySentAtAsc(ticketId, pageable)
                .map(this::mapToMessageResponse);
    }
    
    @Override
    public SupportTicketResponse closeTicket(Long ticketId, String resolutionNotes) {
        log.info("Closing ticket: {} with resolution", ticketId);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        ticket.setStatus(SupportTicket.TicketStatus.RESOLVED);
        ticket.setResolutionNotes(resolutionNotes);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        
        // Add system message about resolution
        addSystemMessage(ticket, "Ticket has been resolved: " + resolutionNotes);
        
        log.info("Closed ticket: {} with resolution", ticketId);
        return mapToResponse(ticket);
    }
    
    @Override
    public SupportTicketResponse escalateTicket(Long ticketId, String escalationReason) {
        log.info("Escalating ticket: {}", ticketId);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        ticket.setStatus(SupportTicket.TicketStatus.ESCALATED);
        ticket.setPriority(SupportTicket.TicketPriority.HIGH);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        
        // Add system message about escalation
        addSystemMessage(ticket, "Ticket escalated: " + escalationReason);
        
        log.info("Escalated ticket: {}", ticketId);
        return mapToResponse(ticket);
    }
    
    @Override
    public SupportTicketResponse rateTicket(Long ticketId, Integer rating) {
        log.info("Rating ticket: {} with rating: {}", ticketId, rating);
        
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        ticket.setCustomerSatisfactionRating(rating);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        
        // Add system message about rating
        addSystemMessage(ticket, String.format("Customer rated this ticket: %d/5 stars", rating));
        
        log.info("Rated ticket: {} with rating: {}", ticketId, rating);
        return mapToResponse(ticket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getUnassignedTickets(Pageable pageable) {
        log.debug("Retrieving unassigned tickets");
        return ticketRepository.findByAssignedToAgentIdIsNull(pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getOverdueTickets(int overdueHours) {
        log.debug("Retrieving overdue tickets (older than {} hours)", overdueHours);
        LocalDateTime overdueThreshold = LocalDateTime.now().minusHours(overdueHours);
        return ticketRepository.findOverdueTickets(overdueThreshold)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTicketStatistics() {
        log.debug("Generating ticket statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Status statistics
        List<Object[]> statusStats = ticketRepository.getTicketStatsByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("statusCounts", statusCounts);
        
        // Priority statistics
        List<Object[]> priorityStats = ticketRepository.getTicketStatsByPriority();
        Map<String, Long> priorityCounts = new HashMap<>();
        for (Object[] stat : priorityStats) {
            priorityCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("priorityCounts", priorityCounts);
        
        // Category statistics
        List<Object[]> categoryStats = ticketRepository.getTicketStatsByCategory();
        Map<String, Long> categoryCounts = new HashMap<>();
        for (Object[] stat : categoryStats) {
            categoryCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("categoryCounts", categoryCounts);
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> searchTickets(String searchTerm, Pageable pageable) {
        log.debug("Searching tickets with term: {}", searchTerm);
        // For now, return all tickets - in a real implementation, you'd add full-text search
        return ticketRepository.findAll(pageable).map(this::mapToResponse);
    }
    
    @Override
    public void markMessagesAsRead(Long ticketId, Long userId) {
        log.debug("Marking messages as read for ticket: {} by user: {}", ticketId, userId);
        List<TicketMessage> messages = messageRepository.findBySupportTicketIdOrderBySentAtAsc(ticketId);
        for (TicketMessage message : messages) {
            if (!message.getIsRead()) {
                message.setIsRead(true);
            }
        }
        messageRepository.saveAll(messages);
    }
    
    private void addSystemMessage(SupportTicket ticket, String content) {
        TicketMessage systemMessage = TicketMessage.builder()
                .supportTicket(ticket)
                .content(content)
                .sender(TicketMessage.MessageSender.SYSTEM)
                .sentAt(LocalDateTime.now())
                .isInternal(false)
                .isRead(false)
                .messageType(TicketMessage.MessageType.SYSTEM_NOTE)
                .build();
        
        messageRepository.save(systemMessage);
    }
    
    private SupportTicketResponse mapToResponse(SupportTicket ticket) {
        return SupportTicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .customerId(ticket.getCustomerId())
                .customerEmail(ticket.getCustomerEmail())
                .customerName(ticket.getCustomerName())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .assignedToAgentId(ticket.getAssignedToAgentId())
                .assignedToAgentName(ticket.getAssignedToAgentName())
                .customerSatisfactionRating(ticket.getCustomerSatisfactionRating())
                .resolutionNotes(ticket.getResolutionNotes())
                .build();
    }
    
    private TicketMessageResponse mapToMessageResponse(TicketMessage message) {
        return TicketMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .senderEmail(message.getSenderEmail())
                .sentAt(message.getSentAt())
                .isInternal(message.getIsInternal())
                .isRead(message.getIsRead())
                .messageType(message.getMessageType())
                .build();
    }
}