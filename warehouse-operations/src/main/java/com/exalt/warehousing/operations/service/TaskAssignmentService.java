package com.exalt.warehousing.operations.service;

import com.exalt.warehousing.operations.entity.TaskAssignment;
import com.exalt.warehousing.operations.enums.TaskPriority;
import com.exalt.warehousing.operations.enums.TaskStatus;
import com.exalt.warehousing.operations.enums.TaskType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for TaskAssignment operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public interface TaskAssignmentService {

    TaskAssignment createTask(TaskAssignment task);
    
    TaskAssignment updateTask(Long id, TaskAssignment task);
    
    Optional<TaskAssignment> findById(Long id);
    
    List<TaskAssignment> findByStaffId(Long staffId);
    
    List<TaskAssignment> findByZoneId(Long zoneId);
    
    List<TaskAssignment> findByStatus(TaskStatus status);
    
    List<TaskAssignment> findByType(TaskType type);
    
    List<TaskAssignment> findByPriority(TaskPriority priority);
    
    void deleteTask(Long id);
    
    TaskAssignment startTask(Long id);
    
    TaskAssignment completeTask(Long id);
    
    Long countActiveTasksByZone(Long zoneId);
}

