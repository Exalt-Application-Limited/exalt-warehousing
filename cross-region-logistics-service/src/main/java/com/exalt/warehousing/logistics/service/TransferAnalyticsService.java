package com.exalt.warehousing.logistics.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for transfer analytics and metrics
 */
public interface TransferAnalyticsService {
    
    /**
     * Get transfer volume metrics by source warehouse
     *
     * @param startDate start date
     * @param endDate end date
     * @return map of warehouse IDs to transfer counts
     */
    Map<UUID, Integer> getTransferVolumeBySourceWarehouse(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get transfer volume metrics by destination warehouse
     *
     * @param startDate start date
     * @param endDate end date
     * @return map of warehouse IDs to transfer counts
     */
    Map<UUID, Integer> getTransferVolumeByDestinationWarehouse(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get transfer volume metrics by status
     *
     * @param startDate start date
     * @param endDate end date
     * @return map of status to transfer counts
     */
    Map<String, Integer> getTransferVolumeByStatus(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get average transfer completion time in hours
     *
     * @param startDate start date
     * @param endDate end date
     * @return average completion time in hours
     */
    Double getAverageTransferCompletionTime(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get average transfer completion time in hours by warehouse
     *
     * @param warehouseId warehouse ID
     * @param startDate start date
     * @param endDate end date
     * @return average completion time in hours
     */
    Double getAverageTransferCompletionTimeByWarehouse(UUID warehouseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get top routes by volume
     *
     * @param startDate start date
     * @param endDate end date
     * @param limit maximum number of routes to return
     * @return list of route metrics
     */
    List<RouteMetric> getTopRoutesByVolume(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Get daily transfer count metrics
     *
     * @param startDate start date
     * @param endDate end date
     * @return map of dates to transfer counts
     */
    Map<LocalDate, Integer> getDailyTransferCounts(LocalDate startDate, LocalDate endDate);
    
    /**
     * Class representing route metrics
     */
    class RouteMetric {
        private final UUID sourceWarehouseId;
        private final UUID destinationWarehouseId;
        private final Integer count;
        private final Double averageCompletionTimeHours;
        
        public RouteMetric(UUID sourceWarehouseId, UUID destinationWarehouseId, 
                          Integer count, Double averageCompletionTimeHours) {
            this.sourceWarehouseId = sourceWarehouseId;
            this.destinationWarehouseId = destinationWarehouseId;
            this.count = count;
            this.averageCompletionTimeHours = averageCompletionTimeHours;
        }
        
        public UUID getSourceWarehouseId() {
            return sourceWarehouseId;
        }
        
        public UUID getDestinationWarehouseId() {
            return destinationWarehouseId;
        }
        
        public Integer getCount() {
            return count;
        }
        
        public Double getAverageCompletionTimeHours() {
            return averageCompletionTimeHours;
        }
    }
} 
