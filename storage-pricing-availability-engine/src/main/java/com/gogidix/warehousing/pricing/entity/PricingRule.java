package com.gogidix.warehousing.pricing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Pricing Rule Entity
 * 
 * Defines dynamic pricing rules based on various factors including
 * demand, occupancy, seasonality, and market conditions.
 */
@Entity
@Table(name = "pricing_rules")
@Data
@EqualsAndHashCode(callSuper = false)
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleName;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleStatus status;

    // Rule Conditions
    @Column(nullable = false)
    private Integer priority = 1;

    @Column
    private BigDecimal minOccupancyRate;

    @Column
    private BigDecimal maxOccupancyRate;

    @Column
    private Integer minDemandScore;

    @Column
    private Integer maxDemandScore;

    @Column
    private String facilityType;

    @Column
    private String unitType;
    
    @Column
    private String unitSizeCategory;
    
    @Column
    private BigDecimal basePrice;

    @Column
    private String geographicRegion;

    @Column
    private String seasonalPeriod;

    @Column
    private String dayOfWeek;

    @Column
    private Integer hourOfDay;

    // Pricing Adjustments
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType adjustmentType;

    @Column(nullable = false)
    private BigDecimal adjustmentValue;

    @Column
    private BigDecimal minPrice;

    @Column
    private BigDecimal maxPrice;

    @Column
    private BigDecimal capPercentage;

    // Advanced Configuration
    @ElementCollection
    @CollectionTable(name = "pricing_rule_parameters", joinColumns = @JoinColumn(name = "rule_id"))
    @MapKeyColumn(name = "parameter_name")
    @Column(name = "parameter_value")
    private Map<String, String> parameters;

    @Column(columnDefinition = "TEXT")
    private String formula;

    @Column(nullable = false)
    private Boolean compoundable = true;

    // Validity Period
    @Column
    private LocalDateTime validFrom;

    @Column
    private LocalDateTime validUntil;

    // Usage Tracking
    @Column(nullable = false)
    private Integer applicationCount = 0;

    @Column
    private LocalDateTime lastAppliedDate;

    @Column(nullable = false)
    private BigDecimal totalRevenueImpact = BigDecimal.ZERO;

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    @Column
    private String lastModifiedBy;

    // Business Methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == RuleStatus.ACTIVE &&
               (this.validFrom == null || now.isAfter(this.validFrom)) &&
               (this.validUntil == null || now.isBefore(this.validUntil));
    }

    public boolean appliesTo(PricingContext context) {
        if (!isActive()) return false;

        // Check occupancy rate
        if (this.minOccupancyRate != null && 
            context.getOccupancyRate().compareTo(this.minOccupancyRate) < 0) {
            return false;
        }
        if (this.maxOccupancyRate != null && 
            context.getOccupancyRate().compareTo(this.maxOccupancyRate) > 0) {
            return false;
        }

        // Check demand score
        if (this.minDemandScore != null && context.getDemandScore() < this.minDemandScore) {
            return false;
        }
        if (this.maxDemandScore != null && context.getDemandScore() > this.maxDemandScore) {
            return false;
        }

        // Check facility and unit filters
        if (this.facilityType != null && 
            !this.facilityType.equals(context.getFacilityType())) {
            return false;
        }
        if (this.unitSizeCategory != null && 
            !this.unitSizeCategory.equals(context.getUnitSizeCategory())) {
            return false;
        }

        // Check geographic region
        if (this.geographicRegion != null && 
            !this.geographicRegion.equals(context.getGeographicRegion())) {
            return false;
        }

        // Check time-based conditions
        if (this.seasonalPeriod != null && 
            !this.seasonalPeriod.equals(context.getSeasonalPeriod())) {
            return false;
        }
        if (this.dayOfWeek != null && 
            !this.dayOfWeek.equals(context.getDayOfWeek())) {
            return false;
        }
        if (this.hourOfDay != null && !this.hourOfDay.equals(context.getHourOfDay())) {
            return false;
        }

        return true;
    }

    public BigDecimal calculateAdjustment(BigDecimal basePrice, PricingContext context) {
        BigDecimal adjustment = BigDecimal.ZERO;

        switch (this.adjustmentType) {
            case PERCENTAGE:
                adjustment = basePrice.multiply(this.adjustmentValue).divide(BigDecimal.valueOf(100));
                break;
            case FIXED_AMOUNT:
                adjustment = this.adjustmentValue;
                break;
            case MULTIPLIER:
                adjustment = basePrice.multiply(this.adjustmentValue).subtract(basePrice);
                break;
            case FORMULA:
                adjustment = evaluateFormula(basePrice, context);
                break;
        }

        // Apply caps
        if (this.capPercentage != null) {
            BigDecimal maxAdjustment = basePrice.multiply(this.capPercentage).divide(BigDecimal.valueOf(100));
            if (adjustment.abs().compareTo(maxAdjustment) > 0) {
                adjustment = adjustment.signum() == 1 ? maxAdjustment : maxAdjustment.negate();
            }
        }

        return adjustment;
    }

    private BigDecimal evaluateFormula(BigDecimal basePrice, PricingContext context) {
        // Simple formula evaluation - in production would use expression parser
        if ("DEMAND_MULTIPLIER".equals(this.formula)) {
            return basePrice.multiply(BigDecimal.valueOf(context.getDemandScore() / 100.0)).subtract(basePrice);
        }
        return BigDecimal.ZERO;
    }

    public void recordApplication(BigDecimal revenueImpact) {
        this.applicationCount++;
        this.lastAppliedDate = LocalDateTime.now();
        this.totalRevenueImpact = this.totalRevenueImpact.add(revenueImpact);
    }

    public void activate() {
        this.status = RuleStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = RuleStatus.INACTIVE;
    }

    public void suspend() {
        this.status = RuleStatus.SUSPENDED;
    }

    // Enums
    public enum RuleType {
        DEMAND_BASED,
        OCCUPANCY_BASED,
        SEASONAL,
        PROMOTIONAL,
        COMPETITOR_BASED,
        TIME_OF_DAY,
        GEOGRAPHIC,
        CUSTOMER_SEGMENT,
        INVENTORY_BASED,
        REVENUE_OPTIMIZATION
    }

    public enum RuleStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        EXPIRED,
        DRAFT
    }

    public enum AdjustmentType {
        PERCENTAGE,
        FIXED_AMOUNT,
        MULTIPLIER,
        FORMULA
    }

    @Data
    public static class PricingContext {
        private BigDecimal occupancyRate;
        private Integer demandScore;
        private String facilityType;
        private String unitSizeCategory;
        private String geographicRegion;
        private String seasonalPeriod;
        private String dayOfWeek;
        private Integer hourOfDay;
        private BigDecimal competitorPrice;
        private String customerSegment;
        private Integer availableUnits;
        private BigDecimal historicalDemand;
    }
}