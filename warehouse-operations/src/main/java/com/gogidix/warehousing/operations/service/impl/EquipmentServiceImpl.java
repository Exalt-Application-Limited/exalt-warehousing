package com.gogidix.warehousing.operations.service.impl;

import com.gogidix.warehousing.operations.entity.Equipment;
import com.gogidix.warehousing.operations.enums.EquipmentStatus;
import com.gogidix.warehousing.operations.enums.EquipmentType;
import com.gogidix.warehousing.operations.repository.EquipmentRepository;
import com.gogidix.warehousing.operations.service.EquipmentService;
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
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    public Equipment createEquipment(Equipment equipment) {
        log.info("Creating new equipment: {}", equipment.getEquipmentName());
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setCreatedAt(LocalDateTime.now());
        equipment.setUpdatedAt(LocalDateTime.now());
        return equipmentRepository.save(equipment);
    }

    @Override
    public Optional<Equipment> findById(Long id) {
        return equipmentRepository.findById(id);
    }

    @Override
    public List<Equipment> findByType(EquipmentType type) {
        return equipmentRepository.findByEquipmentType(type);
    }

    @Override
    public List<Equipment> findByStatus(EquipmentStatus status) {
        return equipmentRepository.findByStatus(status);
    }

    @Override
    public List<Equipment> findByZoneId(Long zoneId) {
        return equipmentRepository.findByWarehouseZoneId(zoneId);
    }

    @Override
    public List<Equipment> findByStaffId(Long staffId) {
        return equipmentRepository.findByStatus(EquipmentStatus.IN_USE);
    }

    @Override
    public Equipment updateEquipment(Long id, Equipment updatedEquipment) {
        return equipmentRepository.findById(id)
                .map(equipment -> {
                    equipment.setEquipmentName(updatedEquipment.getEquipmentName());
                    equipment.setMaintenanceNotes(updatedEquipment.getMaintenanceNotes());
                    equipment.setEquipmentType(updatedEquipment.getEquipmentType());
                    equipment.setStatus(updatedEquipment.getStatus());
                    equipment.setUpdatedAt(LocalDateTime.now());
                    return equipmentRepository.save(equipment);
                })
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
    }

    @Override
    public Equipment assignToStaff(Long equipmentId, Long staffId) {
        return equipmentRepository.findById(equipmentId)
                .map(equipment -> {
                    equipment.setCurrentUserId(staffId);
                    equipment.setStatus(EquipmentStatus.IN_USE);
                    equipment.setUpdatedAt(LocalDateTime.now());
                    return equipmentRepository.save(equipment);
                })
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));
    }

    @Override
    public Equipment unassignFromStaff(Long equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .map(equipment -> {
                    equipment.setCurrentUserId(null);
                    equipment.setStatus(EquipmentStatus.AVAILABLE);
                    equipment.setUpdatedAt(LocalDateTime.now());
                    return equipmentRepository.save(equipment);
                })
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));
    }

    @Override
    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
    }

    @Override
    public List<Equipment> findEquipmentNeedingMaintenance() {
        return equipmentRepository.findEquipmentNeedingMaintenance(LocalDateTime.now());
    }

    @Override
    public Equipment scheduleMaintenace(Long equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .map(equipment -> {
                    equipment.setStatus(EquipmentStatus.MAINTENANCE);
                    equipment.setUpdatedAt(LocalDateTime.now());
                    return equipmentRepository.save(equipment);
                })
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));
    }
}

