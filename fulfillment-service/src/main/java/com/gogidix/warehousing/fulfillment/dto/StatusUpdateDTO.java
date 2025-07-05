package com.gogidix.warehousing.fulfillment.dto;

import com.gogidix.warehousing.fulfillment.enums.FulfillmentStatus;
import com.gogidix.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.gogidix.warehousing.fulfillment.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for status updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDTO {

    /**
     * New fulfillment order status
     */
    private FulfillmentStatus status;
    
    /**
     * New item fulfillment status
     */
    private ItemFulfillmentStatus itemStatus;
    
    /**
     * New shipment status
     */
    private ShipmentStatus shipmentStatus;
    
    /**
     * Optional reason for status change
     */
    private String reason;
} 
