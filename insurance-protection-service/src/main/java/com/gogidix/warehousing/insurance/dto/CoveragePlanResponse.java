package com.gogidix.warehousing.insurance.dto;

import com.gogidix.warehousing.insurance.entity.CoveragePlan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Coverage Plan Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoveragePlanResponse {
    
    private Long id;
    private String planCode;
    private String planName;
    private String description;
    private CoveragePlan.CoverageType coverageType;
    private BigDecimal maxCoverageAmount;
    private BigDecimal minCoverageAmount;
    private BigDecimal premiumRate;
    private BigDecimal basePremium;
    private BigDecimal deductibleAmount;
    private Boolean isActive;
    private List<String> features;
    private List<String> exclusions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer activePolicyCount;
}