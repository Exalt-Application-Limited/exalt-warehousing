package com.gogidix.warehousing.operations.repository;

import com.gogidix.warehousing.operations.entity.TaskAssignment;
import com.gogidix.warehousing.operations.enums.TaskPriority;
import com.gogidix.warehousing.operations.enums.TaskStatus;
import com.gogidix.warehousing.operations.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for TaskAssignment entity operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findByAssignedStaffId(Long staffId);
    
    List<TaskAssignment> findByWarehouseZoneId(Long zoneId);
    
    List<TaskAssignment> findByTaskType(TaskType taskType);
    
    List<TaskAssignment> findByTaskStatus(TaskStatus taskStatus);
    
    List<TaskAssignment> findByPriority(TaskPriority priority);
    
    @Query("SELECT ta FROM TaskAssignment ta WHERE ta.assignedStaffId = :staffId AND ta.taskStatus = :status")
    List<TaskAssignment> findByStaffAndStatus(@Param("staffId") Long staffId, @Param("status") TaskStatus status);
    
    @Query("SELECT ta FROM TaskAssignment ta WHERE ta.scheduledStartTime <= :endTime AND ta.scheduledEndTime >= :startTime")
    List<TaskAssignment> findOverlappingTasks(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT ta FROM TaskAssignment ta WHERE ta.priority = :priority AND ta.taskStatus IN ('PENDING', 'IN_PROGRESS') ORDER BY ta.scheduledStartTime")
    List<TaskAssignment> findActivePriorityTasks(@Param("priority") TaskPriority priority);
    
    @Query("SELECT COUNT(ta) FROM TaskAssignment ta WHERE ta.warehouseZone.id = :zoneId AND ta.taskStatus = 'IN_PROGRESS'")
    Long countActiveTasksByZone(@Param("zoneId") Long zoneId);
}

