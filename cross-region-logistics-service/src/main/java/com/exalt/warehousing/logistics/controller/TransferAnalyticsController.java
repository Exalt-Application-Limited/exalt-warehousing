package com.exalt.warehousing.logistics.controller;

import com.exalt.warehousing.logistics.service.TransferAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for transfer analytics operations
 */
@RestController
@RequestMapping("/transfers/analytics")
@Slf4j
@Tag(name = "Transfer Analytics", description = "API for transfer analytics and metrics")
public class TransferAnalyticsController {

    private final TransferAnalyticsService analyticsService;

    @Autowired
    public TransferAnalyticsController(TransferAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/volume/source")
    @Operation(summary = "Get transfer volume metrics by source warehouse")
    public ResponseEntity<Map<String, Object>> getTransferVolumeBySourceWarehouse(
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get transfer volume by source warehouse from {} to {}", startDate, endDate);
        
        Map<UUID, Integer> volumes = analyticsService.getTransferVolumeBySourceWarehouse(startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("volumes", volumes);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/volume/destination")
    @Operation(summary = "Get transfer volume metrics by destination warehouse")
    public ResponseEntity<Map<String, Object>> getTransferVolumeByDestinationWarehouse(
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get transfer volume by destination warehouse from {} to {}", startDate, endDate);
        
        Map<UUID, Integer> volumes = analyticsService.getTransferVolumeByDestinationWarehouse(startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("volumes", volumes);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/volume/status")
    @Operation(summary = "Get transfer volume metrics by status")
    public ResponseEntity<Map<String, Object>> getTransferVolumeByStatus(
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get transfer volume by status from {} to {}", startDate, endDate);
        
        Map<String, Integer> volumes = analyticsService.getTransferVolumeByStatus(startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("volumes", volumes);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completion-time")
    @Operation(summary = "Get average transfer completion time")
    public ResponseEntity<Map<String, Object>> getAverageTransferCompletionTime(
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get average transfer completion time from {} to {}", startDate, endDate);
        
        Double avgTime = analyticsService.getAverageTransferCompletionTime(startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("averageCompletionTimeHours", avgTime);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completion-time/warehouse/{warehouseId}")
    @Operation(summary = "Get average transfer completion time by warehouse")
    public ResponseEntity<Map<String, Object>> getAverageTransferCompletionTimeByWarehouse(
            @Parameter(description = "Warehouse ID") @PathVariable UUID warehouseId,
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get average transfer completion time for warehouse {} from {} to {}", 
                warehouseId, startDate, endDate);
        
        Double avgTime = analyticsService.getAverageTransferCompletionTimeByWarehouse(warehouseId, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("warehouseId", warehouseId);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("averageCompletionTimeHours", avgTime);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-routes")
    @Operation(summary = "Get top routes by volume")
    public ResponseEntity<Map<String, Object>> getTopRoutesByVolume(
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Maximum number of routes to return") 
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("REST request to get top {} routes by volume from {} to {}", limit, startDate, endDate);
        
        List<TransferAnalyticsService.RouteMetric> routeMetrics = 
                analyticsService.getTopRoutesByVolume(startDate, endDate, limit);
        
        List<Map<String, Object>> routesList = routeMetrics.stream()
                .map(rm -> {
                    Map<String, Object> route = new HashMap<>();
                    route.put("sourceWarehouseId", rm.getSourceWarehouseId());
                    route.put("destinationWarehouseId", rm.getDestinationWarehouseId());
                    route.put("count", rm.getCount());
                    route.put("averageCompletionTimeHours", rm.getAverageCompletionTimeHours());
                    return route;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("limit", limit);
        response.put("routes", routesList);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily-counts")
    @Operation(summary = "Get daily transfer counts")
    public ResponseEntity<Map<String, Object>> getDailyTransferCounts(
            @Parameter(description = "Start date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (inclusive)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("REST request to get daily transfer counts from {} to {}", startDate, endDate);
        
        Map<LocalDate, Integer> dailyCounts = analyticsService.getDailyTransferCounts(startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("dailyCounts", dailyCounts);
        
        return ResponseEntity.ok(response);
    }
} 
