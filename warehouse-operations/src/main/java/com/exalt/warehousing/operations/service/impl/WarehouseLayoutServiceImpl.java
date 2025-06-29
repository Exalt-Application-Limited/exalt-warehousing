package com.exalt.warehousing.operations.service.impl;

import com.exalt.warehousing.operations.entity.WarehouseLayout;
import com.exalt.warehousing.operations.enums.LayoutStatus;
import com.exalt.warehousing.operations.repository.WarehouseLayoutRepository;
import com.exalt.warehousing.operations.service.WarehouseLayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseLayoutServiceImpl implements WarehouseLayoutService {

    private final WarehouseLayoutRepository warehouseLayoutRepository;

    @Override
    public WarehouseLayout createLayout(WarehouseLayout layout) {
        log.info("Creating warehouse layout: {}", layout.getLayoutName());
        layout.setStatus(LayoutStatus.DRAFT);
        layout.setCreatedAt(LocalDateTime.now());
        layout.setUpdatedAt(LocalDateTime.now());
        return warehouseLayoutRepository.save(layout);
    }

    @Override
    public Optional<WarehouseLayout> findById(Long id) {
        return warehouseLayoutRepository.findById(id);
    }

    @Override
    public Optional<WarehouseLayout> findByLayoutCode(String layoutCode) {
        return warehouseLayoutRepository.findByLayoutCode(layoutCode);
    }

    @Override
    public List<WarehouseLayout> findAll() {
        return warehouseLayoutRepository.findAll();
    }

    @Override
    public Optional<WarehouseLayout> findActiveLayoutByWarehouse(Long warehouseId) {
        return warehouseLayoutRepository.findActiveLayoutByWarehouseId(warehouseId);
    }

    @Override
    public Optional<WarehouseLayout> findActiveLayoutByVendor(Long vendorId) {
        return warehouseLayoutRepository.findActiveLayoutByVendorId(vendorId);
    }

    @Override
    public WarehouseLayout updateLayout(Long id, WarehouseLayout updatedLayout) {
        return warehouseLayoutRepository.findById(id)
                .map(layout -> {
                    layout.setLayoutName(updatedLayout.getLayoutName());
                    layout.setNotes(updatedLayout.getNotes());
                    layout.setLayoutJson(updatedLayout.getLayoutJson());
                    layout.setVersion(layout.getVersion() + 1);
                    layout.setUpdatedAt(LocalDateTime.now());
                    return warehouseLayoutRepository.save(layout);
                })
                .orElseThrow(() -> new RuntimeException("Warehouse layout not found with id: " + id));
    }

    @Override
    public WarehouseLayout activateLayout(Long id) {
        return warehouseLayoutRepository.findById(id)
                .map(layout -> {
                    // Deactivate any existing active layout for the warehouse
                    warehouseLayoutRepository.findActiveLayoutByWarehouseId(layout.getWarehouseId())
                            .ifPresent(activeLayout -> {
                                activeLayout.setStatus(LayoutStatus.ARCHIVED);
                                warehouseLayoutRepository.save(activeLayout);
                            });
                    
                    layout.setStatus(LayoutStatus.ACTIVE);
                    layout.setEffectiveDate(LocalDateTime.now());
                    layout.setUpdatedAt(LocalDateTime.now());
                    return warehouseLayoutRepository.save(layout);
                })
                .orElseThrow(() -> new RuntimeException("Warehouse layout not found with id: " + id));
    }

    @Override
    public WarehouseLayout deactivateLayout(Long id) {
        return warehouseLayoutRepository.findById(id)
                .map(layout -> {
                    layout.setStatus(LayoutStatus.ARCHIVED);
                    layout.setUpdatedAt(LocalDateTime.now());
                    return warehouseLayoutRepository.save(layout);
                })
                .orElseThrow(() -> new RuntimeException("Warehouse layout not found with id: " + id));
    }

    @Override
    public WarehouseLayout optimizeLayout(Long id) {
        return warehouseLayoutRepository.findById(id)
                .map(layout -> {
                    // Perform layout optimization logic here
                    layout.setOptimizationScore(calculateOptimizationScore(id));
                    layout.setUpdatedAt(LocalDateTime.now());
                    return warehouseLayoutRepository.save(layout);
                })
                .orElseThrow(() -> new RuntimeException("Warehouse layout not found with id: " + id));
    }

    @Override
    public List<WarehouseLayout> findLayoutsNeedingOptimization() {
        return warehouseLayoutRepository.findLayoutsNeedingOptimization(LocalDateTime.now());
    }

    @Override
    public Double calculateOptimizationScore(Long id) {
        return warehouseLayoutRepository.findById(id)
                .map(layout -> {
                    // Implement optimization score calculation logic
                    return 85.0; // Default score
                })
                .orElse(0.0);
    }

    @Override
    public Double getAverageOptimizationScore() {
        return 82.5; // Default average score
    }

    @Override
    public Double getAverageSpaceUtilization() {
        return 75.3; // Default average utilization
    }

    @Override
    public List<WarehouseLayout> getTopPerformingLayouts(int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return warehouseLayoutRepository.findTopPerformingLayouts(pageable);
    }

    @Override
    public void deleteLayout(Long id) {
        warehouseLayoutRepository.deleteById(id);
    }

    @Override
    public Page<WarehouseLayout> searchLayouts(
            String layoutName, String layoutType, LayoutStatus status, 
            Boolean isActive, Long warehouseId, Long vendorId, 
            Pageable pageable) {
        return warehouseLayoutRepository.findBySearchCriteria(
                layoutName, layoutType, status, isActive, warehouseId, vendorId, pageable);
    }
}

