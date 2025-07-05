package com.gogidix.warehousing.insurance.dto;

import com.gogidix.warehousing.insurance.entity.InsuranceClaim;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Insurance Claim Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceClaimResponse {
    
    private Long id;
    private String claimNumber;
    private PolicySummary policy;
    private String incidentType;
    private LocalDate incidentDate;
    private String incidentDescription;
    private BigDecimal claimedAmount;
    private BigDecimal assessedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal paidAmount;
    private InsuranceClaim.ClaimStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime assessedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
    private AssessorInfo assessor;
    private ApproverInfo approver;
    private String assessorNotes;
    private String approverNotes;
    private List<ClaimDocumentSummary> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PolicySummary {
        private Long id;
        private String policyNumber;
        private String customerName;
        private String storageUnitNumber;
        private BigDecimal coverageAmount;
        private BigDecimal deductible;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssessorInfo {
        private Long id;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApproverInfo {
        private Long id;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClaimDocumentSummary {
        private Long id;
        private String fileName;
        private String documentType;
        private String description;
        private LocalDateTime uploadedAt;
    }
}