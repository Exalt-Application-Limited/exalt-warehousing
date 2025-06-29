package com.exalt.warehousing.operations.service;

import com.exalt.warehousing.operations.entity.StaffAssignment;
import com.exalt.warehousing.operations.enums.AssignmentStatus;
import com.exalt.warehousing.operations.enums.AssignmentType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for StaffAssignment operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
public interface StaffAssignmentService {

    StaffAssignment createAssignment(StaffAssignment assignment);
    
    StaffAssignment updateAssignment(Long id, StaffAssignment assignment);
    
    Optional<StaffAssignment> findById(Long id);
    
    List<StaffAssignment> findByStaffId(Long staffId);
    
    List<StaffAssignment> findByZoneId(Long zoneId);
    
    List<StaffAssignment> findByStatus(AssignmentStatus status);
    
    List<StaffAssignment> findByType(AssignmentType type);
    
    void deleteAssignment(Long id);
    
    StaffAssignment activateAssignment(Long id);
    
    StaffAssignment completeAssignment(Long id);
    
    Long countActiveAssignmentsByZone(Long zoneId);
}

