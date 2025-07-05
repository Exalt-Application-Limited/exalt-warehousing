package com.gogidix.warehousing.operations.repository;

import com.gogidix.warehousing.operations.entity.Equipment;
import com.gogidix.warehousing.operations.enums.EquipmentStatus;
import com.gogidix.warehousing.operations.enums.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Equipment entity operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByEquipmentType(EquipmentType equipmentType);
    
    List<Equipment> findByStatus(EquipmentStatus status);
    
    List<Equipment> findByWarehouseZoneId(Long zoneId);
    
    List<Equipment> findByAssignedStaffId(Long staffId);
    
    @Query("SELECT e FROM Equipment e WHERE e.status = :status AND e.equipmentType = :type")
    List<Equipment> findByStatusAndType(@Param("status") EquipmentStatus status, @Param("type") EquipmentType type);
    
    @Query("SELECT e FROM Equipment e WHERE e.nextMaintenanceDate <= :date")
    List<Equipment> findEquipmentNeedingMaintenance(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.warehouseZone.id = :zoneId AND e.status = 'OPERATIONAL'")
    Long countOperationalEquipmentByZone(@Param("zoneId") Long zoneId);
    
    @Query("SELECT e FROM Equipment e WHERE e.status = 'MAINTENANCE' AND e.isActive = true")
    List<Equipment> findEquipmentInMaintenance();
}

