package com.exalt.warehousing.operations.service.impl;

import com.exalt.warehousing.operations.entity.TaskAssignment;
import com.exalt.warehousing.operations.enums.TaskPriority;
import com.exalt.warehousing.operations.enums.TaskStatus;
import com.exalt.warehousing.operations.enums.TaskType;
import com.exalt.warehousing.operations.repository.TaskAssignmentRepository;
import com.exalt.warehousing.operations.service.TaskAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TaskAssignmentService
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TaskAssignmentServiceImpl implements TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;

    @Override
    public TaskAssignment createTask(TaskAssignment task) {
        return taskAssignmentRepository.save(task);
    }

    @Override
    public TaskAssignment updateTask(Long id, TaskAssignment task) {
        task.setId(id);
        return taskAssignmentRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskAssignment> findById(Long id) {
        return taskAssignmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAssignment> findByStaffId(Long staffId) {
        return taskAssignmentRepository.findByAssignedStaffId(staffId);
    }
    @Override
    @Transactional(readOnly = true)
    public List<TaskAssignment> findByZoneId(Long zoneId) {
        return taskAssignmentRepository.findByWarehouseZoneId(zoneId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAssignment> findByStatus(TaskStatus status) {
        return taskAssignmentRepository.findByTaskStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAssignment> findByType(TaskType type) {
        return taskAssignmentRepository.findByTaskType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAssignment> findByPriority(TaskPriority priority) {
        return taskAssignmentRepository.findByPriority(priority);
    }

    @Override
    public void deleteTask(Long id) {
        taskAssignmentRepository.deleteById(id);
    }

    @Override
    public TaskAssignment startTask(Long id) {
        TaskAssignment task = taskAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setActualStartTime(LocalDateTime.now());
        return taskAssignmentRepository.save(task);
    }

    @Override
    public TaskAssignment completeTask(Long id) {
        TaskAssignment task = taskAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(TaskStatus.COMPLETED);
        task.setActualEndTime(LocalDateTime.now());
        return taskAssignmentRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveTasksByZone(Long zoneId) {
        return taskAssignmentRepository.countActiveTasksByZone(zoneId);
    }
}

