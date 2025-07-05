package com.gogidix.warehousing.logistics.service.impl;

import com.gogidix.warehousing.logistics.client.WarehouseClient;
import com.gogidix.warehousing.logistics.service.ShippingRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of ShippingRouteService for route optimization
 */
@Service
@Slf4j
public class ShippingRouteServiceImpl implements ShippingRouteService {

    private final WarehouseClient warehouseClient;
    
    // Cache for warehouse location data to minimize API calls
    private final Map<UUID, Map<String, Object>> warehouseCache = new ConcurrentHashMap<>();
    
    // Cache for route information to minimize calculations
    private final Map<String, RouteInfo> routeCache = new ConcurrentHashMap<>();
    
    @Autowired
    public ShippingRouteServiceImpl(WarehouseClient warehouseClient) {
        this.warehouseClient = warehouseClient;
    }
    
    @Override
    public RouteInfo calculateOptimizedRoute(UUID sourceWarehouseId, UUID destinationWarehouseId) {
        String cacheKey = sourceWarehouseId.toString() + "-" + destinationWarehouseId.toString();
        
        // Check cache first
        if (routeCache.containsKey(cacheKey)) {
            log.debug("Route cache hit for {}", cacheKey);
            return routeCache.get(cacheKey);
        }
        
        log.debug("Calculating optimized route from {} to {}", sourceWarehouseId, destinationWarehouseId);
        
        try {
            // Get warehouse locations
            Map<String, Object> sourceWarehouse = getWarehouseDetails(sourceWarehouseId);
            Map<String, Object> destWarehouse = getWarehouseDetails(destinationWarehouseId);
            
            // Extract location coordinates
            double sourceLat = Double.parseDouble(sourceWarehouse.get("latitude").toString());
            double sourceLng = Double.parseDouble(sourceWarehouse.get("longitude").toString());
            double destLat = Double.parseDouble(destWarehouse.get("latitude").toString());
            double destLng = Double.parseDouble(destWarehouse.get("longitude").toString());
            
            // Calculate distance using Haversine formula
            double distanceKm = calculateHaversineDistance(sourceLat, sourceLng, destLat, destLng);
            
            // Estimate time based on distance (avg speed of 60 km/h)
            int estimatedTimeHours = (int) Math.ceil(distanceKm / 60.0);
            
            // Calculate base cost ($0.5 per km)
            double baseCost = distanceKm * 0.5;
            
            // Determine recommended carrier based on distance
            String recommendedCarrier = determineRecommendedCarrier(distanceKm);
            
            RouteInfo routeInfo = new RouteInfo(distanceKm, estimatedTimeHours, baseCost, recommendedCarrier);
            
            // Cache the result
            routeCache.put(cacheKey, routeInfo);
            
            return routeInfo;
        } catch (Exception e) {
            log.error("Error calculating optimized route: {}", e.getMessage(), e);
            // Return a default route info with high cost to indicate error
            return new RouteInfo(Double.MAX_VALUE, Integer.MAX_VALUE, Double.MAX_VALUE, "ERROR");
        }
    }
    
    @Override
    public Integer estimateDeliveryTime(UUID sourceWarehouseId, UUID destinationWarehouseId, String shippingMethod) {
        RouteInfo routeInfo = calculateOptimizedRoute(sourceWarehouseId, destinationWarehouseId);
        
        // Apply multiplier based on shipping method
        switch (shippingMethod.toLowerCase()) {
            case "express":
                return (int) Math.ceil(routeInfo.getEstimatedTimeHours() * 0.6); // 40% faster
            case "economy":
                return (int) Math.ceil(routeInfo.getEstimatedTimeHours() * 1.5); // 50% slower
            case "standard":
            default:
                return routeInfo.getEstimatedTimeHours();
        }
    }
    
    @Override
    public Double calculateShippingCost(UUID sourceWarehouseId, UUID destinationWarehouseId, 
                                      Double totalWeightKg, String shippingMethod) {
        RouteInfo routeInfo = calculateOptimizedRoute(sourceWarehouseId, destinationWarehouseId);
        
        // Base cost from route + weight cost
        double baseCost = routeInfo.getBaseCost();
        double weightCost = totalWeightKg * 0.75; // $0.75 per kg
        
        // Apply multiplier based on shipping method
        switch (shippingMethod.toLowerCase()) {
            case "express":
                return (baseCost + weightCost) * 1.8; // 80% premium
            case "economy":
                return (baseCost + weightCost) * 0.8; // 20% discount
            case "standard":
            default:
                return baseCost + weightCost;
        }
    }
    
    @Override
    public Map<String, String> getAvailableCarriers(UUID sourceWarehouseId, UUID destinationWarehouseId) {
        RouteInfo routeInfo = calculateOptimizedRoute(sourceWarehouseId, destinationWarehouseId);
        
        // In a real implementation, this would query a carrier service
        // For this implementation, we'll return a hard-coded list based on distance
        Map<String, String> carriers = new HashMap<>();
        
        // All routes have these carriers
        carriers.put("FEDEX", "FedEx");
        carriers.put("UPS", "UPS");
        
        // Add carriers based on distance
        double distance = routeInfo.getDistanceKm();
        
        if (distance < 500) {
            carriers.put("LOCAL", "Local Courier");
        }
        
        if (distance > 200) {
            carriers.put("DHL", "DHL");
        }
        
        if (distance > 1000) {
            carriers.put("MAERSK", "Maersk (Sea Freight)");
        }
        
        return carriers;
    }
    
    /**
     * Get warehouse details from client or cache
     *
     * @param warehouseId warehouse ID
     * @return warehouse details
     */
    private Map<String, Object> getWarehouseDetails(UUID warehouseId) {
        // Check cache first
        if (warehouseCache.containsKey(warehouseId)) {
            return warehouseCache.get(warehouseId);
        }
        
        // Fetch from client
        Map<String, Object> warehouse = warehouseClient.getWarehouse(warehouseId);
        
        // Cache for future use
        warehouseCache.put(warehouseId, warehouse);
        
        return warehouse;
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     *
     * @param lat1 latitude of first point
     * @param lng1 longitude of first point
     * @param lat2 latitude of second point
     * @param lng2 longitude of second point
     * @return distance in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        // Earth radius in kilometers
        final double R = 6371.0;
        
        // Convert degrees to radians
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Determine recommended carrier based on distance
     *
     * @param distanceKm distance in kilometers
     * @return recommended carrier ID
     */
    private String determineRecommendedCarrier(double distanceKm) {
        if (distanceKm < 200) {
            return "LOCAL";
        } else if (distanceKm < 500) {
            return "FEDEX";
        } else if (distanceKm < 1000) {
            return "UPS";
        } else {
            return "DHL";
        }
    }
} 
