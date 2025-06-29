package com.exalt.warehousing.logistics.service.impl;

import com.exalt.warehousing.logistics.model.TransferRequest;
import com.exalt.warehousing.logistics.model.TransferStatus;
import com.exalt.warehousing.logistics.repository.TransferRequestRepository;
import com.exalt.warehousing.logistics.service.TransferAnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of TransferAnalyticsService for analytics and metrics
 */
@Service
@Slf4j
public class TransferAnalyticsServiceImpl implements TransferAnalyticsService {

    private final TransferRequestRepository transferRequestRepository;

    @Autowired
    public TransferAnalyticsServiceImpl(TransferRequestRepository transferRequestRepository) {
        this.transferRequestRepository = transferRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transferVolumeBySourceCache", key = "#startDate.toString() + #endDate.toString()")
    public Map<UUID, Integer> getTransferVolumeBySourceWarehouse(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting transfer volume by source warehouse from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> transfers = transferRequestRepository.findByCreatedAtBetween(start, end);
        
        return transfers.stream()
                .collect(Collectors.groupingBy(TransferRequest::getSourceWarehouseId, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transferVolumeByDestCache", key = "#startDate.toString() + #endDate.toString()")
    public Map<UUID, Integer> getTransferVolumeByDestinationWarehouse(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting transfer volume by destination warehouse from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> transfers = transferRequestRepository.findByCreatedAtBetween(start, end);
        
        return transfers.stream()
                .collect(Collectors.groupingBy(TransferRequest::getDestinationWarehouseId, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transferVolumeByStatusCache", key = "#startDate.toString() + #endDate.toString()")
    public Map<String, Integer> getTransferVolumeByStatus(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting transfer volume by status from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> transfers = transferRequestRepository.findByCreatedAtBetween(start, end);
        
        return transfers.stream()
                .collect(Collectors.groupingBy(tr -> tr.getStatus().name(), Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "avgCompletionTimeCache", key = "#startDate.toString() + #endDate.toString()")
    public Double getAverageTransferCompletionTime(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting average transfer completion time from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> completedTransfers = transferRequestRepository.findByStatusAndCompletedAtBetween(
                TransferStatus.COMPLETED, start, end);
        
        if (completedTransfers.isEmpty()) {
            return 0.0;
        }
        
        // Calculate average duration in hours
        double totalHours = completedTransfers.stream()
                .mapToDouble(tr -> {
                    Duration duration = Duration.between(tr.getCreatedAt(), tr.getCompletedAt());
                    return duration.toHours() + (duration.toMinutesPart() / 60.0);
                })
                .sum();
        
        return totalHours / completedTransfers.size();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "avgCompletionTimeByWarehouseCache", 
              key = "#warehouseId.toString() + #startDate.toString() + #endDate.toString()")
    public Double getAverageTransferCompletionTimeByWarehouse(UUID warehouseId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting average transfer completion time for warehouse {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> completedTransfers = transferRequestRepository.findByStatusAndSourceWarehouseIdAndCompletedAtBetween(
                TransferStatus.COMPLETED, warehouseId, start, end);
        
        if (completedTransfers.isEmpty()) {
            return 0.0;
        }
        
        // Calculate average duration in hours
        double totalHours = completedTransfers.stream()
                .mapToDouble(tr -> {
                    Duration duration = Duration.between(tr.getCreatedAt(), tr.getCompletedAt());
                    return duration.toHours() + (duration.toMinutesPart() / 60.0);
                })
                .sum();
        
        return totalHours / completedTransfers.size();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "topRoutesCache", 
              key = "#startDate.toString() + #endDate.toString() + #limit")
    public List<RouteMetric> getTopRoutesByVolume(LocalDate startDate, LocalDate endDate, int limit) {
        log.debug("Getting top {} routes by volume from {} to {}", limit, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> transfers = transferRequestRepository.findByCreatedAtBetween(start, end);
        
        // Group by source and destination warehouses
        Map<String, List<TransferRequest>> routeGroups = transfers.stream()
                .collect(Collectors.groupingBy(tr -> 
                        tr.getSourceWarehouseId().toString() + "-" + tr.getDestinationWarehouseId().toString()));
        
        // Convert to route metrics
        List<RouteMetric> routeMetrics = new ArrayList<>();
        for (Map.Entry<String, List<TransferRequest>> entry : routeGroups.entrySet()) {
            String[] warehouses = entry.getKey().split("-");
            UUID sourceId = UUID.fromString(warehouses[0]);
            UUID destId = UUID.fromString(warehouses[1]);
            int count = entry.getValue().size();
            
            // Calculate average completion time for completed transfers
            double avgTime = entry.getValue().stream()
                    .filter(tr -> tr.getStatus() == TransferStatus.COMPLETED && tr.getCompletedAt() != null)
                    .mapToDouble(tr -> {
                        Duration duration = Duration.between(tr.getCreatedAt(), tr.getCompletedAt());
                        return duration.toHours() + (duration.toMinutesPart() / 60.0);
                    })
                    .average()
                    .orElse(0.0);
            
            routeMetrics.add(new RouteMetric(sourceId, destId, count, avgTime));
        }
        
        // Sort by count descending and limit
        return routeMetrics.stream()
                .sorted(Comparator.comparing(RouteMetric::getCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dailyTransferCountsCache", 
              key = "#startDate.toString() + #endDate.toString()")
    public Map<LocalDate, Integer> getDailyTransferCounts(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting daily transfer counts from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<TransferRequest> transfers = transferRequestRepository.findByCreatedAtBetween(start, end);
        
        // Group by date
        Map<LocalDate, Integer> dailyCounts = transfers.stream()
                .collect(Collectors.groupingBy(tr -> tr.getCreatedAt().toLocalDate(), Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
        
        // Fill in missing dates with zero counts
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            result.put(date, dailyCounts.getOrDefault(date, 0));
        }
        
        return result;
    }
} 
