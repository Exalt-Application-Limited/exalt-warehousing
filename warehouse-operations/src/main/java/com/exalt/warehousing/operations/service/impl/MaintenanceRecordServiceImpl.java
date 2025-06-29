package com.exalt.warehousing.operations.service.impl;

import com.exalt.warehousing.operations.entity.MaintenanceRecord;
import com.exalt.warehousing.operations.enums.MaintenanceFrequency;
import com.exalt.warehousing.operations.repository.MaintenanceRecordRepository;
import com.exalt.warehousing.operations.service.MaintenanceRecordService;
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
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;

    @Override
    public MaintenanceRecord createMaintenanceRecord(MaintenanceRecord record) {
        log.info("Creating maintenance record for equipment: {}", record.getEquipment().getId());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return maintenanceRecordRepository.save(record);
    }

    @Override
    public Optional<MaintenanceRecord> findById(Long id) {
        return maintenanceRecordRepository.findById(id);
    }

    @Override
    public List<MaintenanceRecord> findByEquipmentId(Long equipmentId) {
        return maintenanceRecordRepository.findByEquipmentId(equipmentId);
    }

    @Override
    public List<MaintenanceRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return maintenanceRecordRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<MaintenanceRecord> findByMaintenanceType(String type) {
        return maintenanceRecordRepository.findByMaintenanceType(type);
    }

    @Override
    public List<MaintenanceRecord> findByFrequency(MaintenanceFrequency frequency) {
        return maintenanceRecordRepository.findByFrequency(frequency);
    }

    @Override
    public List<MaintenanceRecord> findByUserId(Long userId) {
        return maintenanceRecordRepository.findByEquipmentId(userId);
    }

    @Override
    public MaintenanceRecord updateMaintenanceRecord(Long id, MaintenanceRecord updatedRecord) {
        return maintenanceRecordRepository.findById(id)
                .map(record -> {
                    record.setServicesPerformed(updatedRecord.getServicesPerformed());
                    record.setMaintenanceDate(updatedRecord.getMaintenanceDate());
                    record.setEndTime(updatedRecord.getEndTime());
                    record.setTechnicianName(updatedRecord.getTechnicianName());
                    record.setMaintenanceNotes(updatedRecord.getMaintenanceNotes());
                    record.setUpdatedAt(LocalDateTime.now());
                    return maintenanceRecordRepository.save(record);
                })
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));
    }

    @Override
    public void deleteMaintenanceRecord(Long id) {
        maintenanceRecordRepository.deleteById(id);
    }

    @Override
    public Long countMaintenanceRecordsByEquipment(Long equipmentId) {
        return maintenanceRecordRepository.countMaintenanceRecordsByEquipment(equipmentId);
    }

    @Override
    public List<MaintenanceRecord> findEmergencyMaintenanceRecords() {
        return maintenanceRecordRepository.findEmergencyMaintenanceRecords();
    }
}

