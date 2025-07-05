package com.gogidix.warehousing.operations.service;

import com.gogidix.warehousing.operations.entity.WarehouseLayout;
import com.gogidix.warehousing.operations.enums.LayoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for WarehouseLayout operations
 * 
 * Provides comprehensive warehouse layout management including optimization,
 * analytics, and advanced operational capabilities.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public interface WarehouseLayoutService {

    // Basic CRUD Operations
    WarehouseLayout createLayout(WarehouseLayout layout);
    
    WarehouseLayout updateLayout(Long id, WarehouseLayout layout);
    
    Optional<WarehouseLayout> findById(Long id);
    
    Optional<WarehouseLayout> findByLayoutCode(String layoutCode);
    
    List<WarehouseLayout> findAll();
    
    void deleteLayout(Long id);

    // Layout Management
    WarehouseLayout activateLayout(Long id);
    
    WarehouseLayout deactivateLayout(Long id);
    
    Optional<WarehouseLayout> findActiveLayoutByWarehouse(Long warehouseId);
    
    Optional<WarehouseLayout> findActiveLayoutByVendor(Long vendorId);

    // Optimization Services
    WarehouseLayout optimizeLayout(Long id);
    
    List<WarehouseLayout> findLayoutsNeedingOptimization();
    
    Double calculateOptimizationScore(Long id);

    // Analytics and Reporting
    Double getAverageOptimizationScore();
    
    Double getAverageSpaceUtilization();
    
    List<WarehouseLayout> getTopPerformingLayouts(int limit);

    // Search and Filter
    Page<WarehouseLayout> searchLayouts(String layoutName, String layoutType, 
                                      LayoutStatus status, Boolean isActive, 
                                      Long warehouseId, Long vendorId, Pageable pageable);
}

