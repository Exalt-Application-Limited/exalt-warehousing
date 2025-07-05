package com.gogidix.warehousing.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for shipment tracking information.
 * Used for integration with courier services and third-party carriers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfoDTO {

    private UUID shipmentId;
    private String trackingNumber;
    private String carrierCode;
    private String carrierName;
    private String status;
    private String estimatedDeliveryDate;
    private String originAddress;
    private String destinationAddress;
    private LocalDateTime shippedDate;
    private LocalDateTime updatedAt;
    private Double weight;
    private String weightUnit;
    private Integer numberOfPackages;
    private String serviceType;
    private String signatureRequired;
    private String specialInstructions;
    private List<TrackingEventDTO> events;
    private String labelUrl;
    private Boolean delivered;
    private LocalDateTime deliveredAt;
    private String signedBy;
    private String proofOfDeliveryUrl;
    
    /**
     * Nested DTO for tracking events/checkpoints
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEventDTO {
        private LocalDateTime timestamp;
        private String status;
        private String location;
        private String description;
        private String eventCode;
    }
}