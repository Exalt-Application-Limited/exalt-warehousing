package com.exalt.warehousing.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for tracking information.
 * Used for shipment tracking across multiple carriers and services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackingInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tracking number/ID
     */
    @NotBlank(message = "Tracking number is required")
    @Size(max = 100, message = "Tracking number cannot exceed 100 characters")
    private String trackingNumber;

    /**
     * Shipping carrier name
     */
    @NotBlank(message = "Carrier is required")
    @Size(max = 50, message = "Carrier name cannot exceed 50 characters")
    private String carrier;

    /**
     * Current status of the shipment
     */
    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status;

    /**
     * Detailed status description
     */
    @Size(max = 500, message = "Status description cannot exceed 500 characters")
    private String statusDescription;

    /**
     * Current location of the shipment
     */
    @Size(max = 200, message = "Current location cannot exceed 200 characters")
    private String currentLocation;

    /**
     * Estimated delivery date
     */
    private LocalDateTime estimatedDelivery;

    /**
     * Actual delivery date (if delivered)
     */
    private LocalDateTime actualDelivery;

    /**
     * Shipment creation date
     */
    private LocalDateTime shipmentDate;

    /**
     * Last update timestamp
     */
    private LocalDateTime lastUpdated;

    /**
     * Origin address
     */
    private AddressDTO originAddress;

    /**
     * Destination address
     */
    private AddressDTO destinationAddress;

    /**
     * List of tracking events/history
     */
    private List<TrackingEventDTO> events;

    /**
     * Weight of the package
     */
    private Double weight;

    /**
     * Weight unit (kg, lbs, etc.)
     */
    private String weightUnit;

    /**
     * Package dimensions (length x width x height)
     */
    private String dimensions;

    /**
     * Service type (express, standard, etc.)
     */
    private String serviceType;

    /**
     * Special instructions or notes
     */
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    /**
     * Signature required flag
     */
    private Boolean signatureRequired;

    /**
     * Delivery confirmation details
     */
    private String deliveryConfirmation;

    /**
     * Exception details if any issues occurred
     */
    private String exception;

    /**
     * Data Transfer Object for tracking events
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrackingEventDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * Event timestamp
         */
        private LocalDateTime timestamp;

        /**
         * Event status/type
         */
        @NotBlank(message = "Event status is required")
        private String status;

        /**
         * Event description
         */
        @Size(max = 500, message = "Event description cannot exceed 500 characters")
        private String description;

        /**
         * Location where event occurred
         */
        @Size(max = 200, message = "Location cannot exceed 200 characters")
        private String location;

        /**
         * Additional event details
         */
        @Size(max = 1000, message = "Details cannot exceed 1000 characters")
        private String details;
    }

    /**
     * Check if shipment is delivered
     * 
     * @return true if delivered, false otherwise
     */
    public boolean isDelivered() {
        return "DELIVERED".equalsIgnoreCase(status) || actualDelivery != null;
    }

    /**
     * Check if shipment is in transit
     * 
     * @return true if in transit, false otherwise
     */
    public boolean isInTransit() {
        return "IN_TRANSIT".equalsIgnoreCase(status) || 
               "OUT_FOR_DELIVERY".equalsIgnoreCase(status);
    }

    /**
     * Check if shipment has exception/issue
     * 
     * @return true if has exception, false otherwise
     */
    public boolean hasException() {
        return exception != null && !exception.isBlank();
    }

    /**
     * Get delivery status summary
     * 
     * @return String summary of delivery status
     */
    public String getDeliveryStatusSummary() {
        if (isDelivered()) {
            return "Delivered" + (actualDelivery != null ? " on " + actualDelivery.toLocalDate() : "");
        } else if (hasException()) {
            return "Exception: " + exception;
        } else if (isInTransit()) {
            return "In Transit" + (estimatedDelivery != null ? " (Est. " + estimatedDelivery.toLocalDate() + ")" : "");
        } else {
            return status != null ? status.replace("_", " ") : "Unknown";
        }
    }
}
