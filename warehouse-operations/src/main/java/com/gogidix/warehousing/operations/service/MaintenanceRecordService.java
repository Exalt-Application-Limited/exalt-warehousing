package com.gogidix.warehousing.operations.service;

import com.gogidix.warehousing.operations.entity.MaintenanceRecord;
import com.gogidix.warehousing.operations.enums.MaintenanceFrequency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for MaintenanceRecord operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public interface MaintenanceRecordService {

    MaintenanceRecord createMaintenanceRecord(MaintenanceRecord record);
    
    MaintenanceRecord updateMaintenanceRecord(Long id, MaintenanceRecord record);
    
    Optional<MaintenanceRecord> findById(Long id);
    
    List<MaintenanceRecord> findByEquipmentId(Long equipmentId);
    
    List<MaintenanceRecord> findByMaintenanceType(String type);
    
    List<MaintenanceRecord> findByFrequency(MaintenanceFrequency frequency);
    
    List<MaintenanceRecord> findByUserId(Long userId);
    
    List<MaintenanceRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    void deleteMaintenanceRecord(Long id);
    
    List<MaintenanceRecord> findEmergencyMaintenanceRecords();
    
    Long countMaintenanceRecordsByEquipment(Long equipmentId);
}

