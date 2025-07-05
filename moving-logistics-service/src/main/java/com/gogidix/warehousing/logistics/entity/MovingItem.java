package com.gogidix.warehousing.logistics.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Moving Item Entity
 */
@Entity
@Table(name = "moving_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovingItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moving_request_id", nullable = false)
    private MovingRequest movingRequest;
    
    @Column(nullable = false)
    private String itemName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;
    
    private Integer quantity = 1;
    
    private BigDecimal weight;
    
    private String dimensions;
    
    @Column(nullable = false)
    private Boolean isFragile = false;
    
    @Column(nullable = false)
    private Boolean requiresDisassembly = false;
    
    @Column(columnDefinition = "TEXT")
    private String specialHandling;
    
    public enum ItemCategory {
        FURNITURE, APPLIANCES, ELECTRONICS, CLOTHING, DOCUMENTS, ARTWORK, FRAGILE, OTHER
    }
}