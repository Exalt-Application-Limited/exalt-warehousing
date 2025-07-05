package com.gogidix.warehousing.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Coverage Plan Entity
 * 
 * Represents different insurance coverage plans available to customers.
 */
@Entity
@Table(name = "coverage_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoveragePlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String planCode;
    
    @Column(nullable = false)
    private String planName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageType coverageType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal maxCoverageAmount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minCoverageAmount;
    
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal premiumRate; // As percentage of coverage amount
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePremium;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deductibleAmount;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @ElementCollection
    @CollectionTable(name = "coverage_plan_features", joinColumns = @JoinColumn(name = "coverage_plan_id"))
    @Column(name = "feature")
    private List<String> features;
    
    @ElementCollection
    @CollectionTable(name = "coverage_plan_exclusions", joinColumns = @JoinColumn(name = "coverage_plan_id"))
    @Column(name = "exclusion")
    private List<String> exclusions;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "coveragePlan", fetch = FetchType.LAZY)
    private List<InsurancePolicy> policies;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum CoverageType {
        BASIC, STANDARD, PREMIUM, COMPREHENSIVE, CUSTOM
    }
}