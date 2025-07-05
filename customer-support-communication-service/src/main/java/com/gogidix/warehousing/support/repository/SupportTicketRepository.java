package com.gogidix.warehousing.support.repository;

import com.gogidix.warehousing.support.entity.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Support Ticket Repository
 * 
 * Data access layer for support ticket operations.
 */
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    /**
     * Find ticket by ticket number
     */
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    
    /**
     * Find tickets by customer ID
     */
    Page<SupportTicket> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find tickets by customer email
     */
    Page<SupportTicket> findByCustomerEmail(String customerEmail, Pageable pageable);
    
    /**
     * Find tickets by status
     */
    Page<SupportTicket> findByStatus(SupportTicket.TicketStatus status, Pageable pageable);
    
    /**
     * Find tickets assigned to specific agent
     */
    Page<SupportTicket> findByAssignedToAgentId(Long agentId, Pageable pageable);
    
    /**
     * Find tickets by priority
     */
    Page<SupportTicket> findByPriority(SupportTicket.TicketPriority priority, Pageable pageable);
    
    /**
     * Find tickets by category
     */
    Page<SupportTicket> findByCategory(SupportTicket.TicketCategory category, Pageable pageable);
    
    /**
     * Find tickets created within date range
     */
    @Query("SELECT st FROM SupportTicket st WHERE st.createdAt BETWEEN :startDate AND :endDate")
    Page<SupportTicket> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate, 
                                               Pageable pageable);
    
    /**
     * Find unassigned tickets
     */
    Page<SupportTicket> findByAssignedToAgentIdIsNull(Pageable pageable);
    
    /**
     * Find overdue tickets (open for more than specified hours)
     */
    @Query("SELECT st FROM SupportTicket st WHERE st.status IN ('OPEN', 'IN_PROGRESS') " +
           "AND st.createdAt < :overdueThreshold")
    List<SupportTicket> findOverdueTickets(@Param("overdueThreshold") LocalDateTime overdueThreshold);
    
    /**
     * Get ticket statistics
     */
    @Query("SELECT st.status, COUNT(st) FROM SupportTicket st GROUP BY st.status")
    List<Object[]> getTicketStatsByStatus();
    
    @Query("SELECT st.priority, COUNT(st) FROM SupportTicket st GROUP BY st.priority")
    List<Object[]> getTicketStatsByPriority();
    
    @Query("SELECT st.category, COUNT(st) FROM SupportTicket st GROUP BY st.category")
    List<Object[]> getTicketStatsByCategory();
    
    /**
     * Find tickets requiring escalation
     */
    @Query("SELECT st FROM SupportTicket st WHERE st.priority IN ('HIGH', 'URGENT', 'CRITICAL') " +
           "AND st.status = 'OPEN' AND st.assignedToAgentId IS NULL")
    List<SupportTicket> findTicketsRequiringEscalation();
    
    /**
     * Count active tickets for customer
     */
    long countByCustomerIdAndStatusIn(Long customerId, List<SupportTicket.TicketStatus> statuses);
}