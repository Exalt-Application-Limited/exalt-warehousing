package com.gogidix.warehousing.operations.service.impl;

import com.gogidix.warehousing.operations.entity.WarehouseZone;
import com.gogidix.warehousing.operations.enums.ZoneType;
import com.gogidix.warehousing.operations.repository.WarehouseZoneRepository;
import com.gogidix.warehousing.operations.service.WarehouseZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of WarehouseZoneService
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseZoneServiceImpl implements WarehouseZoneService {

    private final WarehouseZoneRepository warehouseZoneRepository;

    @Override
    public WarehouseZone createZone(WarehouseZone zone) {
        return warehouseZoneRepository.save(zone);
    }

    @Override
    public WarehouseZone updateZone(Long id, WarehouseZone zone) {
        zone.setId(id);
        return warehouseZoneRepository.save(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseZone> findById(Long id) {
        return warehouseZoneRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseZone> findByLayoutId(Long layoutId) {
        return warehouseZoneRepository.findByWarehouseLayoutId(layoutId);
    }
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseZone> findByZoneType(ZoneType zoneType) {
        return warehouseZoneRepository.findByZoneType(zoneType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseZone> findActiveZones() {
        return warehouseZoneRepository.findByIsActiveTrue();
    }

    @Override
    public void deleteZone(Long id) {
        warehouseZoneRepository.deleteById(id);
    }

    @Override
    public WarehouseZone activateZone(Long id) {
        WarehouseZone zone = warehouseZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
        zone.setIsActive(true);
        return warehouseZoneRepository.save(zone);
    }

    @Override
    public WarehouseZone deactivateZone(Long id) {
        WarehouseZone zone = warehouseZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
        zone.setIsActive(false);
        return warehouseZoneRepository.save(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalCapacityByLayout(Long layoutId) {
        return warehouseZoneRepository.calculateTotalCapacityByLayout(layoutId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageUtilization(Long layoutId) {
        return warehouseZoneRepository.calculateAverageUtilizationByLayout(layoutId);
    }
}

