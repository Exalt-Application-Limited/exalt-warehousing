package com.gogidix.warehousing.shared.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WarehouseCalculationUtils
 */
class WarehouseCalculationUtilsTest {

    @Test
    @DisplayName("Should calculate capacity utilization correctly")
    void testCalculateCapacityUtilization() {
        // Test normal case
        BigDecimal used = new BigDecimal("75");
        BigDecimal total = new BigDecimal("100");
        BigDecimal result = WarehouseCalculationUtils.calculateCapacityUtilization(used, total);
        assertEquals(new BigDecimal("75.0000"), result);

        // Test over-capacity (should cap at 100%)
        used = new BigDecimal("120");
        result = WarehouseCalculationUtils.calculateCapacityUtilization(used, total);
        assertEquals(new BigDecimal("100"), result);

        // Test zero total capacity
        result = WarehouseCalculationUtils.calculateCapacityUtilization(used, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, result);

        // Test null values
        result = WarehouseCalculationUtils.calculateCapacityUtilization(null, total);
        assertEquals(BigDecimal.ZERO, result);

        result = WarehouseCalculationUtils.calculateCapacityUtilization(used, null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate throughput rate correctly")
    void testCalculateThroughputRate() {
        // Test normal case (60 items in 30 minutes = 120 items/hour)
        int itemsProcessed = 60;
        Duration timeSpan = Duration.ofMinutes(30);
        BigDecimal result = WarehouseCalculationUtils.calculateThroughputRate(itemsProcessed, timeSpan);
        assertEquals(new BigDecimal("120.0000"), result);

        // Test zero duration
        result = WarehouseCalculationUtils.calculateThroughputRate(itemsProcessed, Duration.ZERO);
        assertEquals(BigDecimal.ZERO, result);

        // Test null duration
        result = WarehouseCalculationUtils.calculateThroughputRate(itemsProcessed, null);
        assertEquals(BigDecimal.ZERO, result);

        // Test negative duration
        result = WarehouseCalculationUtils.calculateThroughputRate(itemsProcessed, Duration.ofMinutes(-30));
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate accuracy percentage correctly")
    void testCalculateAccuracy() {
        // Test normal case
        int correctOrders = 95;
        int totalOrders = 100;
        BigDecimal result = WarehouseCalculationUtils.calculateAccuracy(correctOrders, totalOrders);
        assertEquals(new BigDecimal("95.0000"), result);

        // Test perfect accuracy
        result = WarehouseCalculationUtils.calculateAccuracy(100, 100);
        assertEquals(new BigDecimal("100.0000"), result);

        // Test zero total orders
        result = WarehouseCalculationUtils.calculateAccuracy(correctOrders, 0);
        assertEquals(BigDecimal.ZERO, result);

        // Test negative total orders
        result = WarehouseCalculationUtils.calculateAccuracy(correctOrders, -10);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate inventory turnover correctly")
    void testCalculateInventoryTurnover() {
        // Test normal case
        BigDecimal cogs = new BigDecimal("1000000");
        BigDecimal avgInventory = new BigDecimal("250000");
        BigDecimal result = WarehouseCalculationUtils.calculateInventoryTurnover(cogs, avgInventory);
        assertEquals(new BigDecimal("4.0000"), result);

        // Test zero average inventory
        result = WarehouseCalculationUtils.calculateInventoryTurnover(cogs, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, result);

        // Test null values
        result = WarehouseCalculationUtils.calculateInventoryTurnover(null, avgInventory);
        assertEquals(BigDecimal.ZERO, result);

        result = WarehouseCalculationUtils.calculateInventoryTurnover(cogs, null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate order cycle time correctly")
    void testCalculateOrderCycleTime() {
        // Test normal case (2 hours)
        LocalDateTime orderReceived = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime orderShipped = LocalDateTime.of(2023, 1, 1, 12, 0);
        BigDecimal result = WarehouseCalculationUtils.calculateOrderCycleTime(orderReceived, orderShipped);
        assertEquals(new BigDecimal("2.0000"), result);

        // Test shipped before received (invalid)
        result = WarehouseCalculationUtils.calculateOrderCycleTime(orderShipped, orderReceived);
        assertEquals(BigDecimal.ZERO, result);

        // Test null values
        result = WarehouseCalculationUtils.calculateOrderCycleTime(null, orderShipped);
        assertEquals(BigDecimal.ZERO, result);

        result = WarehouseCalculationUtils.calculateOrderCycleTime(orderReceived, null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate weighted performance score correctly")
    void testCalculateWeightedPerformanceScore() {
        // Setup test data
        Map<String, BigDecimal> metrics = new HashMap<>();
        metrics.put("efficiency", new BigDecimal("85"));
        metrics.put("accuracy", new BigDecimal("95"));
        metrics.put("speed", new BigDecimal("75"));

        Map<String, BigDecimal> weights = new HashMap<>();
        weights.put("efficiency", new BigDecimal("0.4"));
        weights.put("accuracy", new BigDecimal("0.4"));
        weights.put("speed", new BigDecimal("0.2"));

        // Test normal case
        BigDecimal result = WarehouseCalculationUtils.calculateWeightedPerformanceScore(metrics, weights);
        // Expected: (85*0.4 + 95*0.4 + 75*0.2) / (0.4+0.4+0.2) = 87
        assertEquals(new BigDecimal("87.0000"), result);

        // Test empty maps
        result = WarehouseCalculationUtils.calculateWeightedPerformanceScore(new HashMap<>(), weights);
        assertEquals(BigDecimal.ZERO, result);

        // Test null maps
        result = WarehouseCalculationUtils.calculateWeightedPerformanceScore(null, weights);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate statistics correctly")
    void testCalculateStatistics() {
        // Test normal case
        List<BigDecimal> values = Arrays.asList(
                new BigDecimal("10"),
                new BigDecimal("20"),
                new BigDecimal("30"),
                new BigDecimal("40"),
                new BigDecimal("50")
        );

        WarehouseCalculationUtils.StatisticalSummary stats = 
                WarehouseCalculationUtils.calculateStatistics(values);

        assertEquals(5, stats.getCount());
        assertEquals(new BigDecimal("30.0"), stats.getMean().setScale(1, BigDecimal.ROUND_HALF_UP));
        assertEquals(new BigDecimal("10.0"), stats.getMinimum().setScale(1, BigDecimal.ROUND_HALF_UP));
        assertEquals(new BigDecimal("50.0"), stats.getMaximum().setScale(1, BigDecimal.ROUND_HALF_UP));

        // Test empty list
        stats = WarehouseCalculationUtils.calculateStatistics(Arrays.asList());
        assertEquals(0, stats.getCount());

        // Test null list
        stats = WarehouseCalculationUtils.calculateStatistics(null);
        assertEquals(0, stats.getCount());
    }

    @Test
    @DisplayName("Should calculate trend direction correctly")
    void testCalculateTrendDirection() {
        // Test increasing trend
        List<BigDecimal> increasingValues = Arrays.asList(
                new BigDecimal("10"),
                new BigDecimal("15"),
                new BigDecimal("20"),
                new BigDecimal("25"),
                new BigDecimal("30")
        );
        int trend = WarehouseCalculationUtils.calculateTrendDirection(increasingValues);
        assertEquals(1, trend); // Increasing

        // Test decreasing trend
        List<BigDecimal> decreasingValues = Arrays.asList(
                new BigDecimal("30"),
                new BigDecimal("25"),
                new BigDecimal("20"),
                new BigDecimal("15"),
                new BigDecimal("10")
        );
        trend = WarehouseCalculationUtils.calculateTrendDirection(decreasingValues);
        assertEquals(-1, trend); // Decreasing

        // Test stable trend
        List<BigDecimal> stableValues = Arrays.asList(
                new BigDecimal("20"),
                new BigDecimal("20.1"),
                new BigDecimal("19.9"),
                new BigDecimal("20.05"),
                new BigDecimal("20")
        );
        trend = WarehouseCalculationUtils.calculateTrendDirection(stableValues);
        assertEquals(0, trend); // Stable

        // Test insufficient data
        trend = WarehouseCalculationUtils.calculateTrendDirection(Arrays.asList(new BigDecimal("10")));
        assertEquals(0, trend); // Unknown/stable

        // Test null list
        trend = WarehouseCalculationUtils.calculateTrendDirection(null);
        assertEquals(0, trend); // Unknown/stable
    }

    @Test
    @DisplayName("Should calculate cost per order correctly")
    void testCalculateCostPerOrder() {
        // Test normal case
        BigDecimal totalCost = new BigDecimal("50000");
        int ordersProcessed = 1000;
        BigDecimal result = WarehouseCalculationUtils.calculateCostPerOrder(totalCost, ordersProcessed);
        assertEquals(new BigDecimal("50.0000"), result);

        // Test zero orders
        result = WarehouseCalculationUtils.calculateCostPerOrder(totalCost, 0);
        assertEquals(BigDecimal.ZERO, result);

        // Test negative orders
        result = WarehouseCalculationUtils.calculateCostPerOrder(totalCost, -100);
        assertEquals(BigDecimal.ZERO, result);

        // Test null cost
        result = WarehouseCalculationUtils.calculateCostPerOrder(null, ordersProcessed);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate damage rate correctly")
    void testCalculateDamageRate() {
        // Test normal case
        int damagedItems = 5;
        int totalItems = 1000;
        BigDecimal result = WarehouseCalculationUtils.calculateDamageRate(damagedItems, totalItems);
        assertEquals(new BigDecimal("0.5000"), result);

        // Test zero total items
        result = WarehouseCalculationUtils.calculateDamageRate(damagedItems, 0);
        assertEquals(BigDecimal.ZERO, result);

        // Test negative total items
        result = WarehouseCalculationUtils.calculateDamageRate(damagedItems, -100);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should convert square meters to square feet correctly")
    void testConvertSquareMetersToSquareFeet() {
        // Test normal case (100 sqm ≈ 1076.4 sqft)
        BigDecimal squareMeters = new BigDecimal("100");
        BigDecimal result = WarehouseCalculationUtils.convertSquareMetersToSquareFeet(squareMeters);
        assertEquals(new BigDecimal("1076.4000"), result);

        // Test null value
        result = WarehouseCalculationUtils.convertSquareMetersToSquareFeet(null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should convert square feet to square meters correctly")
    void testConvertSquareFeetToSquareMeters() {
        // Test normal case (1000 sqft ≈ 92.903 sqm)
        BigDecimal squareFeet = new BigDecimal("1000");
        BigDecimal result = WarehouseCalculationUtils.convertSquareFeetToSquareMeters(squareFeet);
        assertEquals(new BigDecimal("92.9030"), result);

        // Test null value
        result = WarehouseCalculationUtils.convertSquareFeetToSquareMeters(null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate equipment utilization correctly")
    void testCalculateEquipmentUtilization() {
        // Test normal case (8 hours used out of 10 hours available = 80%)
        BigDecimal actualHours = new BigDecimal("8");
        BigDecimal availableHours = new BigDecimal("10");
        BigDecimal result = WarehouseCalculationUtils.calculateEquipmentUtilization(actualHours, availableHours);
        assertEquals(new BigDecimal("80.0000"), result);

        // Test over-utilization (should cap at 100%)
        actualHours = new BigDecimal("12");
        result = WarehouseCalculationUtils.calculateEquipmentUtilization(actualHours, availableHours);
        assertEquals(new BigDecimal("100"), result);

        // Test zero available hours
        result = WarehouseCalculationUtils.calculateEquipmentUtilization(actualHours, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, result);

        // Test null values
        result = WarehouseCalculationUtils.calculateEquipmentUtilization(null, availableHours);
        assertEquals(BigDecimal.ZERO, result);
    }
}