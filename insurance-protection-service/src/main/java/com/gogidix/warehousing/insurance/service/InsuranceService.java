package com.gogidix.warehousing.insurance.service;

import com.gogidix.warehousing.insurance.dto.*;
import com.gogidix.warehousing.insurance.entity.InsuranceClaim;
import com.gogidix.warehousing.insurance.entity.InsurancePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Insurance Service Interface
 * 
 * Business logic for insurance operations including policies and claims.
 */
public interface InsuranceService {
    
    // Policy Operations
    
    /**
     * Create a new insurance policy
     */
    InsurancePolicyResponse createPolicy(CreatePolicyRequest request);
    
    /**
     * Get policy by ID
     */
    InsurancePolicyResponse getPolicyById(Long policyId);
    
    /**
     * Get policy by policy number
     */
    InsurancePolicyResponse getPolicyByNumber(String policyNumber);
    
    /**
     * Get customer policies
     */
    Page<InsurancePolicyResponse> getCustomerPolicies(Long customerId, Pageable pageable);
    
    /**
     * Get policy for storage unit
     */
    InsurancePolicyResponse getPolicyByStorageUnit(Long storageUnitId);
    
    /**
     * Update policy status
     */
    InsurancePolicyResponse updatePolicyStatus(Long policyId, InsurancePolicy.PolicyStatus status);
    
    /**
     * Cancel policy
     */
    InsurancePolicyResponse cancelPolicy(Long policyId, String reason);
    
    /**
     * Renew policy
     */
    InsurancePolicyResponse renewPolicy(Long policyId);
    
    /**
     * Get policies due for renewal
     */
    List<InsurancePolicyResponse> getPoliciesDueForRenewal(int daysAhead);
    
    /**
     * Get expired policies
     */
    List<InsurancePolicyResponse> getExpiredPolicies();
    
    // Claim Operations
    
    /**
     * Create a new insurance claim
     */
    InsuranceClaimResponse createClaim(CreateClaimRequest request);
    
    /**
     * Get claim by ID
     */
    InsuranceClaimResponse getClaimById(Long claimId);
    
    /**
     * Get claim by claim number
     */
    InsuranceClaimResponse getClaimByNumber(String claimNumber);
    
    /**
     * Get policy claims
     */
    Page<InsuranceClaimResponse> getPolicyClaims(Long policyId, Pageable pageable);
    
    /**
     * Get customer claims
     */
    Page<InsuranceClaimResponse> getCustomerClaims(Long customerId, Pageable pageable);
    
    /**
     * Update claim status
     */
    InsuranceClaimResponse updateClaimStatus(Long claimId, InsuranceClaim.ClaimStatus status);
    
    /**
     * Assign claim to assessor
     */
    InsuranceClaimResponse assignClaimToAssessor(Long claimId, Long assessorId, String assessorName);
    
    /**
     * Assess claim
     */
    InsuranceClaimResponse assessClaim(Long claimId, BigDecimal assessedAmount, String notes);
    
    /**
     * Approve claim
     */
    InsuranceClaimResponse approveClaim(Long claimId, BigDecimal approvedAmount, Long approverId, String approverName, String notes);
    
    /**
     * Reject claim
     */
    InsuranceClaimResponse rejectClaim(Long claimId, String reason);
    
    /**
     * Pay claim
     */
    InsuranceClaimResponse payClaim(Long claimId, BigDecimal paidAmount);
    
    /**
     * Get pending claims
     */
    List<InsuranceClaimResponse> getPendingClaims();
    
    /**
     * Get overdue claims
     */
    List<InsuranceClaimResponse> getOverdueClaims(int daysOverdue);
    
    /**
     * Get high-value claims
     */
    List<InsuranceClaimResponse> getHighValueClaims(BigDecimal threshold);
    
    // Coverage Plan Operations
    
    /**
     * Get all active coverage plans
     */
    List<CoveragePlanResponse> getActiveCoveragePlans();
    
    /**
     * Get coverage plan by ID
     */
    CoveragePlanResponse getCoveragePlanById(Long planId);
    
    /**
     * Calculate premium for coverage
     */
    BigDecimal calculatePremium(Long planId, BigDecimal coverageAmount);
    
    // Statistics and Reports
    
    /**
     * Get insurance statistics
     */
    Map<String, Object> getInsuranceStatistics();
    
    /**
     * Get policy statistics
     */
    Map<String, Object> getPolicyStatistics();
    
    /**
     * Get claim statistics
     */
    Map<String, Object> getClaimStatistics();
    
    /**
     * Get customer insurance summary
     */
    Map<String, Object> getCustomerInsuranceSummary(Long customerId);
}