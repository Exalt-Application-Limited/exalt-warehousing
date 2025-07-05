package com.gogidix.warehousing.insurance.service.impl;

import com.gogidix.warehousing.insurance.dto.*;
import com.gogidix.warehousing.insurance.entity.*;
import com.gogidix.warehousing.insurance.repository.*;
import com.gogidix.warehousing.insurance.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Insurance Service Implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InsuranceServiceImpl implements InsuranceService {
    
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;
    private final CoveragePlanRepository coveragePlanRepository;
    
    @Override
    public InsurancePolicyResponse createPolicy(CreatePolicyRequest request) {
        log.info("Creating insurance policy for customer: {}, storage unit: {}", 
                request.getCustomerId(), request.getStorageUnitId());
        
        // Validate coverage plan exists
        CoveragePlan coveragePlan = coveragePlanRepository.findById(request.getCoveragePlanId())
            .orElseThrow(() -> new RuntimeException("Coverage plan not found: " + request.getCoveragePlanId()));
        
        // Calculate premium
        BigDecimal monthlyPremium = calculatePremium(request.getCoveragePlanId(), request.getCoverageAmount());
        
        // Create policy
        InsurancePolicy policy = InsurancePolicy.builder()
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .storageUnitId(request.getStorageUnitId())
            .storageUnitNumber(request.getStorageUnitNumber())
            .coveragePlan(coveragePlan)
            .coverageAmount(request.getCoverageAmount())
            .monthlyPremium(monthlyPremium)
            .deductible(coveragePlan.getDeductibleAmount())
            .status(InsurancePolicy.PolicyStatus.ACTIVE)
            .effectiveDate(request.getEffectiveDate())
            .expirationDate(request.getExpirationDate() != null ? 
                request.getExpirationDate() : request.getEffectiveDate().plusYears(1))
            .createdAt(LocalDateTime.now())
            .build();
        
        policy = policyRepository.save(policy);
        
        log.info("Created insurance policy: {} for customer: {}", policy.getPolicyNumber(), request.getCustomerId());
        return mapToPolicyResponse(policy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InsurancePolicyResponse getPolicyById(Long policyId) {
        log.debug("Retrieving policy by ID: {}", policyId);
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new RuntimeException("Policy not found with ID: " + policyId));
        return mapToPolicyResponse(policy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InsurancePolicyResponse getPolicyByNumber(String policyNumber) {
        log.debug("Retrieving policy by number: {}", policyNumber);
        InsurancePolicy policy = policyRepository.findByPolicyNumber(policyNumber)
            .orElseThrow(() -> new RuntimeException("Policy not found with number: " + policyNumber));
        return mapToPolicyResponse(policy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<InsurancePolicyResponse> getCustomerPolicies(Long customerId, Pageable pageable) {
        log.debug("Retrieving policies for customer: {}", customerId);
        return policyRepository.findByCustomerId(customerId, pageable)
            .map(this::mapToPolicyResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InsurancePolicyResponse getPolicyByStorageUnit(Long storageUnitId) {
        log.debug("Retrieving policy for storage unit: {}", storageUnitId);
        InsurancePolicy policy = policyRepository.findByStorageUnitId(storageUnitId)
            .orElseThrow(() -> new RuntimeException("No policy found for storage unit: " + storageUnitId));
        return mapToPolicyResponse(policy);
    }
    
    @Override
    public InsurancePolicyResponse updatePolicyStatus(Long policyId, InsurancePolicy.PolicyStatus status) {
        log.info("Updating policy {} status to: {}", policyId, status);
        
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new RuntimeException("Policy not found with ID: " + policyId));
        
        policy.setStatus(status);
        policy.setUpdatedAt(LocalDateTime.now());
        
        if (status == InsurancePolicy.PolicyStatus.CANCELLED) {
            policy.setCancelledAt(LocalDateTime.now());
        }
        
        policy = policyRepository.save(policy);
        
        log.info("Updated policy {} status to: {}", policyId, status);
        return mapToPolicyResponse(policy);
    }
    
    @Override
    public InsurancePolicyResponse cancelPolicy(Long policyId, String reason) {
        log.info("Cancelling policy: {} with reason: {}", policyId, reason);
        
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new RuntimeException("Policy not found with ID: " + policyId));
        
        policy.setStatus(InsurancePolicy.PolicyStatus.CANCELLED);
        policy.setCancellationReason(reason);
        policy.setCancelledAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());
        
        policy = policyRepository.save(policy);
        
        log.info("Cancelled policy: {}", policyId);
        return mapToPolicyResponse(policy);
    }
    
    @Override
    public InsurancePolicyResponse renewPolicy(Long policyId) {
        log.info("Renewing policy: {}", policyId);
        
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new RuntimeException("Policy not found with ID: " + policyId));
        
        LocalDate newExpirationDate = policy.getExpirationDate().plusYears(1);
        policy.setExpirationDate(newExpirationDate);
        policy.setStatus(InsurancePolicy.PolicyStatus.ACTIVE);
        policy.setUpdatedAt(LocalDateTime.now());
        
        policy = policyRepository.save(policy);
        
        log.info("Renewed policy: {} until: {}", policyId, newExpirationDate);
        return mapToPolicyResponse(policy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InsurancePolicyResponse> getPoliciesDueForRenewal(int daysAhead) {
        log.debug("Retrieving policies due for renewal in {} days", daysAhead);
        LocalDate currentDate = LocalDate.now();
        LocalDate renewalDate = currentDate.plusDays(daysAhead);
        
        return policyRepository.findPoliciesDueForRenewal(currentDate, renewalDate)
            .stream()
            .map(this::mapToPolicyResponse)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InsurancePolicyResponse> getExpiredPolicies() {
        log.debug("Retrieving expired policies");
        return policyRepository.findExpiredPolicies(LocalDate.now())
            .stream()
            .map(this::mapToPolicyResponse)
            .toList();
    }
    
    // Claim operations implementation omitted for brevity
    // ... (would implement all claim-related methods similarly)
    
    @Override
    public InsuranceClaimResponse createClaim(CreateClaimRequest request) {
        log.info("Creating insurance claim for policy: {}", request.getPolicyId());
        
        InsurancePolicy policy = policyRepository.findById(request.getPolicyId())
            .orElseThrow(() -> new RuntimeException("Policy not found: " + request.getPolicyId()));
        
        InsuranceClaim claim = InsuranceClaim.builder()
            .insurancePolicy(policy)
            .incidentType(request.getIncidentType())
            .incidentDate(request.getIncidentDate())
            .incidentDescription(request.getIncidentDescription())
            .claimedAmount(request.getClaimedAmount())
            .status(InsuranceClaim.ClaimStatus.SUBMITTED)
            .submittedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
        
        claim = claimRepository.save(claim);
        
        log.info("Created insurance claim: {} for policy: {}", claim.getClaimNumber(), request.getPolicyId());
        return mapToClaimResponse(claim);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InsuranceClaimResponse getClaimById(Long claimId) {
        log.debug("Retrieving claim by ID: {}", claimId);
        InsuranceClaim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new RuntimeException("Claim not found with ID: " + claimId));
        return mapToClaimResponse(claim);
    }
    
    // Additional methods implementation omitted for brevity
    // ... (would implement all remaining interface methods)
    
    @Override
    @Transactional(readOnly = true)
    public List<CoveragePlanResponse> getActiveCoveragePlans() {
        log.debug("Retrieving active coverage plans");
        return coveragePlanRepository.findByIsActiveTrueOrderByPremiumRateAsc()
            .stream()
            .map(this::mapToCoveragePlanResponse)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public CoveragePlanResponse getCoveragePlanById(Long planId) {
        log.debug("Retrieving coverage plan by ID: {}", planId);
        CoveragePlan plan = coveragePlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Coverage plan not found with ID: " + planId));
        return mapToCoveragePlanResponse(plan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePremium(Long planId, BigDecimal coverageAmount) {
        log.debug("Calculating premium for plan: {}, coverage: {}", planId, coverageAmount);
        
        CoveragePlan plan = coveragePlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Coverage plan not found with ID: " + planId));
        
        // Base premium + (coverage amount * premium rate)
        BigDecimal premiumRate = plan.getPremiumRate().divide(BigDecimal.valueOf(100)); // Convert percentage
        BigDecimal calculatedPremium = plan.getBasePremium()
            .add(coverageAmount.multiply(premiumRate));
        
        return calculatedPremium.setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInsuranceStatistics() {
        log.debug("Generating insurance statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Policy statistics
        long totalPolicies = policyRepository.count();
        long activePolicies = policyRepository.countByStatus(InsurancePolicy.PolicyStatus.ACTIVE);
        
        stats.put("totalPolicies", totalPolicies);
        stats.put("activePolicies", activePolicies);
        
        // Claim statistics
        long totalClaims = claimRepository.count();
        BigDecimal totalPaidClaims = claimRepository.getTotalPaidAmount();
        
        stats.put("totalClaims", totalClaims);
        stats.put("totalPaidClaims", totalPaidClaims != null ? totalPaidClaims : BigDecimal.ZERO);
        
        return stats;
    }
    
    // Additional interface methods would be implemented here...
    // For brevity, showing key mapping methods
    
    private InsurancePolicyResponse mapToPolicyResponse(InsurancePolicy policy) {
        return InsurancePolicyResponse.builder()
            .id(policy.getId())
            .policyNumber(policy.getPolicyNumber())
            .customerId(policy.getCustomerId())
            .customerName(policy.getCustomerName())
            .customerEmail(policy.getCustomerEmail())
            .storageUnitId(policy.getStorageUnitId())
            .storageUnitNumber(policy.getStorageUnitNumber())
            .coveragePlan(mapToCoveragePlanSummary(policy.getCoveragePlan()))
            .coverageAmount(policy.getCoverageAmount())
            .monthlyPremium(policy.getMonthlyPremium())
            .deductible(policy.getDeductible())
            .status(policy.getStatus())
            .effectiveDate(policy.getEffectiveDate())
            .expirationDate(policy.getExpirationDate())
            .createdAt(policy.getCreatedAt())
            .updatedAt(policy.getUpdatedAt())
            .build();
    }
    
    private InsuranceClaimResponse mapToClaimResponse(InsuranceClaim claim) {
        return InsuranceClaimResponse.builder()
            .id(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .policy(mapToPolicySummary(claim.getInsurancePolicy()))
            .incidentType(claim.getIncidentType())
            .incidentDate(claim.getIncidentDate())
            .incidentDescription(claim.getIncidentDescription())
            .claimedAmount(claim.getClaimedAmount())
            .assessedAmount(claim.getAssessedAmount())
            .approvedAmount(claim.getApprovedAmount())
            .paidAmount(claim.getPaidAmount())
            .status(claim.getStatus())
            .submittedAt(claim.getSubmittedAt())
            .createdAt(claim.getCreatedAt())
            .build();
    }
    
    private CoveragePlanResponse mapToCoveragePlanResponse(CoveragePlan plan) {
        return CoveragePlanResponse.builder()
            .id(plan.getId())
            .planCode(plan.getPlanCode())
            .planName(plan.getPlanName())
            .description(plan.getDescription())
            .coverageType(plan.getCoverageType())
            .maxCoverageAmount(plan.getMaxCoverageAmount())
            .minCoverageAmount(plan.getMinCoverageAmount())
            .premiumRate(plan.getPremiumRate())
            .basePremium(plan.getBasePremium())
            .deductibleAmount(plan.getDeductibleAmount())
            .isActive(plan.getIsActive())
            .features(plan.getFeatures())
            .exclusions(plan.getExclusions())
            .createdAt(plan.getCreatedAt())
            .updatedAt(plan.getUpdatedAt())
            .build();
    }
    
    private InsurancePolicyResponse.CoveragePlanSummary mapToCoveragePlanSummary(CoveragePlan plan) {
        return InsurancePolicyResponse.CoveragePlanSummary.builder()
            .id(plan.getId())
            .planCode(plan.getPlanCode())
            .planName(plan.getPlanName())
            .coverageType(plan.getCoverageType().name())
            .maxCoverageAmount(plan.getMaxCoverageAmount())
            .premiumRate(plan.getPremiumRate())
            .build();
    }
    
    private InsuranceClaimResponse.PolicySummary mapToPolicySummary(InsurancePolicy policy) {
        return InsuranceClaimResponse.PolicySummary.builder()
            .id(policy.getId())
            .policyNumber(policy.getPolicyNumber())
            .customerName(policy.getCustomerName())
            .storageUnitNumber(policy.getStorageUnitNumber())
            .coverageAmount(policy.getCoverageAmount())
            .deductible(policy.getDeductible())
            .build();
    }
    
    // Placeholder implementations for remaining interface methods
    @Override public InsuranceClaimResponse getClaimByNumber(String claimNumber) { return null; }
    @Override public Page<InsuranceClaimResponse> getPolicyClaims(Long policyId, Pageable pageable) { return null; }
    @Override public Page<InsuranceClaimResponse> getCustomerClaims(Long customerId, Pageable pageable) { return null; }
    @Override public InsuranceClaimResponse updateClaimStatus(Long claimId, InsuranceClaim.ClaimStatus status) { return null; }
    @Override public InsuranceClaimResponse assignClaimToAssessor(Long claimId, Long assessorId, String assessorName) { return null; }
    @Override public InsuranceClaimResponse assessClaim(Long claimId, BigDecimal assessedAmount, String notes) { return null; }
    @Override public InsuranceClaimResponse approveClaim(Long claimId, BigDecimal approvedAmount, Long approverId, String approverName, String notes) { return null; }
    @Override public InsuranceClaimResponse rejectClaim(Long claimId, String reason) { return null; }
    @Override public InsuranceClaimResponse payClaim(Long claimId, BigDecimal paidAmount) { return null; }
    @Override public List<InsuranceClaimResponse> getPendingClaims() { return null; }
    @Override public List<InsuranceClaimResponse> getOverdueClaims(int daysOverdue) { return null; }
    @Override public List<InsuranceClaimResponse> getHighValueClaims(BigDecimal threshold) { return null; }
    @Override public Map<String, Object> getPolicyStatistics() { return null; }
    @Override public Map<String, Object> getClaimStatistics() { return null; }
    @Override public Map<String, Object> getCustomerInsuranceSummary(Long customerId) { return null; }
}