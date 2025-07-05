package com.gogidix.warehousing.logistics.service.impl;

import com.gogidix.warehousing.logistics.dto.*;
import com.gogidix.warehousing.logistics.entity.*;
import com.gogidix.warehousing.logistics.repository.MovingRequestRepository;
import com.gogidix.warehousing.logistics.service.MovingLogisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Moving Logistics Service Implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovingLogisticsServiceImpl implements MovingLogisticsService {
    
    private final MovingRequestRepository movingRequestRepository;
    
    @Override
    public MovingRequestResponseDto createMovingRequest(CreateMovingRequestDto request) {
        log.info("Creating moving request for customer: {}", request.getCustomerId());
        
        // Calculate estimated cost
        BigDecimal estimatedCost = calculateEstimatedCost(request);
        
        // Create moving request entity
        MovingRequest movingRequest = MovingRequest.builder()
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerPhone(request.getCustomerPhone())
            .movingType(request.getMovingType())
            .serviceType(request.getServiceType())
            .preferredDate(request.getPreferredDate())
            .alternativeDate(request.getAlternativeDate())
            .pickupAddress(request.getPickupAddress())
            .deliveryAddress(request.getDeliveryAddress())
            .estimatedWeight(request.getEstimatedWeight())
            .numberOfItems(request.getNumberOfItems())
            .specialInstructions(request.getSpecialInstructions())
            .estimatedCost(estimatedCost)
            .status(MovingRequest.RequestStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();
        
        movingRequest = movingRequestRepository.save(movingRequest);
        
        // Add items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            final MovingRequest savedRequest = movingRequest;
            List<MovingItem> items = request.getItems().stream()
                .map(itemDto -> MovingItem.builder()
                    .movingRequest(savedRequest)
                    .itemName(itemDto.getItemName())
                    .description(itemDto.getDescription())
                    .category(itemDto.getCategory())
                    .quantity(itemDto.getQuantity())
                    .weight(itemDto.getWeight())
                    .dimensions(itemDto.getDimensions())
                    .isFragile(itemDto.getIsFragile())
                    .requiresDisassembly(itemDto.getRequiresDisassembly())
                    .specialHandling(itemDto.getSpecialHandling())
                    .build())
                .collect(Collectors.toList());
            savedRequest.setItems(items);
            movingRequest = movingRequestRepository.save(savedRequest);
        }
        
        log.info("Created moving request: {} for customer: {}", 
                movingRequest.getRequestNumber(), request.getCustomerId());
        return mapToResponseDto(movingRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MovingRequestResponseDto getMovingRequestById(Long requestId) {
        log.debug("Retrieving moving request by ID: {}", requestId);
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        return mapToResponseDto(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MovingRequestResponseDto getMovingRequestByNumber(String requestNumber) {
        log.debug("Retrieving moving request by number: {}", requestNumber);
        MovingRequest request = movingRequestRepository.findByRequestNumber(requestNumber)
            .orElseThrow(() -> new RuntimeException("Moving request not found with number: " + requestNumber));
        return mapToResponseDto(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MovingRequestResponseDto> getCustomerMovingRequests(Long customerId, Pageable pageable) {
        log.debug("Retrieving moving requests for customer: {}", customerId);
        return movingRequestRepository.findByCustomerId(customerId, pageable)
            .map(this::mapToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MovingRequestResponseDto> getMovingRequestsByStatus(MovingRequest.RequestStatus status, Pageable pageable) {
        log.debug("Retrieving moving requests with status: {}", status);
        return movingRequestRepository.findByStatus(status, pageable)
            .map(this::mapToResponseDto);
    }
    
    @Override
    public MovingRequestResponseDto updateRequestStatus(Long requestId, MovingRequest.RequestStatus status) {
        log.info("Updating moving request {} status to: {}", requestId, status);
        
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        
        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());
        
        if (status == MovingRequest.RequestStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
        }
        
        request = movingRequestRepository.save(request);
        
        log.info("Updated moving request {} status to: {}", requestId, status);
        return mapToResponseDto(request);
    }
    
    @Override
    public MovingRequestResponseDto generateQuote(Long requestId, BigDecimal quotedAmount) {
        log.info("Generating quote for moving request: {} amount: {}", requestId, quotedAmount);
        
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        
        request.setEstimatedCost(quotedAmount);
        request.setStatus(MovingRequest.RequestStatus.QUOTED);
        request.setUpdatedAt(LocalDateTime.now());
        
        request = movingRequestRepository.save(request);
        
        log.info("Generated quote for moving request: {}", requestId);
        return mapToResponseDto(request);
    }
    
    @Override
    public MovingRequestResponseDto assignDriver(Long requestId, Long driverId, String driverName) {
        log.info("Assigning driver {} ({}) to moving request: {}", driverName, driverId, requestId);
        
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        
        request.setAssignedDriverId(driverId);
        request.setAssignedDriverName(driverName);
        request.setUpdatedAt(LocalDateTime.now());
        
        request = movingRequestRepository.save(request);
        
        log.info("Assigned driver {} to moving request: {}", driverName, requestId);
        return mapToResponseDto(request);
    }
    
    @Override
    public MovingRequestResponseDto scheduleMoving(Long requestId, ScheduleMovingRequestDto scheduleDto) {
        log.info("Scheduling moving request: {}", requestId);
        
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        
        request.setAssignedDriverId(scheduleDto.getAssignedDriverId());
        request.setAssignedDriverName(scheduleDto.getAssignedDriverName());
        request.setAssignedVehicleId(scheduleDto.getAssignedVehicleId());
        request.setStatus(MovingRequest.RequestStatus.SCHEDULED);
        request.setScheduledAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        request = movingRequestRepository.save(request);
        
        log.info("Scheduled moving request: {}", requestId);
        return mapToResponseDto(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovingRequestResponseDto> getUnassignedRequests() {
        log.debug("Retrieving unassigned moving requests");
        return movingRequestRepository.findByAssignedDriverIdIsNull()
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovingRequestResponseDto> getRequestsForDate(LocalDate date) {
        log.debug("Retrieving moving requests for date: {}", date);
        return movingRequestRepository.findByPreferredDate(date)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovingRequestResponseDto> getOverdueRequests() {
        log.debug("Retrieving overdue moving requests");
        return movingRequestRepository.findOverdueRequests(LocalDate.now())
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateEstimatedCost(CreateMovingRequestDto request) {
        log.debug("Calculating estimated cost for moving request");
        
        BigDecimal baseCost = BigDecimal.valueOf(100); // Base moving cost
        BigDecimal itemCost = BigDecimal.ZERO;
        
        // Calculate cost based on number of items
        if (request.getNumberOfItems() != null) {
            itemCost = BigDecimal.valueOf(request.getNumberOfItems() * 15);
        }
        
        // Calculate cost based on weight
        BigDecimal weightCost = BigDecimal.ZERO;
        if (request.getEstimatedWeight() != null) {
            weightCost = request.getEstimatedWeight().multiply(BigDecimal.valueOf(2));
        }
        
        // Service type multiplier
        BigDecimal multiplier = getServiceTypeMultiplier(request.getServiceType());
        
        BigDecimal totalCost = baseCost.add(itemCost).add(weightCost).multiply(multiplier);
        
        return totalCost.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMovingStatistics() {
        log.debug("Generating moving statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Status statistics
        List<Object[]> statusStats = movingRequestRepository.getRequestStatsByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("statusCounts", statusCounts);
        
        // Moving type statistics
        List<Object[]> typeStats = movingRequestRepository.getRequestStatsByMovingType();
        Map<String, Long> typeCounts = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("movingTypeCounts", typeCounts);
        
        // Total requests
        long totalRequests = movingRequestRepository.count();
        stats.put("totalRequests", totalRequests);
        
        return stats;
    }
    
    @Override
    public MovingRequestResponseDto cancelMovingRequest(Long requestId, String reason) {
        log.info("Cancelling moving request: {} with reason: {}", requestId, reason);
        
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        
        request.setStatus(MovingRequest.RequestStatus.CANCELLED);
        request.setSpecialInstructions(request.getSpecialInstructions() + "\nCancellation reason: " + reason);
        request.setUpdatedAt(LocalDateTime.now());
        
        request = movingRequestRepository.save(request);
        
        log.info("Cancelled moving request: {}", requestId);
        return mapToResponseDto(request);
    }
    
    @Override
    public MovingRequestResponseDto completeMovingRequest(Long requestId, BigDecimal finalCost) {
        log.info("Completing moving request: {} with final cost: {}", requestId, finalCost);
        
        MovingRequest request = movingRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Moving request not found with ID: " + requestId));
        
        request.setStatus(MovingRequest.RequestStatus.COMPLETED);
        request.setFinalCost(finalCost);
        request.setCompletedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        request = movingRequestRepository.save(request);
        
        log.info("Completed moving request: {}", requestId);
        return mapToResponseDto(request);
    }
    
    private BigDecimal getServiceTypeMultiplier(MovingRequest.ServiceType serviceType) {
        return switch (serviceType) {
            case BASIC_MOVING -> BigDecimal.valueOf(1.0);
            case FULL_SERVICE -> BigDecimal.valueOf(1.5);
            case PACKING_ONLY -> BigDecimal.valueOf(0.8);
            case UNPACKING_ONLY -> BigDecimal.valueOf(0.8);
            case FURNITURE_ASSEMBLY -> BigDecimal.valueOf(1.2);
        };
    }
    
    private MovingRequestResponseDto mapToResponseDto(MovingRequest request) {
        List<MovingItemDto> itemDtos = null;
        if (request.getItems() != null) {
            itemDtos = request.getItems().stream()
                .map(item -> new MovingItemDto(
                    item.getId(),
                    item.getItemName(),
                    item.getDescription(),
                    item.getCategory(),
                    item.getQuantity(),
                    item.getWeight(),
                    item.getDimensions(),
                    item.getIsFragile(),
                    item.getRequiresDisassembly(),
                    item.getSpecialHandling()
                ))
                .collect(Collectors.toList());
        }
        
        return MovingRequestResponseDto.builder()
            .id(request.getId())
            .requestNumber(request.getRequestNumber())
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerPhone(request.getCustomerPhone())
            .movingType(request.getMovingType())
            .serviceType(request.getServiceType())
            .preferredDate(request.getPreferredDate())
            .alternativeDate(request.getAlternativeDate())
            .pickupAddress(request.getPickupAddress())
            .deliveryAddress(request.getDeliveryAddress())
            .estimatedWeight(request.getEstimatedWeight())
            .numberOfItems(request.getNumberOfItems())
            .specialInstructions(request.getSpecialInstructions())
            .status(request.getStatus())
            .estimatedCost(request.getEstimatedCost())
            .finalCost(request.getFinalCost())
            .assignedDriverId(request.getAssignedDriverId())
            .assignedDriverName(request.getAssignedDriverName())
            .assignedVehicleId(request.getAssignedVehicleId())
            .createdAt(request.getCreatedAt())
            .updatedAt(request.getUpdatedAt())
            .scheduledAt(request.getScheduledAt())
            .completedAt(request.getCompletedAt())
            .items(itemDtos)
            .build();
    }
}