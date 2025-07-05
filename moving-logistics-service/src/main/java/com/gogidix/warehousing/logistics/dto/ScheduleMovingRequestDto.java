package com.gogidix.warehousing.logistics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Schedule Moving Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMovingRequestDto {
    
    @NotNull(message = "Pickup time is required")
    private LocalDateTime scheduledPickupTime;
    
    @NotNull(message = "Delivery time is required")
    private LocalDateTime scheduledDeliveryTime;
    
    @NotNull(message = "Driver ID is required")
    private Long assignedDriverId;
    
    @NotNull(message = "Driver name is required")
    private String assignedDriverName;
    
    @NotNull(message = "Driver phone is required")
    private String assignedDriverPhone;
    
    @NotNull(message = "Vehicle ID is required")
    private Long assignedVehicleId;
    
    private String vehicleDetails;
    
    private String notes;
}