package com.gogidix.warehousing.operations.repository;

import com.gogidix.warehousing.operations.entity.MaintenanceRecord;
import com.gogidix.warehousing.operations.enums.MaintenanceFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for MaintenanceRecord entity operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findByEquipmentId(Long equipmentId);
    
    List<MaintenanceRecord> findByMaintenanceType(String maintenanceType);
    
    List<MaintenanceRecord> findByFrequency(MaintenanceFrequency frequency);
    
    List<MaintenanceRecord> findByPerformedByUserId(Long userId);
    
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.equipment.id = :equipmentId ORDER BY mr.performedAt DESC")
    List<MaintenanceRecord> findByEquipmentIdOrderByDateDesc(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.performedAt BETWEEN :startDate AND :endDate")
    List<MaintenanceRecord> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(mr) FROM MaintenanceRecord mr WHERE mr.equipment.id = :equipmentId")
    Long countMaintenanceRecordsByEquipment(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.isEmergencyMaintenance = true ORDER BY mr.performedAt DESC")
    List<MaintenanceRecord> findEmergencyMaintenanceRecords();
}

