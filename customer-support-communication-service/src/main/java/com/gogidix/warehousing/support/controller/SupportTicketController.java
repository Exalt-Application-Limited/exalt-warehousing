package com.gogidix.warehousing.support.controller;

import com.gogidix.warehousing.support.dto.*;
import com.gogidix.warehousing.support.entity.SupportTicket;
import com.gogidix.warehousing.support.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Support Ticket Controller
 * 
 * REST API endpoints for customer support ticket management.
 */
@RestController
@RequestMapping("/api/v1/support/tickets")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Support Tickets", description = "Customer support ticket management APIs")
public class SupportTicketController {
    
    private final SupportTicketService supportTicketService;
    
    @Operation(summary = "Create new support ticket", 
               description = "Create a new customer support ticket")
    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request) {
        
        log.info("Creating new support ticket for customer: {}", request.getCustomerId());
        SupportTicketResponse response = supportTicketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Get ticket by ID", 
               description = "Retrieve a support ticket by its ID")
    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getTicketById(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId) {
        
        log.debug("Retrieving ticket by ID: {}", ticketId);
        SupportTicketResponse response = supportTicketService.getTicketById(ticketId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get ticket by number", 
               description = "Retrieve a support ticket by its ticket number")
    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<SupportTicketResponse> getTicketByNumber(
            @Parameter(description = "Ticket number") @PathVariable String ticketNumber) {
        
        log.debug("Retrieving ticket by number: {}", ticketNumber);
        SupportTicketResponse response = supportTicketService.getTicketByNumber(ticketNumber);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get customer tickets", 
               description = "Get all tickets for a specific customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<SupportTicketResponse>> getCustomerTickets(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            Pageable pageable) {
        
        log.debug("Retrieving tickets for customer: {}", customerId);
        Page<SupportTicketResponse> response = supportTicketService.getCustomerTickets(customerId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get agent tickets", 
               description = "Get all tickets assigned to a specific agent")
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<Page<SupportTicketResponse>> getAgentTickets(
            @Parameter(description = "Agent ID") @PathVariable Long agentId,
            Pageable pageable) {
        
        log.debug("Retrieving tickets for agent: {}", agentId);
        Page<SupportTicketResponse> response = supportTicketService.getAgentTickets(agentId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get tickets by status", 
               description = "Get all tickets with a specific status")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<SupportTicketResponse>> getTicketsByStatus(
            @Parameter(description = "Ticket status") @PathVariable SupportTicket.TicketStatus status,
            Pageable pageable) {
        
        log.debug("Retrieving tickets with status: {}", status);
        Page<SupportTicketResponse> response = supportTicketService.getTicketsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update ticket status", 
               description = "Update the status of a support ticket")
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<SupportTicketResponse> updateTicketStatus(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "New status") @RequestParam SupportTicket.TicketStatus status) {
        
        log.info("Updating ticket {} status to: {}", ticketId, status);
        SupportTicketResponse response = supportTicketService.updateTicketStatus(ticketId, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update ticket priority", 
               description = "Update the priority of a support ticket")
    @PutMapping("/{ticketId}/priority")
    public ResponseEntity<SupportTicketResponse> updateTicketPriority(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "New priority") @RequestParam SupportTicket.TicketPriority priority) {
        
        log.info("Updating ticket {} priority to: {}", ticketId, priority);
        SupportTicketResponse response = supportTicketService.updateTicketPriority(ticketId, priority);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Assign ticket to agent", 
               description = "Assign a support ticket to a specific agent")
    @PutMapping("/{ticketId}/assign")
    public ResponseEntity<SupportTicketResponse> assignTicketToAgent(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "Agent ID") @RequestParam Long agentId,
            @Parameter(description = "Agent name") @RequestParam String agentName) {
        
        log.info("Assigning ticket {} to agent: {} ({})", ticketId, agentName, agentId);
        SupportTicketResponse response = supportTicketService.assignTicketToAgent(ticketId, agentId, agentName);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Add message to ticket", 
               description = "Add a new message to a support ticket")
    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<TicketMessageResponse> addMessageToTicket(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Valid @RequestBody AddMessageRequest request) {
        
        log.info("Adding message to ticket: {}", ticketId);
        TicketMessageResponse response = supportTicketService.addMessageToTicket(ticketId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Get ticket messages", 
               description = "Get all messages for a support ticket")
    @GetMapping("/{ticketId}/messages")
    public ResponseEntity<Page<TicketMessageResponse>> getTicketMessages(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            Pageable pageable) {
        
        log.debug("Retrieving messages for ticket: {}", ticketId);
        Page<TicketMessageResponse> response = supportTicketService.getTicketMessages(ticketId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Close ticket", 
               description = "Close a support ticket with resolution notes")
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<SupportTicketResponse> closeTicket(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "Resolution notes") @RequestParam String resolutionNotes) {
        
        log.info("Closing ticket: {} with resolution", ticketId);
        SupportTicketResponse response = supportTicketService.closeTicket(ticketId, resolutionNotes);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Escalate ticket", 
               description = "Escalate a support ticket to higher priority")
    @PutMapping("/{ticketId}/escalate")
    public ResponseEntity<SupportTicketResponse> escalateTicket(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "Escalation reason") @RequestParam String escalationReason) {
        
        log.info("Escalating ticket: {}", ticketId);
        SupportTicketResponse response = supportTicketService.escalateTicket(ticketId, escalationReason);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Rate ticket", 
               description = "Rate the resolution of a support ticket")
    @PutMapping("/{ticketId}/rate")
    public ResponseEntity<SupportTicketResponse> rateTicket(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "Rating (1-5)") @RequestParam Integer rating) {
        
        log.info("Rating ticket: {} with rating: {}", ticketId, rating);
        SupportTicketResponse response = supportTicketService.rateTicket(ticketId, rating);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get unassigned tickets", 
               description = "Get all tickets that are not assigned to any agent")
    @GetMapping("/unassigned")
    public ResponseEntity<Page<SupportTicketResponse>> getUnassignedTickets(Pageable pageable) {
        
        log.debug("Retrieving unassigned tickets");
        Page<SupportTicketResponse> response = supportTicketService.getUnassignedTickets(pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get overdue tickets", 
               description = "Get all tickets that are overdue (open longer than specified hours)")
    @GetMapping("/overdue")
    public ResponseEntity<List<SupportTicketResponse>> getOverdueTickets(
            @Parameter(description = "Overdue threshold in hours") @RequestParam(defaultValue = "24") int overdueHours) {
        
        log.debug("Retrieving overdue tickets (older than {} hours)", overdueHours);
        List<SupportTicketResponse> response = supportTicketService.getOverdueTickets(overdueHours);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get ticket statistics", 
               description = "Get comprehensive statistics about support tickets")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTicketStatistics() {
        
        log.debug("Generating ticket statistics");
        Map<String, Object> response = supportTicketService.getTicketStatistics();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Search tickets", 
               description = "Search tickets by various criteria")
    @GetMapping("/search")
    public ResponseEntity<Page<SupportTicketResponse>> searchTickets(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            Pageable pageable) {
        
        log.debug("Searching tickets with term: {}", searchTerm);
        Page<SupportTicketResponse> response = supportTicketService.searchTickets(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Mark messages as read", 
               description = "Mark all messages in a ticket as read for a specific user")
    @PutMapping("/{ticketId}/messages/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @Parameter(description = "Ticket ID") @PathVariable Long ticketId,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        log.debug("Marking messages as read for ticket: {} by user: {}", ticketId, userId);
        supportTicketService.markMessagesAsRead(ticketId, userId);
        return ResponseEntity.ok().build();
    }
}