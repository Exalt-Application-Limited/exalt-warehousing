package com.exalt.warehousing.logistics.controller;

import com.exalt.warehousing.logistics.service.ShippingRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for shipping route operations
 */
@RestController
@RequestMapping("/shipping-routes")
@Slf4j
@Tag(name = "Shipping Routes", description = "API for shipping route optimization and cost calculation")
public class ShippingRouteController {

    private final ShippingRouteService shippingRouteService;

    @Autowired
    public ShippingRouteController(ShippingRouteService shippingRouteService) {
        this.shippingRouteService = shippingRouteService;
    }

    @GetMapping("/optimize")
    @Operation(summary = "Calculate optimized route between warehouses")
    public ResponseEntity<Map<String, Object>> calculateOptimizedRoute(
            @Parameter(description = "Source warehouse ID") @RequestParam UUID sourceWarehouseId,
            @Parameter(description = "Destination warehouse ID") @RequestParam UUID destinationWarehouseId) {
        
        log.info("REST request to calculate optimized route from {} to {}", 
                sourceWarehouseId, destinationWarehouseId);
        
        ShippingRouteService.RouteInfo routeInfo = 
                shippingRouteService.calculateOptimizedRoute(sourceWarehouseId, destinationWarehouseId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sourceWarehouseId", sourceWarehouseId);
        response.put("destinationWarehouseId", destinationWarehouseId);
        response.put("distanceKm", routeInfo.getDistanceKm());
        response.put("estimatedTimeHours", routeInfo.getEstimatedTimeHours());
        response.put("baseCost", routeInfo.getBaseCost());
        response.put("recommendedCarrier", routeInfo.getRecommendedCarrier());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delivery-time")
    @Operation(summary = "Estimate delivery time between warehouses")
    public ResponseEntity<Map<String, Object>> estimateDeliveryTime(
            @Parameter(description = "Source warehouse ID") @RequestParam UUID sourceWarehouseId,
            @Parameter(description = "Destination warehouse ID") @RequestParam UUID destinationWarehouseId,
            @Parameter(description = "Shipping method (standard, express, economy)") 
                @RequestParam(defaultValue = "standard") String shippingMethod) {
        
        log.info("REST request to estimate delivery time from {} to {} via {}", 
                sourceWarehouseId, destinationWarehouseId, shippingMethod);
        
        Integer estimatedHours = shippingRouteService.estimateDeliveryTime(
                sourceWarehouseId, destinationWarehouseId, shippingMethod);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sourceWarehouseId", sourceWarehouseId);
        response.put("destinationWarehouseId", destinationWarehouseId);
        response.put("shippingMethod", shippingMethod);
        response.put("estimatedTimeHours", estimatedHours);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shipping-cost")
    @Operation(summary = "Calculate shipping cost")
    public ResponseEntity<Map<String, Object>> calculateShippingCost(
            @Parameter(description = "Source warehouse ID") @RequestParam UUID sourceWarehouseId,
            @Parameter(description = "Destination warehouse ID") @RequestParam UUID destinationWarehouseId,
            @Parameter(description = "Total weight in kilograms") @RequestParam Double totalWeightKg,
            @Parameter(description = "Shipping method (standard, express, economy)") 
                @RequestParam(defaultValue = "standard") String shippingMethod) {
        
        log.info("REST request to calculate shipping cost from {} to {} for {} kg via {}", 
                sourceWarehouseId, destinationWarehouseId, totalWeightKg, shippingMethod);
        
        Double cost = shippingRouteService.calculateShippingCost(
                sourceWarehouseId, destinationWarehouseId, totalWeightKg, shippingMethod);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sourceWarehouseId", sourceWarehouseId);
        response.put("destinationWarehouseId", destinationWarehouseId);
        response.put("totalWeightKg", totalWeightKg);
        response.put("shippingMethod", shippingMethod);
        response.put("shippingCost", cost);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carriers")
    @Operation(summary = "Get available carriers for a route")
    public ResponseEntity<Map<String, Object>> getAvailableCarriers(
            @Parameter(description = "Source warehouse ID") @RequestParam UUID sourceWarehouseId,
            @Parameter(description = "Destination warehouse ID") @RequestParam UUID destinationWarehouseId) {
        
        log.info("REST request to get available carriers from {} to {}", 
                sourceWarehouseId, destinationWarehouseId);
        
        Map<String, String> carriers = shippingRouteService.getAvailableCarriers(
                sourceWarehouseId, destinationWarehouseId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sourceWarehouseId", sourceWarehouseId);
        response.put("destinationWarehouseId", destinationWarehouseId);
        response.put("carriers", carriers);
        
        return ResponseEntity.ok(response);
    }
} 
