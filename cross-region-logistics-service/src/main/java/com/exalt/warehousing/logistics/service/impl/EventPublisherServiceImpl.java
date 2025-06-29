package com.exalt.warehousing.logistics.service.impl;

import com.exalt.warehousing.logistics.event.TransferEvent;
import com.exalt.warehousing.logistics.model.TransferRequest;
import com.exalt.warehousing.logistics.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of EventPublisherService for publishing transfer events
 */
@Service
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    private static final String TRANSFER_EVENTS_TOPIC = "transfer-events";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Override
    public void publishTransferCreated(TransferRequest transferRequest) {
        log.debug("Publishing transfer created event for transfer request: {}", transferRequest.getId());
        
        TransferEvent event = TransferEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TRANSFER_CREATED")
                .transferId(UUID.fromString(transferRequest.getId()))
                .referenceNumber(transferRequest.getReferenceNumber())
                .sourceWarehouseId(transferRequest.getSourceWarehouseId())
                .destinationWarehouseId(transferRequest.getDestinationWarehouseId())
                .status(transferRequest.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        
        sendEvent(event);
    }
    
    @Override
    public void publishTransferStatusChanged(TransferRequest transferRequest, String previousStatus) {
        log.debug("Publishing transfer status changed event for transfer request: {}", transferRequest.getId());
        
        TransferEvent event = TransferEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TRANSFER_STATUS_CHANGED")
                .transferId(UUID.fromString(transferRequest.getId()))
                .referenceNumber(transferRequest.getReferenceNumber())
                .sourceWarehouseId(transferRequest.getSourceWarehouseId())
                .destinationWarehouseId(transferRequest.getDestinationWarehouseId())
                .status(transferRequest.getStatus())
                .timestamp(LocalDateTime.now())
                .metadata(previousStatus)
                .build();
        
        sendEvent(event);
    }
    
    @Override
    public void publishTransferCancelled(TransferRequest transferRequest) {
        log.debug("Publishing transfer cancelled event for transfer request: {}", transferRequest.getId());
        
        TransferEvent event = TransferEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TRANSFER_CANCELLED")
                .transferId(UUID.fromString(transferRequest.getId()))
                .referenceNumber(transferRequest.getReferenceNumber())
                .sourceWarehouseId(transferRequest.getSourceWarehouseId())
                .destinationWarehouseId(transferRequest.getDestinationWarehouseId())
                .status(transferRequest.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        
        sendEvent(event);
    }
    
    @Override
    public void publishTransferCompleted(TransferRequest transferRequest) {
        log.debug("Publishing transfer completed event for transfer request: {}", transferRequest.getId());
        
        TransferEvent event = TransferEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TRANSFER_COMPLETED")
                .transferId(UUID.fromString(transferRequest.getId()))
                .referenceNumber(transferRequest.getReferenceNumber())
                .sourceWarehouseId(transferRequest.getSourceWarehouseId())
                .destinationWarehouseId(transferRequest.getDestinationWarehouseId())
                .status(transferRequest.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
        
        sendEvent(event);
    }
    
    /**
     * Send an event to Kafka
     *
     * @param event the event to send
     */
    private void sendEvent(TransferEvent event) {
        try {
            kafkaTemplate.send(TRANSFER_EVENTS_TOPIC, event.getTransferId().toString(), event);
            log.debug("Successfully sent event: {}", event.getEventType());
        } catch (Exception e) {
            log.error("Failed to send event: {}", event.getEventType(), e);
        }
    }
} 
