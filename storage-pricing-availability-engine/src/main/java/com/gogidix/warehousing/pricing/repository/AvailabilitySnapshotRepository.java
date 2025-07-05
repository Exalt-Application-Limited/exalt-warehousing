package com.gogidix.warehousing.pricing.repository;

import com.gogidix.warehousing.pricing.entity.AvailabilitySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AvailabilitySnapshot entity operations.
 */
@Repository
public interface AvailabilitySnapshotRepository extends JpaRepository<AvailabilitySnapshot, Long> {

    /**
     * Find latest availability snapshot by facility ID
     */
    @Query("SELECT a FROM AvailabilitySnapshot a WHERE a.facilityId = :facilityId ORDER BY a.snapshotTimestamp DESC")
    Optional<AvailabilitySnapshot> findLatestByFacilityId(@Param("facilityId") Long facilityId);

    /**
     * Find availability snapshots by facility and unit type
     */
    @Query("SELECT a FROM AvailabilitySnapshot a WHERE a.facilityId = :facilityId AND a.unitType = :unitType " +
           "ORDER BY a.snapshotTimestamp DESC")
    List<AvailabilitySnapshot> findByFacilityIdAndUnitType(@Param("facilityId") Long facilityId, @Param("unitType") String unitType);

    /**
     * Find availability snapshots within time range
     */
    @Query("SELECT a FROM AvailabilitySnapshot a WHERE a.snapshotTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.snapshotTimestamp DESC")
    List<AvailabilitySnapshot> findByTimestampBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find facilities with low availability (below threshold)
     */
    @Query("SELECT a FROM AvailabilitySnapshot a WHERE a.availableUnits < :threshold AND " +
           "a.snapshotTimestamp >= :since ORDER BY a.availableUnits ASC")
    List<AvailabilitySnapshot> findLowAvailabilityFacilities(@Param("threshold") Integer threshold, @Param("since") LocalDateTime since);

    /**
     * Get occupancy rate for facility
     */
    @Query("SELECT (CAST(a.totalUnits - a.availableUnits AS double) / CAST(a.totalUnits AS double)) * 100 " +
           "FROM AvailabilitySnapshot a WHERE a.facilityId = :facilityId " +
           "ORDER BY a.snapshotTimestamp DESC")
    Optional<Double> getLatestOccupancyRate(@Param("facilityId") Long facilityId);

    /**
     * Delete old snapshots (older than specified date)
     */
    @Query("DELETE FROM AvailabilitySnapshot a WHERE a.snapshotTimestamp < :cutoffDate")
    void deleteOldSnapshots(@Param("cutoffDate") LocalDateTime cutoffDate);
}