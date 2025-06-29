package com.exalt.warehousing.logistics.event;

import com.exalt.warehousing.logistics.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener for Kafka events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {

    private final TransferService transferService;

    /**
     * Listen for inventory events
     *
     * @param event the inventory event
     */
    @KafkaListener(topics = "inventory-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeInventoryEvent(InventoryEvent event) {
        log.debug("Received inventory event: {}", event);
        
        try {
            switch (event.getEventType()) {
                case "INVENTORY_ALLOCATED":
                case "INVENTORY_RESERVED":
                    // Handle inventory allocation/reservation events if needed
                    log.debug("Received inventory allocation/reservation event: {}", event.getEventType());
                    break;
                    
                case "INVENTORY_LEVEL_CRITICAL":
                    // Check if any active transfers involve this inventory item
                    log.warn("Inventory level critical for item: {}", event.getInventoryItemId());
                    break;
                    
                default:
                    log.debug("Ignoring inventory event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing inventory event: {}", e.getMessage(), e);
        }
    }

    /**
     * Listen for warehouse events
     *
     * @param event the warehouse event
     */
    @KafkaListener(topics = "warehouse-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeWarehouseEvent(WarehouseEvent event) {
        log.debug("Received warehouse event: {}", event);
        
        try {
            switch (event.getEventType()) {
                case "WAREHOUSE_DISABLED":
                    // Check if any active transfers involve this warehouse
                    log.warn("Warehouse disabled: {}", event.getWarehouseId());
                    // Find affected transfers and take appropriate action
                    break;
                    
                case "WAREHOUSE_ENABLED":
                    log.info("Warehouse enabled: {}", event.getWarehouseId());
                    break;
                    
                case "WAREHOUSE_CAPACITY_CHANGE":
                    log.info("Warehouse capacity changed: {}", event.getWarehouseId());
                    break;
                    
                default:
                    log.debug("Ignoring warehouse event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing warehouse event: {}", e.getMessage(), e);
        }
    }
} 
