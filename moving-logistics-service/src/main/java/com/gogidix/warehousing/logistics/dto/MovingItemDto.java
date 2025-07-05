package com.gogidix.warehousing.logistics.dto;

import com.gogidix.warehousing.logistics.entity.MovingItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Moving Item DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovingItemDto {
    
    private Long id;
    private String itemName;
    private String description;
    private MovingItem.ItemCategory category;
    private Integer quantity;
    private BigDecimal weight;
    private String dimensions;
    private Boolean isFragile;
    private Boolean requiresDisassembly;
    private String specialHandling;
}