package com.exalt.warehousing.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * Cross-domain shipment event for communicating with the courier services domain
 * about shipments that need to be picked up or delivered.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CrossDomainShipmentEvent extends CrossDomainEvent {

    private UUID shipmentId;
    private UUID orderId;
    private String orderNumber;
    private UUID customerId;
    private UUID warehouseId;
    private String warehouseName;
    private String warehouseAddress;
    private String destinationAddress;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String shipmentStatus;
    private LocalDateTime packagedAt;
    private LocalDateTime readyForPickupAt;
    private String trackingNumber;
    private String carrierCode;
    private String carrierName;
    private String serviceLevel;
    private Double totalWeight;
    private String weightUnit;
    private Integer packageCount;
    private Map<String, Object> packageDimensions;
    private Map<String, Object> additionalMetadata;
    private boolean isPriority;
    private boolean requiresSignature;
    private String specialInstructions;

    /**
     * Factory method to create a shipment ready event
     *
     * @param shipmentId the shipment ID
     * @param orderId the order ID
     * @param orderNumber the order number
     * @param customerId the customer ID
     * @param warehouseId the warehouse ID
     * @param warehouseName the warehouse name
     * @param warehouseAddress the warehouse address
     * @param destinationAddress the destination address
     * @param totalWeight the total weight
     * @param packageCount the package count
     * @param serviceLevel the service level
     * @return the cross-domain event
     */
    public static CrossDomainShipmentEvent createShipmentReadyEvent(
            UUID shipmentId,
            UUID orderId,
            String orderNumber,
            UUID customerId,
            UUID warehouseId,
            String warehouseName,
            String warehouseAddress,
            String destinationAddress,
            Double totalWeight,
            Integer packageCount,
            String serviceLevel) {

        return CrossDomainShipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("SHIPMENT_READY_FOR_PICKUP")
                .aggregateId(shipmentId)
                .aggregateType("Shipment")
                .targetDomain("courier-services")
                .targetService("pickup-service")
                .priority(EventPriority.HIGH)
                .businessEntityId(shipmentId)
                .businessEntityType("Shipment")
                .shipmentId(shipmentId)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .customerId(customerId)
                .warehouseId(warehouseId)
                .warehouseName(warehouseName)
                .warehouseAddress(warehouseAddress)
                .destinationAddress(destinationAddress)
                .shipmentStatus("READY_FOR_PICKUP")
                .readyForPickupAt(LocalDateTime.now())
                .packagedAt(LocalDateTime.now().minusMinutes(30))
                .totalWeight(totalWeight)
                .weightUnit("kg")
                .packageCount(packageCount)
                .serviceLevel(serviceLevel)
                .packageDimensions(new HashMap<>())
                .additionalMetadata(new HashMap<>())
                .build();
    }

    /**
     * Factory method to create a shipment status update event
     *
     * @param shipmentId the shipment ID
     * @param orderId the order ID
     * @param trackingNumber the tracking number
     * @param carrierCode the carrier code
     * @param carrierName the carrier name
     * @param shipmentStatus the shipment status
     * @return the cross-domain event
     */
    public static CrossDomainShipmentEvent createShipmentStatusUpdateEvent(
            UUID shipmentId,
            UUID orderId,
            String trackingNumber,
            String carrierCode,
            String carrierName,
            String shipmentStatus) {

        return CrossDomainShipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("SHIPMENT_STATUS_UPDATED")
                .aggregateId(shipmentId)
                .aggregateType("Shipment")
                .targetDomain("all")
                .targetService("notification-service")
                .priority(EventPriority.MEDIUM)
                .businessEntityId(shipmentId)
                .businessEntityType("Shipment")
                .shipmentId(shipmentId)
                .orderId(orderId)
                .trackingNumber(trackingNumber)
                .carrierCode(carrierCode)
                .carrierName(carrierName)
                .shipmentStatus(shipmentStatus)
                .additionalMetadata(new HashMap<>())
                .build();
    }

    /**
     * Factory method to create a delivery confirmation event
     *
     * @param shipmentId the shipment ID
     * @param orderId the order ID
     * @param orderNumber the order number
     * @param customerId the customer ID
     * @param trackingNumber the tracking number
     * @return the cross-domain event
     */
    public static CrossDomainShipmentEvent createDeliveryConfirmedEvent(
            UUID shipmentId,
            UUID orderId,
            String orderNumber,
            UUID customerId,
            String trackingNumber) {

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("deliveredAt", LocalDateTime.now().toString());

        return CrossDomainShipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("DELIVERY_CONFIRMED")
                .aggregateId(shipmentId)
                .aggregateType("Shipment")
                .targetDomain("social-commerce")
                .targetService("order-service")
                .priority(EventPriority.HIGH)
                .businessEntityId(shipmentId)
                .businessEntityType("Shipment")
                .shipmentId(shipmentId)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .customerId(customerId)
                .trackingNumber(trackingNumber)
                .shipmentStatus("DELIVERED")
                .additionalMetadata(metadata)
                .build();
    }
}