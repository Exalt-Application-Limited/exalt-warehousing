package com.exalt.warehousing.operations.service;

import com.exalt.warehousing.operations.entity.WarehouseZone;
import com.exalt.warehousing.operations.enums.ZoneType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for WarehouseZone operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public interface WarehouseZoneService {

    WarehouseZone createZone(WarehouseZone zone);
    
    WarehouseZone updateZone(Long id, WarehouseZone zone);
    
    Optional<WarehouseZone> findById(Long id);
    
    List<WarehouseZone> findByLayoutId(Long layoutId);
    
    List<WarehouseZone> findByZoneType(ZoneType zoneType);
    
    List<WarehouseZone> findActiveZones();
    
    void deleteZone(Long id);
    
    WarehouseZone activateZone(Long id);
    
    WarehouseZone deactivateZone(Long id);
    
    Double calculateTotalCapacityByLayout(Long layoutId);
    
    Double calculateAverageUtilization(Long layoutId);
}

