package com.gogidix.warehousing.operations.repository;

import com.gogidix.warehousing.operations.entity.WarehouseZone;
import com.gogidix.warehousing.operations.enums.ZoneType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WarehouseZone entity operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, Long> {

    // Basic Queries
    Optional<WarehouseZone> findByZoneCode(String zoneCode);
    
    List<WarehouseZone> findByWarehouseLayoutId(Long layoutId);
    
    List<WarehouseZone> findByZoneType(ZoneType zoneType);
    
    List<WarehouseZone> findByIsActiveTrue();

    // Advanced Queries
    @Query("SELECT wz FROM WarehouseZone wz WHERE wz.warehouseLayout.id = :layoutId AND wz.isActive = true")
    List<WarehouseZone> findActiveZonesByLayoutId(@Param("layoutId") Long layoutId);
    
    @Query("SELECT wz FROM WarehouseZone wz WHERE wz.zoneType = :zoneType AND wz.isActive = true")
    List<WarehouseZone> findActiveZonesByType(@Param("zoneType") ZoneType zoneType);
    
    @Query("SELECT wz FROM WarehouseZone wz WHERE wz.currentCapacityPct >= :minCapacity ORDER BY wz.currentCapacityPct DESC")
    List<WarehouseZone> findByCapacityGreaterThanEqual(@Param("minCapacity") Double minCapacity);

    // Capacity and Utilization Queries
    @Query("SELECT SUM(wz.totalCapacityCuft) FROM WarehouseZone wz WHERE wz.warehouseLayout.id = :layoutId AND wz.isActive = true")
    Double calculateTotalCapacityByLayout(@Param("layoutId") Long layoutId);
    
    @Query("SELECT AVG(wz.currentCapacityPct) FROM WarehouseZone wz WHERE wz.warehouseLayout.id = :layoutId AND wz.isActive = true")
    Double calculateAverageUtilizationByLayout(@Param("layoutId") Long layoutId);

    // Search and Filter
    @Query("SELECT wz FROM WarehouseZone wz WHERE " +
           "(:zoneName IS NULL OR LOWER(wz.zoneName) LIKE LOWER(CONCAT('%', :zoneName, '%'))) AND " +
           "(:zoneType IS NULL OR wz.zoneType = :zoneType) AND " +
           "(:isActive IS NULL OR wz.isActive = :isActive) AND " +
           "(:layoutId IS NULL OR wz.warehouseLayout.id = :layoutId)")
    Page<WarehouseZone> findBySearchCriteria(
        @Param("zoneName") String zoneName,
        @Param("zoneType") ZoneType zoneType,
        @Param("isActive") Boolean isActive,
        @Param("layoutId") Long layoutId,
        Pageable pageable
    );
}

