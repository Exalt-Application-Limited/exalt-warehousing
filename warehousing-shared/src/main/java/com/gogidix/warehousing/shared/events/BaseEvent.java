package com.gogidix.warehousing.shared.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base event class for all warehouse events
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {
    
    private java.util.UUID eventId;
    private String eventType;
    private java.util.UUID aggregateId;
    private String aggregateType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String source;
    private String version;
    
    public BaseEvent(String eventType, String aggregateId, String aggregateType) {
        this.eventId = java.util.UUID.randomUUID();
        this.eventType = eventType;
        this.aggregateId = java.util.UUID.fromString(aggregateId);
        this.aggregateType = aggregateType;
        this.timestamp = LocalDateTime.now();
        this.source = "warehousing-service";
        this.version = "1.0";
    }
}
