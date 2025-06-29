package com.exalt.warehousing.shared.utils;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for warehouse-related calculations and metrics
 * 
 * This class provides common calculation methods used across the warehousing domain
 * for performance metrics, capacity planning, efficiency calculations, and statistical analysis.
 */
@UtilityClass
public class WarehouseCalculationUtils {

    // Constants for calculations
    private static final BigDecimal HOURS_PER_DAY = BigDecimal.valueOf(24);
    private static final BigDecimal MINUTES_PER_HOUR = BigDecimal.valueOf(60);
    private static final BigDecimal SECONDS_PER_MINUTE = BigDecimal.valueOf(60);
    private static final int DEFAULT_SCALE = 4;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    /**
     * Calculate warehouse capacity utilization percentage
     * 
     * @param usedCapacity the amount of capacity currently used
     * @param totalCapacity the total available capacity
     * @return utilization percentage (0-100)
     */
    public static BigDecimal calculateCapacityUtilization(BigDecimal usedCapacity, BigDecimal totalCapacity) {
        if (totalCapacity == null || totalCapacity.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (usedCapacity == null || usedCapacity.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return usedCapacity
                .divide(totalCapacity, DEFAULT_SCALE, DEFAULT_ROUNDING)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100)); // Cap at 100%
    }

    /**
     * Calculate throughput rate (items per hour)
     * 
     * @param itemsProcessed number of items processed
     * @param timeSpan time span in which items were processed
     * @return throughput rate in items per hour
     */
    public static BigDecimal calculateThroughputRate(int itemsProcessed, Duration timeSpan) {
        if (timeSpan == null || timeSpan.isZero() || timeSpan.isNegative()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal hours = BigDecimal.valueOf(timeSpan.toMinutes())
                .divide(MINUTES_PER_HOUR, DEFAULT_SCALE, DEFAULT_ROUNDING);
        
        if (hours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(itemsProcessed)
                .divide(hours, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate order fulfillment accuracy percentage
     * 
     * @param correctOrders number of correctly fulfilled orders
     * @param totalOrders total number of orders processed
     * @return accuracy percentage (0-100)
     */
    public static BigDecimal calculateAccuracy(int correctOrders, int totalOrders) {
        if (totalOrders <= 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(correctOrders)
                .divide(BigDecimal.valueOf(totalOrders), DEFAULT_SCALE, DEFAULT_ROUNDING)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate inventory turnover ratio
     * 
     * @param costOfGoodsSold cost of goods sold over the period
     * @param averageInventoryValue average inventory value during the period
     * @return inventory turnover ratio
     */
    public static BigDecimal calculateInventoryTurnover(BigDecimal costOfGoodsSold, BigDecimal averageInventoryValue) {
        if (averageInventoryValue == null || averageInventoryValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (costOfGoodsSold == null || costOfGoodsSold.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return costOfGoodsSold.divide(averageInventoryValue, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate days sales of inventory (DSI)
     * 
     * @param averageInventoryValue average inventory value
     * @param costOfGoodsSold cost of goods sold over the period
     * @param periodDays number of days in the period
     * @return days sales of inventory
     */
    public static BigDecimal calculateDaysSalesOfInventory(BigDecimal averageInventoryValue, 
                                                          BigDecimal costOfGoodsSold, 
                                                          int periodDays) {
        if (costOfGoodsSold == null || costOfGoodsSold.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal dailyCOGS = costOfGoodsSold.divide(BigDecimal.valueOf(periodDays), DEFAULT_SCALE, DEFAULT_ROUNDING);
        
        if (dailyCOGS.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return averageInventoryValue.divide(dailyCOGS, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate order cycle time
     * 
     * @param orderReceivedTime when the order was received
     * @param orderShippedTime when the order was shipped
     * @return cycle time in hours
     */
    public static BigDecimal calculateOrderCycleTime(LocalDateTime orderReceivedTime, LocalDateTime orderShippedTime) {
        if (orderReceivedTime == null || orderShippedTime == null) {
            return BigDecimal.ZERO;
        }
        
        if (orderShippedTime.isBefore(orderReceivedTime)) {
            return BigDecimal.ZERO;
        }
        
        Duration duration = Duration.between(orderReceivedTime, orderShippedTime);
        return BigDecimal.valueOf(duration.toMinutes())
                .divide(MINUTES_PER_HOUR, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate picking productivity (lines per hour)
     * 
     * @param linesPickedCount number of order lines picked
     * @param pickingTimeSpan time spent picking
     * @return productivity in lines per hour
     */
    public static BigDecimal calculatePickingProductivity(int linesPickedCount, Duration pickingTimeSpan) {
        if (pickingTimeSpan == null || pickingTimeSpan.isZero() || pickingTimeSpan.isNegative()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal hours = BigDecimal.valueOf(pickingTimeSpan.toMinutes())
                .divide(MINUTES_PER_HOUR, DEFAULT_SCALE, DEFAULT_ROUNDING);
        
        if (hours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(linesPickedCount)
                .divide(hours, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate cost per order
     * 
     * @param totalOperatingCost total operating costs for the period
     * @param ordersProcessed number of orders processed in the period
     * @return cost per order
     */
    public static BigDecimal calculateCostPerOrder(BigDecimal totalOperatingCost, int ordersProcessed) {
        if (ordersProcessed <= 0) {
            return BigDecimal.ZERO;
        }
        if (totalOperatingCost == null || totalOperatingCost.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return totalOperatingCost.divide(BigDecimal.valueOf(ordersProcessed), DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate space productivity (revenue per square foot)
     * 
     * @param revenue revenue generated
     * @param warehouseArea warehouse area in square feet
     * @return revenue per square foot
     */
    public static BigDecimal calculateSpaceProductivity(BigDecimal revenue, BigDecimal warehouseArea) {
        if (warehouseArea == null || warehouseArea.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (revenue == null || revenue.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return revenue.divide(warehouseArea, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate equipment utilization percentage
     * 
     * @param actualOperatingHours actual hours equipment was in use
     * @param availableHours total hours equipment was available
     * @return utilization percentage (0-100)
     */
    public static BigDecimal calculateEquipmentUtilization(BigDecimal actualOperatingHours, BigDecimal availableHours) {
        if (availableHours == null || availableHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (actualOperatingHours == null || actualOperatingHours.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return actualOperatingHours
                .divide(availableHours, DEFAULT_SCALE, DEFAULT_ROUNDING)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100)); // Cap at 100%
    }

    /**
     * Calculate labor productivity (units per labor hour)
     * 
     * @param unitsProduced number of units produced
     * @param laborHours total labor hours
     * @return productivity in units per labor hour
     */
    public static BigDecimal calculateLaborProductivity(int unitsProduced, BigDecimal laborHours) {
        if (laborHours == null || laborHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(unitsProduced)
                .divide(laborHours, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate damage rate percentage
     * 
     * @param damagedItems number of damaged items
     * @param totalItems total number of items processed
     * @return damage rate percentage (0-100)
     */
    public static BigDecimal calculateDamageRate(int damagedItems, int totalItems) {
        if (totalItems <= 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(damagedItems)
                .divide(BigDecimal.valueOf(totalItems), DEFAULT_SCALE, DEFAULT_ROUNDING)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate return rate percentage
     * 
     * @param returnedOrders number of returned orders
     * @param totalOrders total number of orders
     * @return return rate percentage (0-100)
     */
    public static BigDecimal calculateReturnRate(int returnedOrders, int totalOrders) {
        if (totalOrders <= 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(returnedOrders)
                .divide(BigDecimal.valueOf(totalOrders), DEFAULT_SCALE, DEFAULT_ROUNDING)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate storage cost per unit
     * 
     * @param totalStorageCost total storage costs for the period
     * @param averageUnitsStored average number of units stored
     * @param periodDays number of days in the period
     * @return storage cost per unit per day
     */
    public static BigDecimal calculateStorageCostPerUnit(BigDecimal totalStorageCost, 
                                                        int averageUnitsStored, 
                                                        int periodDays) {
        if (averageUnitsStored <= 0 || periodDays <= 0) {
            return BigDecimal.ZERO;
        }
        if (totalStorageCost == null || totalStorageCost.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalUnitDays = BigDecimal.valueOf(averageUnitsStored)
                .multiply(BigDecimal.valueOf(periodDays));
        
        return totalStorageCost.divide(totalUnitDays, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate weighted performance score
     * 
     * @param metrics map of metric names to values
     * @param weights map of metric names to weights
     * @return weighted performance score
     */
    public static BigDecimal calculateWeightedPerformanceScore(Map<String, BigDecimal> metrics, 
                                                              Map<String, BigDecimal> weights) {
        if (metrics == null || metrics.isEmpty() || weights == null || weights.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        
        for (Map.Entry<String, BigDecimal> entry : metrics.entrySet()) {
            String metricName = entry.getKey();
            BigDecimal metricValue = entry.getValue();
            BigDecimal weight = weights.get(metricName);
            
            if (metricValue != null && weight != null && weight.compareTo(BigDecimal.ZERO) > 0) {
                totalScore = totalScore.add(metricValue.multiply(weight));
                totalWeight = totalWeight.add(weight);
            }
        }
        
        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return totalScore.divide(totalWeight, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculate statistical measures for a list of values
     * 
     * @param values list of numerical values
     * @return statistical summary
     */
    public static StatisticalSummary calculateStatistics(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return StatisticalSummary.empty();
        }
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        values.stream()
                .filter(value -> value != null)
                .mapToDouble(BigDecimal::doubleValue)
                .forEach(stats::addValue);
        
        if (stats.getN() == 0) {
            return StatisticalSummary.empty();
        }
        
        return StatisticalSummary.builder()
                .count((int) stats.getN())
                .sum(BigDecimal.valueOf(stats.getSum()))
                .mean(BigDecimal.valueOf(stats.getMean()))
                .standardDeviation(BigDecimal.valueOf(stats.getStandardDeviation()))
                .variance(BigDecimal.valueOf(stats.getVariance()))
                .minimum(BigDecimal.valueOf(stats.getMin()))
                .maximum(BigDecimal.valueOf(stats.getMax()))
                .median(BigDecimal.valueOf(stats.getPercentile(50)))
                .percentile25(BigDecimal.valueOf(stats.getPercentile(25)))
                .percentile75(BigDecimal.valueOf(stats.getPercentile(75)))
                .build();
    }

    /**
     * Calculate trend direction from historical data
     * 
     * @param values historical values in chronological order
     * @return trend direction (-1 = declining, 0 = stable, 1 = increasing)
     */
    public static int calculateTrendDirection(List<BigDecimal> values) {
        if (values == null || values.size() < 2) {
            return 0; // Stable/unknown
        }
        
        List<BigDecimal> nonNullValues = values.stream()
                .filter(value -> value != null)
                .collect(Collectors.toList());
        
        if (nonNullValues.size() < 2) {
            return 0;
        }
        
        // Simple linear regression slope calculation
        int n = nonNullValues.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = nonNullValues.get(i).doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        
        // Determine trend direction based on slope
        if (slope > 0.01) { // Threshold for significant increase
            return 1;
        } else if (slope < -0.01) { // Threshold for significant decrease
            return -1;
        } else {
            return 0; // Stable
        }
    }

    /**
     * Convert area from square meters to square feet
     * 
     * @param squareMeters area in square meters
     * @return area in square feet
     */
    public static BigDecimal convertSquareMetersToSquareFeet(BigDecimal squareMeters) {
        if (squareMeters == null) {
            return BigDecimal.ZERO;
        }
        
        // 1 square meter = 10.764 square feet
        return squareMeters.multiply(BigDecimal.valueOf(10.764))
                .setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Convert area from square feet to square meters
     * 
     * @param squareFeet area in square feet
     * @return area in square meters
     */
    public static BigDecimal convertSquareFeetToSquareMeters(BigDecimal squareFeet) {
        if (squareFeet == null) {
            return BigDecimal.ZERO;
        }
        
        // 1 square foot = 0.092903 square meters
        return squareFeet.multiply(BigDecimal.valueOf(0.092903))
                .setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Statistical summary data class
     */
    @lombok.Data
    @lombok.Builder
    public static class StatisticalSummary {
        private int count;
        private BigDecimal sum;
        private BigDecimal mean;
        private BigDecimal standardDeviation;
        private BigDecimal variance;
        private BigDecimal minimum;
        private BigDecimal maximum;
        private BigDecimal median;
        private BigDecimal percentile25;
        private BigDecimal percentile75;
        
        public static StatisticalSummary empty() {
            return StatisticalSummary.builder()
                    .count(0)
                    .sum(BigDecimal.ZERO)
                    .mean(BigDecimal.ZERO)
                    .standardDeviation(BigDecimal.ZERO)
                    .variance(BigDecimal.ZERO)
                    .minimum(BigDecimal.ZERO)
                    .maximum(BigDecimal.ZERO)
                    .median(BigDecimal.ZERO)
                    .percentile25(BigDecimal.ZERO)
                    .percentile75(BigDecimal.ZERO)
                    .build();
        }
    }
}