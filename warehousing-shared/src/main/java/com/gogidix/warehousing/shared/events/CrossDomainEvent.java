package com.gogidix.warehousing.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.UUID;

/**
 * Base class for events that need to be published across domains.
 * This event format is compatible with the centralized dashboard integration.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CrossDomainEvent extends BaseEvent {

    /**
     * The target domain for this event
     */
    private String targetDomain;
    
    /**
     * The specific service within the target domain
     */
    private String targetService;
    
    /**
     * The priority of the event
     */
    private EventPriority priority;
    
    /**
     * Additional routing information for the event
     */
    private Map<String, String> routingMetadata;
    
    /**
     * Business entity ID related to this event
     */
    private UUID businessEntityId;
    
    /**
     * Business entity type
     */
    private String businessEntityType;
    
    /**
     * Priority levels for cross-domain events
     */
    public enum EventPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    /**
     * Constructor with basic fields
     */
    public CrossDomainEvent(String eventType, String targetDomain, String targetService, 
                           EventPriority priority, UUID businessEntityId, String businessEntityType) {
        super(eventType, businessEntityId.toString(), businessEntityType);
        this.targetDomain = targetDomain;
        this.targetService = targetService;
        this.priority = priority;
        this.businessEntityId = businessEntityId;
        this.businessEntityType = businessEntityType;
    }
}