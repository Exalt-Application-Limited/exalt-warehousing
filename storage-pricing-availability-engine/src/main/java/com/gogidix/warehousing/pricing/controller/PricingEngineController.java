package com.gogidix.warehousing.pricing.controller;

import com.gogidix.warehousing.pricing.dto.*;
import com.gogidix.warehousing.pricing.service.PricingEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Pricing Engine Controller
 * 
 * REST API endpoints for dynamic pricing and availability management.
 */
@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Pricing Engine", description = "Dynamic pricing and availability management APIs")
public class PricingEngineController {

    private final PricingEngineService pricingEngineService;

    @Operation(summary = "Calculate dynamic price", 
               description = "Calculate optimal price based on demand, occupancy, and market conditions")
    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @Valid @RequestBody PricingRequest request) {
        
        log.info("Calculating price for facility: {}, unit: {}, duration: {}", 
                request.getFacilityId(), request.getUnitType(), request.getDuration());
        
        BigDecimal calculatedPrice = pricingEngineService.calculateDynamicPrice(
            request.getFacilityId(), 
            request.getUnitType(), 
            request.getDuration(), 
            request.getContext()
        );
        
        PriceCalculationResponse response = PriceCalculationResponse.builder()
            .facilityId(request.getFacilityId())
            .unitType(request.getUnitType())
            .duration(request.getDuration())
            .calculatedPrice(calculatedPrice)
            .totalPrice(calculatedPrice.multiply(BigDecimal.valueOf(request.getDuration())))
            .calculatedAt(LocalDateTime.now())
            .currency("USD")
            .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get real-time availability", 
               description = "Get current availability for specified facility and unit type")
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @Parameter(description = "Facility ID")
            @RequestParam Long facilityId,
            @Parameter(description = "Unit type filter")
            @RequestParam(required = false) String unitType) {
        
        log.info("Getting availability for facility: {}", facilityId);
        
        Map<String, Object> availabilityData = pricingEngineService.getRealTimeAvailability(facilityId);
        AvailabilityResponse response = AvailabilityResponse.builder()
            .facilityId(facilityId)
            .totalUnits((Integer) availabilityData.getOrDefault("totalUnits", 0))
            .availableUnits((Integer) availabilityData.getOrDefault("availableUnits", 0))
            .occupancyRate((BigDecimal) availabilityData.getOrDefault("occupancyRate", BigDecimal.ZERO))
            .lastUpdated(LocalDateTime.now())
            .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update availability", 
               description = "Update unit availability after booking or release")
    @PostMapping("/availability/update")
    public ResponseEntity<Void> updateAvailability(
            @RequestBody AvailabilityUpdateRequest request) {
        
        log.info("Updating availability for facility: {}, units: {}", 
                request.getFacilityId(), request.getUnitChanges().size());
        
        // Implementation would update availability and trigger pricing recalculation
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get pricing recommendations", 
               description = "Get AI-powered pricing recommendations for facility management")
    @GetMapping("/recommendations/{facilityId}")
    public ResponseEntity<List<PricingRecommendation>> getPricingRecommendations(
            @PathVariable Long facilityId) {
        
        log.info("Getting pricing recommendations for facility: {}", facilityId);
        
        // Implementation would generate ML-based recommendations
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get demand forecast", 
               description = "Get demand forecasting data for specified period")
    @GetMapping("/forecast")
    public ResponseEntity<DemandForecast> getDemandForecast(
            @Parameter(description = "Facility ID")
            @RequestParam Long facilityId,
            @Parameter(description = "Forecast period in days")
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Getting demand forecast for facility: {}, days: {}", facilityId, days);
        
        // Implementation would generate demand forecast
        return ResponseEntity.ok().build();
    }
}