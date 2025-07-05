package com.gogidix.warehousing.support.repository;

import com.gogidix.warehousing.support.entity.TicketMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ticket Message Repository
 * 
 * Data access layer for ticket message operations.
 */
@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    
    /**
     * Find messages by support ticket ID
     */
    Page<TicketMessage> findBySupportTicketIdOrderBySentAtAsc(Long ticketId, Pageable pageable);
    
    /**
     * Find messages by support ticket ID (without pagination)
     */
    List<TicketMessage> findBySupportTicketIdOrderBySentAtAsc(Long ticketId);
    
    /**
     * Find recent messages for a ticket
     */
    @Query("SELECT tm FROM TicketMessage tm WHERE tm.supportTicket.id = :ticketId " +
           "ORDER BY tm.sentAt DESC")
    Page<TicketMessage> findRecentMessagesByTicketId(@Param("ticketId") Long ticketId, Pageable pageable);
    
    /**
     * Find unread messages for customer
     */
    @Query("SELECT tm FROM TicketMessage tm WHERE tm.supportTicket.customerId = :customerId " +
           "AND tm.isRead = false AND tm.sender != 'CUSTOMER'")
    List<TicketMessage> findUnreadMessagesForCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find unread messages for agent
     */
    @Query("SELECT tm FROM TicketMessage tm WHERE tm.supportTicket.assignedToAgentId = :agentId " +
           "AND tm.isRead = false AND tm.sender = 'CUSTOMER'")
    List<TicketMessage> findUnreadMessagesForAgent(@Param("agentId") Long agentId);
    
    /**
     * Find messages by sender
     */
    Page<TicketMessage> findBySender(TicketMessage.MessageSender sender, Pageable pageable);
    
    /**
     * Find internal messages for a ticket
     */
    List<TicketMessage> findBySupportTicketIdAndIsInternalTrueOrderBySentAtAsc(Long ticketId);
    
    /**
     * Find customer-facing messages for a ticket
     */
    List<TicketMessage> findBySupportTicketIdAndIsInternalFalseOrderBySentAtAsc(Long ticketId);
    
    /**
     * Count unread messages for ticket
     */
    long countBySupportTicketIdAndIsReadFalse(Long ticketId);
    
    /**
     * Find AI suggestions for a ticket
     */
    List<TicketMessage> findBySupportTicketIdAndSender(Long ticketId, TicketMessage.MessageSender sender);
}