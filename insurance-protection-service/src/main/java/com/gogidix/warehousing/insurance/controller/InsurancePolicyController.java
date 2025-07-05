package com.gogidix.warehousing.insurance.controller;

import com.gogidix.warehousing.insurance.dto.*;
import com.gogidix.warehousing.insurance.entity.InsurancePolicy;
import com.gogidix.warehousing.insurance.service.InsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Insurance Policy Controller
 * 
 * REST API endpoints for insurance policy management.
 */
@RestController
@RequestMapping("/api/v1/insurance/policies")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Insurance Policies", description = "Insurance policy management APIs")
public class InsurancePolicyController {
    
    private final InsuranceService insuranceService;
    
    @Operation(summary = "Create new insurance policy", 
               description = "Create a new insurance policy for a storage unit")
    @PostMapping
    public ResponseEntity<InsurancePolicyResponse> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request) {
        
        log.info("Creating new insurance policy for customer: {}, storage unit: {}", 
                request.getCustomerId(), request.getStorageUnitId());
        InsurancePolicyResponse response = insuranceService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Get policy by ID", 
               description = "Retrieve an insurance policy by its ID")
    @GetMapping("/{policyId}")
    public ResponseEntity<InsurancePolicyResponse> getPolicyById(
            @Parameter(description = "Policy ID") @PathVariable Long policyId) {
        
        log.debug("Retrieving policy by ID: {}", policyId);
        InsurancePolicyResponse response = insuranceService.getPolicyById(policyId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get policy by number", 
               description = "Retrieve an insurance policy by its policy number")
    @GetMapping("/number/{policyNumber}")
    public ResponseEntity<InsurancePolicyResponse> getPolicyByNumber(
            @Parameter(description = "Policy number") @PathVariable String policyNumber) {
        
        log.debug("Retrieving policy by number: {}", policyNumber);
        InsurancePolicyResponse response = insuranceService.getPolicyByNumber(policyNumber);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get customer policies", 
               description = "Get all policies for a specific customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<InsurancePolicyResponse>> getCustomerPolicies(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            Pageable pageable) {
        
        log.debug("Retrieving policies for customer: {}", customerId);
        Page<InsurancePolicyResponse> response = insuranceService.getCustomerPolicies(customerId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get policy by storage unit", 
               description = "Get the insurance policy for a specific storage unit")
    @GetMapping("/storage-unit/{storageUnitId}")
    public ResponseEntity<InsurancePolicyResponse> getPolicyByStorageUnit(
            @Parameter(description = "Storage unit ID") @PathVariable Long storageUnitId) {
        
        log.debug("Retrieving policy for storage unit: {}", storageUnitId);
        InsurancePolicyResponse response = insuranceService.getPolicyByStorageUnit(storageUnitId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update policy status", 
               description = "Update the status of an insurance policy")
    @PutMapping("/{policyId}/status")
    public ResponseEntity<InsurancePolicyResponse> updatePolicyStatus(
            @Parameter(description = "Policy ID") @PathVariable Long policyId,
            @Parameter(description = "New status") @RequestParam InsurancePolicy.PolicyStatus status) {
        
        log.info("Updating policy {} status to: {}", policyId, status);
        InsurancePolicyResponse response = insuranceService.updatePolicyStatus(policyId, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Cancel policy", 
               description = "Cancel an insurance policy with a reason")
    @PutMapping("/{policyId}/cancel")
    public ResponseEntity<InsurancePolicyResponse> cancelPolicy(
            @Parameter(description = "Policy ID") @PathVariable Long policyId,
            @Parameter(description = "Cancellation reason") @RequestParam String reason) {
        
        log.info("Cancelling policy: {} with reason: {}", policyId, reason);
        InsurancePolicyResponse response = insuranceService.cancelPolicy(policyId, reason);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Renew policy", 
               description = "Renew an insurance policy for another year")
    @PutMapping("/{policyId}/renew")
    public ResponseEntity<InsurancePolicyResponse> renewPolicy(
            @Parameter(description = "Policy ID") @PathVariable Long policyId) {
        
        log.info("Renewing policy: {}", policyId);
        InsurancePolicyResponse response = insuranceService.renewPolicy(policyId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get policies due for renewal", 
               description = "Get all policies that are due for renewal within specified days")
    @GetMapping("/due-for-renewal")
    public ResponseEntity<List<InsurancePolicyResponse>> getPoliciesDueForRenewal(
            @Parameter(description = "Days ahead to check") @RequestParam(defaultValue = "30") int daysAhead) {
        
        log.debug("Retrieving policies due for renewal in {} days", daysAhead);
        List<InsurancePolicyResponse> response = insuranceService.getPoliciesDueForRenewal(daysAhead);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get expired policies", 
               description = "Get all policies that have expired")
    @GetMapping("/expired")
    public ResponseEntity<List<InsurancePolicyResponse>> getExpiredPolicies() {
        
        log.debug("Retrieving expired policies");
        List<InsurancePolicyResponse> response = insuranceService.getExpiredPolicies();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Calculate premium", 
               description = "Calculate premium for a coverage plan and amount")
    @GetMapping("/calculate-premium")
    public ResponseEntity<Map<String, Object>> calculatePremium(
            @Parameter(description = "Coverage plan ID") @RequestParam Long planId,
            @Parameter(description = "Coverage amount") @RequestParam BigDecimal coverageAmount) {
        
        log.debug("Calculating premium for plan: {}, coverage: {}", planId, coverageAmount);
        BigDecimal premium = insuranceService.calculatePremium(planId, coverageAmount);
        
        Map<String, Object> response = Map.of(
            "planId", planId,
            "coverageAmount", coverageAmount,
            "monthlyPremium", premium,
            "annualPremium", premium.multiply(BigDecimal.valueOf(12))
        );
        
        return ResponseEntity.ok(response);
    }
}