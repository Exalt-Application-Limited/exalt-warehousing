package com.gogidix.warehousing.pricing.service;

import com.gogidix.warehousing.pricing.dto.PricingContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for pricing engine operations.
 */
public interface PricingEngineService {

    /**
     * Calculate dynamic price for a storage unit
     */
    BigDecimal calculateDynamicPrice(Long facilityId, String unitType, Integer duration, PricingContext context);

    /**
     * Get real-time availability for facility
     */
    Map<String, Object> getRealTimeAvailability(Long facilityId);

    /**
     * Forecast demand for specified period
     */
    Map<String, Object> forecastDemand(Long facilityId, LocalDate startDate, LocalDate endDate);

    /**
     * Update availability snapshot
     */
    void updateAvailabilitySnapshot(Long facilityId, Map<String, Integer> availabilityData);

    /**
     * Apply seasonal pricing adjustments
     */
    void applySeasonalPricingAdjustments();

    /**
     * Get competitor pricing analysis
     */
    Map<String, Object> getCompetitorAnalysis(Double latitude, Double longitude, String unitType);

    /**
     * Calculate recommended price
     */
    BigDecimal calculateRecommendedPrice(Long facilityId, String unitType, Map<String, Object> marketConditions);

    /**
     * Get pricing history
     */
    List<Map<String, Object>> getPricingHistory(Long facilityId, String unitType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Optimize pricing strategy
     */
    Map<String, Object> optimizePricingStrategy(Long facilityId, Map<String, Object> constraints);
}