package com.gogidix.warehousing.logistics.dto;

import com.gogidix.warehousing.logistics.entity.DeliverySchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Delivery Schedule DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryScheduleDto {
    
    private Long id;
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime actualPickupTime;
    private LocalDateTime actualDeliveryTime;
    private Long assignedDriverId;
    private String assignedDriverName;
    private String assignedDriverPhone;
    private Long assignedVehicleId;
    private String vehicleDetails;
    private DeliverySchedule.ScheduleStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}