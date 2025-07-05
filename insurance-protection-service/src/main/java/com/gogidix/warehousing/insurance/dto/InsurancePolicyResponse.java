package com.gogidix.warehousing.insurance.dto;

import com.gogidix.warehousing.insurance.entity.InsurancePolicy;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Insurance Policy Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePolicyResponse {
    
    private Long id;
    private String policyNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long storageUnitId;
    private String storageUnitNumber;
    private CoveragePlanSummary coveragePlan;
    private BigDecimal coverageAmount;
    private BigDecimal monthlyPremium;
    private BigDecimal deductible;
    private InsurancePolicy.PolicyStatus status;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer claimCount;
    private BigDecimal totalClaimedAmount;
    private PaymentSummary paymentSummary;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoveragePlanSummary {
        private Long id;
        private String planCode;
        private String planName;
        private String coverageType;
        private BigDecimal maxCoverageAmount;
        private BigDecimal premiumRate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentSummary {
        private BigDecimal totalPaid;
        private BigDecimal pendingAmount;
        private LocalDate nextDueDate;
        private String paymentStatus;
    }
}