package com.gogidix.warehousing.logistics.repository;

import com.gogidix.warehousing.logistics.entity.MovingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Moving Request Repository
 */
@Repository
public interface MovingRequestRepository extends JpaRepository<MovingRequest, Long> {
    
    /**
     * Find request by request number
     */
    Optional<MovingRequest> findByRequestNumber(String requestNumber);
    
    /**
     * Find requests by customer ID
     */
    Page<MovingRequest> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find requests by status
     */
    Page<MovingRequest> findByStatus(MovingRequest.RequestStatus status, Pageable pageable);
    
    /**
     * Find requests by moving type
     */
    Page<MovingRequest> findByMovingType(MovingRequest.MovingType movingType, Pageable pageable);
    
    /**
     * Find requests by service type
     */
    Page<MovingRequest> findByServiceType(MovingRequest.ServiceType serviceType, Pageable pageable);
    
    /**
     * Find requests by preferred date
     */
    List<MovingRequest> findByPreferredDate(LocalDate preferredDate);
    
    /**
     * Find requests by preferred date range
     */
    @Query("SELECT mr FROM MovingRequest mr WHERE mr.preferredDate BETWEEN :startDate AND :endDate")
    List<MovingRequest> findByPreferredDateBetween(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Find requests assigned to driver
     */
    Page<MovingRequest> findByAssignedDriverId(Long driverId, Pageable pageable);
    
    /**
     * Find unassigned requests
     */
    List<MovingRequest> findByAssignedDriverIdIsNull();
    
    /**
     * Find requests needing scheduling
     */
    List<MovingRequest> findByStatusAndScheduledAtIsNull(MovingRequest.RequestStatus status);
    
    /**
     * Find overdue requests
     */
    @Query("SELECT mr FROM MovingRequest mr WHERE mr.preferredDate < :currentDate " +
           "AND mr.status IN ('PENDING', 'QUOTED')")
    List<MovingRequest> findOverdueRequests(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Get request statistics
     */
    @Query("SELECT mr.status, COUNT(mr) FROM MovingRequest mr GROUP BY mr.status")
    List<Object[]> getRequestStatsByStatus();
    
    @Query("SELECT mr.movingType, COUNT(mr) FROM MovingRequest mr GROUP BY mr.movingType")
    List<Object[]> getRequestStatsByMovingType();
    
    /**
     * Count requests by customer
     */
    long countByCustomerId(Long customerId);
    
    /**
     * Find recent requests
     */
    @Query("SELECT mr FROM MovingRequest mr WHERE mr.createdAt >= :since ORDER BY mr.createdAt DESC")
    List<MovingRequest> findRecentRequests(@Param("since") LocalDateTime since);
}