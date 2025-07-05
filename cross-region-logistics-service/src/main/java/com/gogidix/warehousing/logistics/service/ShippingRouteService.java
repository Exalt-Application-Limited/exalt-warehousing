package com.gogidix.warehousing.logistics.service;

import java.util.UUID;
import java.util.Map;

/**
 * Service interface for shipping route optimization
 */
public interface ShippingRouteService {
    
    /**
     * Calculate optimized route between warehouses
     *
     * @param sourceWarehouseId source warehouse ID
     * @param destinationWarehouseId destination warehouse ID
     * @return route information including distance, estimated time, and cost
     */
    RouteInfo calculateOptimizedRoute(UUID sourceWarehouseId, UUID destinationWarehouseId);
    
    /**
     * Calculate estimated delivery time between warehouses
     *
     * @param sourceWarehouseId source warehouse ID
     * @param destinationWarehouseId destination warehouse ID
     * @param shippingMethod shipping method (e.g., "standard", "express", "economy")
     * @return estimated delivery time in hours
     */
    Integer estimateDeliveryTime(UUID sourceWarehouseId, UUID destinationWarehouseId, String shippingMethod);
    
    /**
     * Calculate shipping cost between warehouses
     *
     * @param sourceWarehouseId source warehouse ID
     * @param destinationWarehouseId destination warehouse ID
     * @param totalWeightKg total weight in kilograms
     * @param shippingMethod shipping method (e.g., "standard", "express", "economy")
     * @return estimated shipping cost
     */
    Double calculateShippingCost(UUID sourceWarehouseId, UUID destinationWarehouseId, 
                               Double totalWeightKg, String shippingMethod);
    
    /**
     * Get available shipping carriers for a route
     *
     * @param sourceWarehouseId source warehouse ID
     * @param destinationWarehouseId destination warehouse ID
     * @return map of carrier IDs to carrier names
     */
    Map<String, String> getAvailableCarriers(UUID sourceWarehouseId, UUID destinationWarehouseId);
    
    /**
     * Class representing route information
     */
    class RouteInfo {
        private final Double distanceKm;
        private final Integer estimatedTimeHours;
        private final Double baseCost;
        private final String recommendedCarrier;
        
        public RouteInfo(Double distanceKm, Integer estimatedTimeHours, Double baseCost, String recommendedCarrier) {
            this.distanceKm = distanceKm;
            this.estimatedTimeHours = estimatedTimeHours;
            this.baseCost = baseCost;
            this.recommendedCarrier = recommendedCarrier;
        }
        
        public Double getDistanceKm() {
            return distanceKm;
        }
        
        public Integer getEstimatedTimeHours() {
            return estimatedTimeHours;
        }
        
        public Double getBaseCost() {
            return baseCost;
        }
        
        public String getRecommendedCarrier() {
            return recommendedCarrier;
        }
    }
} 
