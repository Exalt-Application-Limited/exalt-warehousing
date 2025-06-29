package com.exalt.warehousing.operations.service;

import com.exalt.warehousing.operations.entity.Equipment;
import com.exalt.warehousing.operations.enums.EquipmentStatus;
import com.exalt.warehousing.operations.enums.EquipmentType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Equipment operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public interface EquipmentService {

    Equipment createEquipment(Equipment equipment);
    
    Equipment updateEquipment(Long id, Equipment equipment);
    
    Optional<Equipment> findById(Long id);
    
    List<Equipment> findByType(EquipmentType type);
    
    List<Equipment> findByStatus(EquipmentStatus status);
    
    List<Equipment> findByZoneId(Long zoneId);
    
    List<Equipment> findByStaffId(Long staffId);
    
    void deleteEquipment(Long id);
    
    Equipment assignToStaff(Long equipmentId, Long staffId);
    
    Equipment unassignFromStaff(Long equipmentId);
    
    List<Equipment> findEquipmentNeedingMaintenance();
    
    Equipment scheduleMaintenace(Long equipmentId);
}

