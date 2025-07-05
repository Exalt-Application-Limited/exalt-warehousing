package com.gogidix.warehousing.logistics.controller;

import com.gogidix.warehousing.logistics.dto.*;
import com.gogidix.warehousing.logistics.entity.MovingRequest;
import com.gogidix.warehousing.logistics.service.MovingLogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Moving Logistics Controller
 * 
 * REST API endpoints for moving and delivery coordination.
 */
@RestController
@RequestMapping("/api/v1/moving")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Moving Logistics", description = "Moving and delivery coordination APIs")
public class MovingLogisticsController {
    
    private final MovingLogisticsService movingLogisticsService;
    
    @Operation(summary = "Create moving request", 
               description = "Create a new moving and delivery request")
    @PostMapping("/requests")
    public ResponseEntity<MovingRequestResponseDto> createMovingRequest(
            @Valid @RequestBody CreateMovingRequestDto request) {
        
        log.info("Creating moving request for customer: {}", request.getCustomerId());
        MovingRequestResponseDto response = movingLogisticsService.createMovingRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Get moving request by ID", 
               description = "Retrieve a moving request by its ID")
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<MovingRequestResponseDto> getMovingRequestById(
            @Parameter(description = "Request ID") @PathVariable Long requestId) {
        
        log.debug("Retrieving moving request by ID: {}", requestId);
        MovingRequestResponseDto response = movingLogisticsService.getMovingRequestById(requestId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get moving request by number", 
               description = "Retrieve a moving request by its request number")
    @GetMapping("/requests/number/{requestNumber}")
    public ResponseEntity<MovingRequestResponseDto> getMovingRequestByNumber(
            @Parameter(description = "Request number") @PathVariable String requestNumber) {
        
        log.debug("Retrieving moving request by number: {}", requestNumber);
        MovingRequestResponseDto response = movingLogisticsService.getMovingRequestByNumber(requestNumber);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get customer moving requests", 
               description = "Get all moving requests for a specific customer")
    @GetMapping("/requests/customer/{customerId}")
    public ResponseEntity<Page<MovingRequestResponseDto>> getCustomerMovingRequests(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            Pageable pageable) {
        
        log.debug("Retrieving moving requests for customer: {}", customerId);
        Page<MovingRequestResponseDto> response = movingLogisticsService.getCustomerMovingRequests(customerId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get moving requests by status", 
               description = "Get all moving requests with a specific status")
    @GetMapping("/requests/status/{status}")
    public ResponseEntity<Page<MovingRequestResponseDto>> getMovingRequestsByStatus(
            @Parameter(description = "Request status") @PathVariable MovingRequest.RequestStatus status,
            Pageable pageable) {
        
        log.debug("Retrieving moving requests with status: {}", status);
        Page<MovingRequestResponseDto> response = movingLogisticsService.getMovingRequestsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update request status", 
               description = "Update the status of a moving request")
    @PutMapping("/requests/{requestId}/status")
    public ResponseEntity<MovingRequestResponseDto> updateRequestStatus(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Parameter(description = "New status") @RequestParam MovingRequest.RequestStatus status) {
        
        log.info("Updating moving request {} status to: {}", requestId, status);
        MovingRequestResponseDto response = movingLogisticsService.updateRequestStatus(requestId, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Generate quote", 
               description = "Generate a price quote for a moving request")
    @PutMapping("/requests/{requestId}/quote")
    public ResponseEntity<MovingRequestResponseDto> generateQuote(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Parameter(description = "Quoted amount") @RequestParam BigDecimal quotedAmount) {
        
        log.info("Generating quote for moving request: {} amount: {}", requestId, quotedAmount);
        MovingRequestResponseDto response = movingLogisticsService.generateQuote(requestId, quotedAmount);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Assign driver", 
               description = "Assign a driver to a moving request")
    @PutMapping("/requests/{requestId}/assign-driver")
    public ResponseEntity<MovingRequestResponseDto> assignDriver(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Parameter(description = "Driver ID") @RequestParam Long driverId,
            @Parameter(description = "Driver name") @RequestParam String driverName) {
        
        log.info("Assigning driver {} ({}) to moving request: {}", driverName, driverId, requestId);
        MovingRequestResponseDto response = movingLogisticsService.assignDriver(requestId, driverId, driverName);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Schedule moving", 
               description = "Schedule a moving request with specific times and resources")
    @PutMapping("/requests/{requestId}/schedule")
    public ResponseEntity<MovingRequestResponseDto> scheduleMoving(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Valid @RequestBody ScheduleMovingRequestDto scheduleDto) {
        
        log.info("Scheduling moving request: {}", requestId);
        MovingRequestResponseDto response = movingLogisticsService.scheduleMoving(requestId, scheduleDto);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get unassigned requests", 
               description = "Get all moving requests that are not assigned to any driver")
    @GetMapping("/requests/unassigned")
    public ResponseEntity<List<MovingRequestResponseDto>> getUnassignedRequests() {
        
        log.debug("Retrieving unassigned moving requests");
        List<MovingRequestResponseDto> response = movingLogisticsService.getUnassignedRequests();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get requests for date", 
               description = "Get all moving requests scheduled for a specific date")
    @GetMapping("/requests/date/{date}")
    public ResponseEntity<List<MovingRequestResponseDto>> getRequestsForDate(
            @Parameter(description = "Date (YYYY-MM-DD)") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.debug("Retrieving moving requests for date: {}", date);
        List<MovingRequestResponseDto> response = movingLogisticsService.getRequestsForDate(date);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get overdue requests", 
               description = "Get all moving requests that are overdue")
    @GetMapping("/requests/overdue")
    public ResponseEntity<List<MovingRequestResponseDto>> getOverdueRequests() {
        
        log.debug("Retrieving overdue moving requests");
        List<MovingRequestResponseDto> response = movingLogisticsService.getOverdueRequests();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Calculate estimated cost", 
               description = "Calculate the estimated cost for a moving request")
    @PostMapping("/requests/calculate-cost")
    public ResponseEntity<Map<String, Object>> calculateEstimatedCost(
            @Valid @RequestBody CreateMovingRequestDto request) {
        
        log.debug("Calculating estimated cost for moving request");
        BigDecimal estimatedCost = movingLogisticsService.calculateEstimatedCost(request);
        
        Map<String, Object> response = Map.of(
            "estimatedCost", estimatedCost,
            "currency", "USD",
            "movingType", request.getMovingType(),
            "serviceType", request.getServiceType()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Cancel moving request", 
               description = "Cancel a moving request with a reason")
    @PutMapping("/requests/{requestId}/cancel")
    public ResponseEntity<MovingRequestResponseDto> cancelMovingRequest(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        
        log.info("Cancelling moving request: {} with reason: {}", requestId, reason);
        MovingRequestResponseDto response = movingLogisticsService.cancelMovingRequest(requestId, reason);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Complete moving request", 
               description = "Mark a moving request as completed with final cost")
    @PutMapping("/requests/{requestId}/complete")
    public ResponseEntity<MovingRequestResponseDto> completeMovingRequest(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Parameter(description = "Final cost") @RequestParam BigDecimal finalCost) {
        
        log.info("Completing moving request: {} with final cost: {}", requestId, finalCost);
        MovingRequestResponseDto response = movingLogisticsService.completeMovingRequest(requestId, finalCost);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get moving statistics", 
               description = "Get comprehensive statistics about moving requests")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getMovingStatistics() {
        
        log.debug("Generating moving statistics");
        Map<String, Object> response = movingLogisticsService.getMovingStatistics();
        return ResponseEntity.ok(response);
    }
}