package com.exalt.warehousing.operations.service.impl;

import com.exalt.warehousing.operations.entity.StaffAssignment;
import com.exalt.warehousing.operations.enums.AssignmentStatus;
import com.exalt.warehousing.operations.enums.AssignmentType;
import com.exalt.warehousing.operations.repository.StaffAssignmentRepository;
import com.exalt.warehousing.operations.service.StaffAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffAssignmentServiceImpl implements StaffAssignmentService {

    private final StaffAssignmentRepository staffAssignmentRepository;

    @Override
    public StaffAssignment createAssignment(StaffAssignment assignment) {
        log.info("Creating staff assignment for staff: {} to zone: {}", 
                assignment.getStaffMemberId(), assignment.getZoneId());
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        return staffAssignmentRepository.save(assignment);
    }

    @Override
    public Optional<StaffAssignment> findById(Long id) {
        return staffAssignmentRepository.findById(id);
    }

    @Override
    public List<StaffAssignment> findByStaffId(Long staffId) {
        return staffAssignmentRepository.findByStaffMemberId(staffId);
    }

    @Override
    public List<StaffAssignment> findByZoneId(Long zoneId) {
        return staffAssignmentRepository.findByWarehouseZoneId(zoneId);
    }

    @Override
    public List<StaffAssignment> findByStatus(AssignmentStatus status) {
        return staffAssignmentRepository.findByStatus(status);
    }

    @Override
    public List<StaffAssignment> findByType(AssignmentType type) {
        return staffAssignmentRepository.findByAssignmentType(type);
    }

    @Override
    public StaffAssignment updateAssignment(Long id, StaffAssignment updatedAssignment) {
        return staffAssignmentRepository.findById(id)
                .map(assignment -> {
                    assignment.setZoneId(updatedAssignment.getZoneId());
                    assignment.setAssignmentStartTime(updatedAssignment.getAssignmentStartTime());
                    assignment.setAssignmentEndTime(updatedAssignment.getAssignmentEndTime());
                    assignment.setAssignmentType(updatedAssignment.getAssignmentType());
                    assignment.setUpdatedAt(LocalDateTime.now());
                    return staffAssignmentRepository.save(assignment);
                })
                .orElseThrow(() -> new RuntimeException("Staff assignment not found with id: " + id));
    }

    @Override
    public void deleteAssignment(Long id) {
        staffAssignmentRepository.deleteById(id);
    }

    @Override
    public StaffAssignment activateAssignment(Long id) {
        return staffAssignmentRepository.findById(id)
                .map(assignment -> {
                    assignment.setStatus(AssignmentStatus.IN_PROGRESS);
                    assignment.setActualStartTime(LocalDateTime.now());
                    assignment.setUpdatedAt(LocalDateTime.now());
                    return staffAssignmentRepository.save(assignment);
                })
                .orElseThrow(() -> new RuntimeException("Staff assignment not found with id: " + id));
    }

    @Override
    public StaffAssignment completeAssignment(Long id) {
        return staffAssignmentRepository.findById(id)
                .map(assignment -> {
                    assignment.setStatus(AssignmentStatus.COMPLETED);
                    assignment.setActualEndTime(LocalDateTime.now());
                    assignment.setUpdatedAt(LocalDateTime.now());
                    return staffAssignmentRepository.save(assignment);
                })
                .orElseThrow(() -> new RuntimeException("Staff assignment not found with id: " + id));
    }

    @Override
    public Long countActiveAssignmentsByZone(Long zoneId) {
        return staffAssignmentRepository.countActiveAssignmentsByZone(zoneId);
    }
}

