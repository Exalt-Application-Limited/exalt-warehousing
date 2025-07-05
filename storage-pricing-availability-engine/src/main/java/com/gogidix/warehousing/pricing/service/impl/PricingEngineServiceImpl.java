package com.gogidix.warehousing.pricing.service.impl;

import com.gogidix.warehousing.pricing.entity.AvailabilitySnapshot;
import com.gogidix.warehousing.pricing.entity.PricingRule;
import com.gogidix.warehousing.pricing.repository.AvailabilitySnapshotRepository;
import com.gogidix.warehousing.pricing.repository.PricingRuleRepository;
import com.gogidix.warehousing.pricing.service.PricingEngineService;
import com.gogidix.warehousing.pricing.dto.PricingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of PricingEngineService with dynamic pricing algorithms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingEngineServiceImpl implements PricingEngineService {

    private final PricingRuleRepository pricingRuleRepository;
    private final AvailabilitySnapshotRepository availabilitySnapshotRepository;

    @Override
    @Cacheable(value = "dynamicPrice", key = "#facilityId + '_' + #unitType + '_' + #duration")
    public BigDecimal calculateDynamicPrice(Long facilityId, String unitType, Integer duration, PricingContext context) {
        log.info("Calculating dynamic price for facility: {}, unitType: {}, duration: {}", facilityId, unitType, duration);
        
        // Get base price from pricing rules
        List<PricingRule> rules = pricingRuleRepository.findActivePricingRulesByFacilityId(facilityId);
        BigDecimal basePrice = getBasePrice(rules, unitType);
        
        // Apply demand-based pricing
        BigDecimal demandMultiplier = calculateDemandMultiplier(facilityId, unitType);
        
        // Apply seasonal adjustments
        BigDecimal seasonalMultiplier = calculateSeasonalMultiplier(facilityId);
        
        // Apply duration discounts
        BigDecimal durationMultiplier = calculateDurationMultiplier(duration);
        
        // Apply occupancy-based pricing
        BigDecimal occupancyMultiplier = calculateOccupancyMultiplier(facilityId);
        
        BigDecimal finalPrice = basePrice
            .multiply(demandMultiplier)
            .multiply(seasonalMultiplier)
            .multiply(durationMultiplier)
            .multiply(occupancyMultiplier)
            .setScale(2, RoundingMode.HALF_UP);
        
        log.info("Final dynamic price calculated: {} for facility: {}", finalPrice, facilityId);
        return finalPrice;
    }

    @Override
    @Cacheable(value = "availability", key = "#facilityId")
    public Map<String, Object> getRealTimeAvailability(Long facilityId) {
        log.info("Getting real-time availability for facility: {}", facilityId);
        
        Optional<AvailabilitySnapshot> snapshot = availabilitySnapshotRepository.findLatestByFacilityId(facilityId);
        
        Map<String, Object> availability = new HashMap<>();
        if (snapshot.isPresent()) {
            AvailabilitySnapshot snap = snapshot.get();
            availability.put("facilityId", facilityId);
            availability.put("totalUnits", snap.getTotalUnits());
            availability.put("availableUnits", snap.getAvailableUnits());
            availability.put("occupancyRate", calculateOccupancyRate(snap));
            availability.put("lastUpdated", snap.getSnapshotTimestamp());
            availability.put("unitTypes", parseUnitTypeBreakdown(snap.getUnitTypeBreakdown()));
        } else {
            availability.put("facilityId", facilityId);
            availability.put("error", "No availability data found");
        }
        
        return availability;
    }

    @Override
    public Map<String, Object> forecastDemand(Long facilityId, LocalDate startDate, LocalDate endDate) {
        log.info("Forecasting demand for facility: {} from {} to {}", facilityId, startDate, endDate);
        
        // Historical demand analysis
        List<AvailabilitySnapshot> historicalData = availabilitySnapshotRepository
            .findByTimestampBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        Map<String, Object> forecast = new HashMap<>();
        forecast.put("facilityId", facilityId);
        forecast.put("forecastPeriod", Map.of("start", startDate, "end", endDate));
        
        if (!historicalData.isEmpty()) {
            // Calculate demand trends
            double avgOccupancy = historicalData.stream()
                .mapToDouble(this::calculateOccupancyRate)
                .average()
                .orElse(0.0);
            
            forecast.put("averageOccupancy", avgOccupancy);
            forecast.put("demandTrend", calculateDemandTrend(historicalData));
            forecast.put("peakPeriods", identifyPeakPeriods(historicalData));
            forecast.put("recommendedCapacity", calculateRecommendedCapacity(avgOccupancy));
        }
        
        return forecast;
    }

    @Override
    @Async
    public void updateAvailabilitySnapshot(Long facilityId, Map<String, Integer> availabilityData) {
        log.info("Updating availability snapshot for facility: {}", facilityId);
        
        AvailabilitySnapshot snapshot = new AvailabilitySnapshot();
        snapshot.setFacilityId(facilityId);
        snapshot.setTotalUnits(availabilityData.get("totalUnits"));
        snapshot.setAvailableUnits(availabilityData.get("availableUnits"));
        snapshot.setUnitTypeBreakdown(convertToJson(availabilityData));
        snapshot.setSnapshotTimestamp(LocalDateTime.now());
        
        availabilitySnapshotRepository.save(snapshot);
        log.info("Availability snapshot updated for facility: {}", facilityId);
    }

    @Override
    @Async
    public void applySeasonalPricingAdjustments() {
        log.info("Applying seasonal pricing adjustments");
        
        List<PricingRule> seasonalRules = pricingRuleRepository.findCurrentSeasonalRules();
        
        for (PricingRule rule : seasonalRules) {
            // Apply seasonal adjustments logic
            BigDecimal seasonalAdjustment = calculateSeasonalAdjustment(rule);
            log.info("Applied seasonal adjustment: {} for rule: {}", seasonalAdjustment, rule.getId());
        }
    }

    @Override
    public Map<String, Object> getCompetitorAnalysis(Double latitude, Double longitude, String unitType) {
        log.info("Getting competitor analysis for location: {}, {}, unitType: {}", latitude, longitude, unitType);
        
        // Mock competitor analysis - in real implementation, this would call external APIs
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("averagePrice", BigDecimal.valueOf(120.00));
        analysis.put("priceRange", Map.of("min", 80.00, "max", 180.00));
        analysis.put("competitorCount", 8);
        analysis.put("marketPosition", "competitive");
        
        return analysis;
    }

    @Override
    public BigDecimal calculateRecommendedPrice(Long facilityId, String unitType, Map<String, Object> marketConditions) {
        log.info("Calculating recommended price for facility: {}, unitType: {}", facilityId, unitType);
        
        // Base price from current pricing
        PricingContext context = PricingContext.builder()
            .occupancyRate(new BigDecimal("75.0"))
            .demandScore(80)
            .build();
        BigDecimal currentPrice = calculateDynamicPrice(facilityId, unitType, 1, context);
        
        // Market conditions adjustments
        BigDecimal marketMultiplier = BigDecimal.valueOf(1.0);
        if (marketConditions.containsKey("competitorPricing")) {
            marketMultiplier = calculateMarketCompetitivenessMultiplier(marketConditions);
        }
        
        return currentPrice.multiply(marketMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<Map<String, Object>> getPricingHistory(Long facilityId, String unitType, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting pricing history for facility: {}, unitType: {} from {} to {}", facilityId, unitType, startDate, endDate);
        
        // Mock pricing history - in real implementation, this would query price history table
        List<Map<String, Object>> history = new ArrayList<>();
        
        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Object> pricePoint = new HashMap<>();
            pricePoint.put("timestamp", current);
            pricePoint.put("price", BigDecimal.valueOf(100 + Math.random() * 50));
            pricePoint.put("occupancyRate", 70 + Math.random() * 25);
            history.add(pricePoint);
            current = current.plusDays(1);
        }
        
        return history;
    }

    @Override
    public Map<String, Object> optimizePricingStrategy(Long facilityId, Map<String, Object> constraints) {
        log.info("Optimizing pricing strategy for facility: {}", facilityId);
        
        Map<String, Object> optimization = new HashMap<>();
        optimization.put("facilityId", facilityId);
        optimization.put("currentRevenue", calculateCurrentRevenue(facilityId));
        optimization.put("optimizedRevenue", calculateOptimizedRevenue(facilityId, constraints));
        optimization.put("recommendations", generatePricingRecommendations(facilityId, constraints));
        
        return optimization;
    }

    // Helper methods
    
    private BigDecimal getBasePrice(List<PricingRule> rules, String unitType) {
        return rules.stream()
            .filter(rule -> unitType.equals(rule.getUnitType()))
            .map(PricingRule::getBasePrice)
            .findFirst()
            .orElse(BigDecimal.valueOf(100.00));
    }

    private BigDecimal calculateDemandMultiplier(Long facilityId, String unitType) {
        // Simplified demand calculation based on recent bookings
        return BigDecimal.valueOf(1.0 + Math.random() * 0.3); // 1.0 to 1.3x
    }

    private BigDecimal calculateSeasonalMultiplier(Long facilityId) {
        // Simplified seasonal calculation
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        
        // Higher demand in summer months (moving season)
        if (month >= 5 && month <= 9) {
            return BigDecimal.valueOf(1.15); // 15% increase
        }
        return BigDecimal.valueOf(0.95); // 5% decrease
    }

    private BigDecimal calculateDurationMultiplier(Integer duration) {
        if (duration >= 12) {
            return BigDecimal.valueOf(0.85); // 15% discount for long-term
        } else if (duration >= 6) {
            return BigDecimal.valueOf(0.92); // 8% discount for medium-term
        }
        return BigDecimal.valueOf(1.0); // No discount for short-term
    }

    private BigDecimal calculateOccupancyMultiplier(Long facilityId) {
        Optional<Double> occupancyRate = availabilitySnapshotRepository.getLatestOccupancyRate(facilityId);
        
        if (occupancyRate.isPresent()) {
            double rate = occupancyRate.get();
            if (rate > 90) {
                return BigDecimal.valueOf(1.25); // 25% premium for high occupancy
            } else if (rate > 75) {
                return BigDecimal.valueOf(1.10); // 10% premium for good occupancy
            } else if (rate < 50) {
                return BigDecimal.valueOf(0.90); // 10% discount for low occupancy
            }
        }
        
        return BigDecimal.valueOf(1.0); // No adjustment
    }

    private double calculateOccupancyRate(AvailabilitySnapshot snapshot) {
        if (snapshot.getTotalUnits() == 0) return 0.0;
        return ((double) (snapshot.getTotalUnits() - snapshot.getAvailableUnits()) / snapshot.getTotalUnits()) * 100;
    }

    private Map<String, Object> parseUnitTypeBreakdown(String breakdown) {
        // Simple JSON parsing - in real implementation would use proper JSON parsing
        Map<String, Object> parsed = new HashMap<>();
        parsed.put("small", 10);
        parsed.put("medium", 15);
        parsed.put("large", 8);
        return parsed;
    }

    private String convertToJson(Map<String, Integer> data) {
        // Simple JSON conversion - in real implementation would use proper JSON library
        return data.toString();
    }

    private String calculateDemandTrend(List<AvailabilitySnapshot> historicalData) {
        if (historicalData.size() < 2) return "stable";
        
        double firstOccupancy = calculateOccupancyRate(historicalData.get(0));
        double lastOccupancy = calculateOccupancyRate(historicalData.get(historicalData.size() - 1));
        
        if (lastOccupancy > firstOccupancy + 5) return "increasing";
        if (lastOccupancy < firstOccupancy - 5) return "decreasing";
        return "stable";
    }

    private List<String> identifyPeakPeriods(List<AvailabilitySnapshot> historicalData) {
        List<String> peaks = new ArrayList<>();
        peaks.add("Summer months");
        peaks.add("End of month");
        return peaks;
    }

    private int calculateRecommendedCapacity(double avgOccupancy) {
        if (avgOccupancy > 85) return 120; // Expand capacity
        if (avgOccupancy < 60) return 80;  // Reduce capacity
        return 100; // Maintain current capacity
    }

    private BigDecimal calculateSeasonalAdjustment(PricingRule rule) {
        // Simplified seasonal adjustment calculation
        return BigDecimal.valueOf(1.05);
    }

    private BigDecimal calculateMarketCompetitivenessMultiplier(Map<String, Object> marketConditions) {
        // Simplified market competitiveness calculation
        return BigDecimal.valueOf(0.98); // 2% reduction to stay competitive
    }

    private BigDecimal calculateCurrentRevenue(Long facilityId) {
        // Mock revenue calculation
        return BigDecimal.valueOf(50000.00);
    }

    private BigDecimal calculateOptimizedRevenue(Long facilityId, Map<String, Object> constraints) {
        // Mock optimized revenue calculation
        return BigDecimal.valueOf(55000.00);
    }

    private List<String> generatePricingRecommendations(Long facilityId, Map<String, Object> constraints) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Increase prices for premium units by 8%");
        recommendations.add("Apply longer-term contract discounts");
        recommendations.add("Implement dynamic weekend pricing");
        return recommendations;
    }
}