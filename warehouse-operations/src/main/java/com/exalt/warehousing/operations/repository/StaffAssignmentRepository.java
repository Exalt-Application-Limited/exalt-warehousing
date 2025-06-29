package com.exalt.warehousing.operations.repository;

import com.exalt.warehousing.operations.entity.StaffAssignment;
import com.exalt.warehousing.operations.enums.AssignmentStatus;
import com.exalt.warehousing.operations.enums.AssignmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for StaffAssignment entity operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Repository
public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {

    List<StaffAssignment> findByStaffMemberId(Long staffMemberId);
    
    List<StaffAssignment> findByWarehouseZoneId(Long zoneId);
    
    List<StaffAssignment> findByAssignmentType(AssignmentType assignmentType);
    
    List<StaffAssignment> findByStatus(AssignmentStatus status);
    
    @Query("SELECT sa FROM StaffAssignment sa WHERE sa.staffMemberId = :staffId AND sa.status = :status")
    List<StaffAssignment> findByStaffAndStatus(@Param("staffId") Long staffId, @Param("status") AssignmentStatus status);
    
    @Query("SELECT sa FROM StaffAssignment sa WHERE sa.startTime <= :endTime AND sa.endTime >= :startTime")
    List<StaffAssignment> findOverlappingAssignments(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(sa) FROM StaffAssignment sa WHERE sa.warehouseZone.id = :zoneId AND sa.status = 'ACTIVE'")
    Long countActiveAssignmentsByZone(@Param("zoneId") Long zoneId);
}

