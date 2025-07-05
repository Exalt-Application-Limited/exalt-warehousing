package com.gogidix.warehousing.support.service;

import com.gogidix.warehousing.support.dto.CreateTicketRequest;
import com.gogidix.warehousing.support.dto.SupportTicketResponse;
import com.gogidix.warehousing.support.entity.SupportTicket;
import com.gogidix.warehousing.support.repository.SupportTicketRepository;
import com.gogidix.warehousing.support.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SupportTicketService
 */
@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    private CreateTicketRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateTicketRequest(
            1L,
            "customer@example.com", 
            "John Doe",
            "Test Issue",
            "This is a test support ticket",
            SupportTicket.TicketCategory.GENERAL_INQUIRY,
            SupportTicket.TicketPriority.MEDIUM
        );
    }

    @Test
    void createTicket_ValidRequest_ReturnsTicketResponse() {
        // Given
        SupportTicket savedTicket = SupportTicket.builder()
            .id(1L)
            .ticketNumber("CST-123456")
            .customerId(validRequest.getCustomerId())
            .customerEmail(validRequest.getCustomerEmail())
            .customerName(validRequest.getCustomerName())
            .subject(validRequest.getSubject())
            .description(validRequest.getDescription())
            .category(validRequest.getCategory())
            .priority(validRequest.getPriority())
            .status(SupportTicket.TicketStatus.OPEN)
            .build();

        when(ticketRepository.save(any(SupportTicket.class))).thenReturn(savedTicket);

        // When
        SupportTicketResponse response = supportTicketService.createTicket(validRequest);

        // Then
        assertNotNull(response);
        assertEquals(savedTicket.getId(), response.getId());
        assertEquals(savedTicket.getTicketNumber(), response.getTicketNumber());
        assertEquals(savedTicket.getCustomerId(), response.getCustomerId());
        assertEquals(SupportTicket.TicketStatus.OPEN, response.getStatus());
        
        verify(ticketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void getTicketById_ExistingTicket_ReturnsTicketResponse() {
        // Given
        Long ticketId = 1L;
        SupportTicket ticket = SupportTicket.builder()
            .id(ticketId)
            .ticketNumber("CST-123456")
            .customerId(1L)
            .customerEmail("customer@example.com")
            .customerName("John Doe")
            .subject("Test Issue")
            .description("This is a test support ticket")
            .category(SupportTicket.TicketCategory.GENERAL_INQUIRY)
            .priority(SupportTicket.TicketPriority.MEDIUM)
            .status(SupportTicket.TicketStatus.OPEN)
            .build();

        when(ticketRepository.findById(ticketId)).thenReturn(java.util.Optional.of(ticket));

        // When
        SupportTicketResponse response = supportTicketService.getTicketById(ticketId);

        // Then
        assertNotNull(response);
        assertEquals(ticket.getId(), response.getId());
        assertEquals(ticket.getTicketNumber(), response.getTicketNumber());
        
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void getTicketById_NonExistentTicket_ThrowsException() {
        // Given
        Long ticketId = 999L;
        when(ticketRepository.findById(ticketId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> supportTicketService.getTicketById(ticketId));
        
        verify(ticketRepository, times(1)).findById(ticketId);
    }
}