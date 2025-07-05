package com.gogidix.warehousing.logistics.service;

import com.gogidix.warehousing.logistics.dto.*;
import com.gogidix.warehousing.logistics.entity.MovingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Moving Logistics Service Interface
 */
public interface MovingLogisticsService {
    
    /**
     * Create a new moving request
     */
    MovingRequestResponseDto createMovingRequest(CreateMovingRequestDto request);
    
    /**
     * Get moving request by ID
     */
    MovingRequestResponseDto getMovingRequestById(Long requestId);
    
    /**
     * Get moving request by request number
     */
    MovingRequestResponseDto getMovingRequestByNumber(String requestNumber);
    
    /**
     * Get customer moving requests
     */
    Page<MovingRequestResponseDto> getCustomerMovingRequests(Long customerId, Pageable pageable);
    
    /**
     * Get moving requests by status
     */
    Page<MovingRequestResponseDto> getMovingRequestsByStatus(MovingRequest.RequestStatus status, Pageable pageable);
    
    /**
     * Update request status
     */
    MovingRequestResponseDto updateRequestStatus(Long requestId, MovingRequest.RequestStatus status);
    
    /**
     * Generate quote for moving request
     */
    MovingRequestResponseDto generateQuote(Long requestId, BigDecimal quotedAmount);
    
    /**
     * Assign driver to moving request
     */
    MovingRequestResponseDto assignDriver(Long requestId, Long driverId, String driverName);
    
    /**
     * Schedule moving request
     */
    MovingRequestResponseDto scheduleMoving(Long requestId, ScheduleMovingRequestDto scheduleDto);
    
    /**
     * Get unassigned requests
     */
    List<MovingRequestResponseDto> getUnassignedRequests();
    
    /**
     * Get requests for date
     */
    List<MovingRequestResponseDto> getRequestsForDate(LocalDate date);
    
    /**
     * Get overdue requests
     */
    List<MovingRequestResponseDto> getOverdueRequests();
    
    /**
     * Calculate estimated cost
     */
    BigDecimal calculateEstimatedCost(CreateMovingRequestDto request);
    
    /**
     * Get moving statistics
     */
    Map<String, Object> getMovingStatistics();
    
    /**
     * Cancel moving request
     */
    MovingRequestResponseDto cancelMovingRequest(Long requestId, String reason);
    
    /**
     * Complete moving request
     */
    MovingRequestResponseDto completeMovingRequest(Long requestId, BigDecimal finalCost);
}