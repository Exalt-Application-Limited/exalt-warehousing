package com.gogidix.warehousing.logistics.dto;

import com.gogidix.warehousing.logistics.entity.MovingRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Moving Request Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovingRequestResponseDto {
    
    private Long id;
    private String requestNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private MovingRequest.MovingType movingType;
    private MovingRequest.ServiceType serviceType;
    private LocalDate preferredDate;
    private LocalDate alternativeDate;
    private String pickupAddress;
    private String deliveryAddress;
    private BigDecimal estimatedWeight;
    private Integer numberOfItems;
    private String specialInstructions;
    private MovingRequest.RequestStatus status;
    private BigDecimal estimatedCost;
    private BigDecimal finalCost;
    private Long assignedDriverId;
    private String assignedDriverName;
    private Long assignedVehicleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private List<MovingItemDto> items;
    private DeliveryScheduleDto schedule;
}