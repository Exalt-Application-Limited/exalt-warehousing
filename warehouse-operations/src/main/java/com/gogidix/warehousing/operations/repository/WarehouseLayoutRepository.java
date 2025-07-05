package com.gogidix.warehousing.operations.repository;

import com.gogidix.warehousing.operations.entity.WarehouseLayout;
import com.gogidix.warehousing.operations.enums.LayoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WarehouseLayout entity operations
 * 
 * Provides comprehensive data access methods for warehouse layout management
 * including advanced querying, optimization, and analytics capabilities.
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Repository
public interface WarehouseLayoutRepository extends JpaRepository<WarehouseLayout, Long> {

    // Basic Queries
    Optional<WarehouseLayout> findByLayoutCode(String layoutCode);
    
    List<WarehouseLayout> findByWarehouseId(Long warehouseId);
    
    List<WarehouseLayout> findByVendorId(Long vendorId);
    
    List<WarehouseLayout> findByStatus(LayoutStatus status);
    
    List<WarehouseLayout> findByIsActiveTrue();
    
    List<WarehouseLayout> findByLayoutType(String layoutType);

    // Advanced Queries
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.warehouseId = :warehouseId AND wl.isActive = true")
    Optional<WarehouseLayout> findActiveLayoutByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.vendorId = :vendorId AND wl.isActive = true")
    Optional<WarehouseLayout> findActiveLayoutByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.status = :status AND wl.effectiveDate <= :currentDate AND (wl.expirationDate IS NULL OR wl.expirationDate > :currentDate)")
    List<WarehouseLayout> findEffectiveLayouts(@Param("status") LayoutStatus status, @Param("currentDate") LocalDateTime currentDate);

    // Optimization Queries
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.optimizationScore >= :minScore ORDER BY wl.optimizationScore DESC")
    List<WarehouseLayout> findByOptimizationScoreGreaterThanEqual(@Param("minScore") Double minScore);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.spaceUtilizationPct >= :minUtilization ORDER BY wl.spaceUtilizationPct DESC")
    List<WarehouseLayout> findBySpaceUtilizationGreaterThanEqual(@Param("minUtilization") Double minUtilization);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.pickingEfficiencyScore >= :minEfficiency ORDER BY wl.pickingEfficiencyScore DESC")
    List<WarehouseLayout> findByPickingEfficiencyGreaterThanEqual(@Param("minEfficiency") Double minEfficiency);

    // Capacity Queries
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.storageCapacityCuft >= :minCapacity ORDER BY wl.storageCapacityCuft DESC")
    List<WarehouseLayout> findByStorageCapacityGreaterThanEqual(@Param("minCapacity") Double minCapacity);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.maxStaffCapacity >= :minStaff ORDER BY wl.maxStaffCapacity DESC")
    List<WarehouseLayout> findByStaffCapacityGreaterThanEqual(@Param("minStaff") Integer minStaff);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.maxDailyThroughput >= :minThroughput ORDER BY wl.maxDailyThroughput DESC")
    List<WarehouseLayout> findByThroughputCapacityGreaterThanEqual(@Param("minThroughput") Integer minThroughput);

    // Feature-based Queries
    List<WarehouseLayout> findByHasAutomationTrue();
    
    List<WarehouseLayout> findByHasClimateControlTrue();
    
    List<WarehouseLayout> findByHasHazmatAreaTrue();
    
    List<WarehouseLayout> findByIsMultiLevelTrue();
    
    List<WarehouseLayout> findByHasMezzanineTrue();

    // Analytics Queries
    @Query("SELECT AVG(wl.optimizationScore) FROM WarehouseLayout wl WHERE wl.isActive = true")
    Double findAverageOptimizationScore();
    
    @Query("SELECT AVG(wl.spaceUtilizationPct) FROM WarehouseLayout wl WHERE wl.isActive = true")
    Double findAverageSpaceUtilization();
    
    @Query("SELECT AVG(wl.pickingEfficiencyScore) FROM WarehouseLayout wl WHERE wl.isActive = true")
    Double findAveragePickingEfficiency();
    
    @Query("SELECT COUNT(wl) FROM WarehouseLayout wl WHERE wl.hasAutomation = true AND wl.isActive = true")
    Long countAutomatedLayouts();
    
    @Query("SELECT COUNT(wl) FROM WarehouseLayout wl WHERE wl.hasClimateControl = true AND wl.isActive = true")
    Long countClimateControlledLayouts();

    // Search and Filter Queries
    @Query("SELECT wl FROM WarehouseLayout wl WHERE " +
           "(:layoutName IS NULL OR LOWER(wl.layoutName) LIKE LOWER(CONCAT('%', :layoutName, '%'))) AND " +
           "(:layoutType IS NULL OR wl.layoutType = :layoutType) AND " +
           "(:status IS NULL OR wl.status = :status) AND " +
           "(:isActive IS NULL OR wl.isActive = :isActive) AND " +
           "(:warehouseId IS NULL OR wl.warehouseId = :warehouseId) AND " +
           "(:vendorId IS NULL OR wl.vendorId = :vendorId)")
    Page<WarehouseLayout> findBySearchCriteria(
        @Param("layoutName") String layoutName,
        @Param("layoutType") String layoutType,
        @Param("status") LayoutStatus status,
        @Param("isActive") Boolean isActive,
        @Param("warehouseId") Long warehouseId,
        @Param("vendorId") Long vendorId,
        Pageable pageable
    );

    // Performance Queries
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.lastOptimizedAt IS NULL OR wl.lastOptimizedAt < :thresholdDate")
    List<WarehouseLayout> findLayoutsNeedingOptimization(@Param("thresholdDate") LocalDateTime thresholdDate);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.avgPickTimeSeconds > :maxPickTime ORDER BY wl.avgPickTimeSeconds DESC")
    List<WarehouseLayout> findLayoutsWithSlowPickTimes(@Param("maxPickTime") Integer maxPickTime);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.avgTravelDistanceFt > :maxDistance ORDER BY wl.avgTravelDistanceFt DESC")
    List<WarehouseLayout> findLayoutsWithLongTravelDistances(@Param("maxDistance") Double maxDistance);

    // Custom Queries for Business Intelligence
    @Query("SELECT wl.layoutType, COUNT(wl), AVG(wl.optimizationScore) FROM WarehouseLayout wl " +
           "WHERE wl.isActive = true GROUP BY wl.layoutType")
    List<Object[]> findLayoutTypePerformanceStats();
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.isActive = true ORDER BY " +
           "(wl.optimizationScore * 0.3 + wl.spaceUtilizationPct * 0.3 + wl.pickingEfficiencyScore * 0.4) DESC")
    List<WarehouseLayout> findTopPerformingLayouts(Pageable pageable);
    
    @Query("SELECT wl FROM WarehouseLayout wl WHERE wl.version > 1 ORDER BY wl.version DESC, wl.lastModifiedAt DESC")
    List<WarehouseLayout> findLayoutsWithMultipleVersions();
}

