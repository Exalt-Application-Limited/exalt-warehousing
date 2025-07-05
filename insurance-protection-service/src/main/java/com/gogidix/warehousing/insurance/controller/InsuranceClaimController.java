package com.gogidix.warehousing.insurance.controller;

import com.gogidix.warehousing.insurance.dto.*;
import com.gogidix.warehousing.insurance.entity.InsuranceClaim;
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
 * Insurance Claim Controller
 * 
 * REST API endpoints for insurance claim management.
 */
@RestController
@RequestMapping("/api/v1/insurance/claims")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Insurance Claims", description = "Insurance claim management APIs")
public class InsuranceClaimController {
    
    private final InsuranceService insuranceService;
    
    @Operation(summary = "Create new insurance claim", 
               description = "File a new insurance claim for a policy")
    @PostMapping
    public ResponseEntity<InsuranceClaimResponse> createClaim(
            @Valid @RequestBody CreateClaimRequest request) {
        
        log.info("Creating new insurance claim for policy: {}", request.getPolicyId());
        InsuranceClaimResponse response = insuranceService.createClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Get claim by ID", 
               description = "Retrieve an insurance claim by its ID")
    @GetMapping("/{claimId}")
    public ResponseEntity<InsuranceClaimResponse> getClaimById(
            @Parameter(description = "Claim ID") @PathVariable Long claimId) {
        
        log.debug("Retrieving claim by ID: {}", claimId);
        InsuranceClaimResponse response = insuranceService.getClaimById(claimId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get pending claims", 
               description = "Get all pending insurance claims")
    @GetMapping("/pending")
    public ResponseEntity<List<InsuranceClaimResponse>> getPendingClaims() {
        
        log.debug("Retrieving pending claims");
        List<InsuranceClaimResponse> response = insuranceService.getPendingClaims();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get overdue claims", 
               description = "Get claims that are overdue for processing")
    @GetMapping("/overdue")
    public ResponseEntity<List<InsuranceClaimResponse>> getOverdueClaims(
            @Parameter(description = "Days overdue") @RequestParam(defaultValue = "7") int daysOverdue) {
        
        log.debug("Retrieving overdue claims (older than {} days)", daysOverdue);
        List<InsuranceClaimResponse> response = insuranceService.getOverdueClaims(daysOverdue);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get high-value claims", 
               description = "Get claims above a specified threshold amount")
    @GetMapping("/high-value")
    public ResponseEntity<List<InsuranceClaimResponse>> getHighValueClaims(
            @Parameter(description = "Minimum claim amount") @RequestParam BigDecimal threshold) {
        
        log.debug("Retrieving high-value claims above: {}", threshold);
        List<InsuranceClaimResponse> response = insuranceService.getHighValueClaims(threshold);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Update claim status", 
               description = "Update the status of an insurance claim")
    @PutMapping("/{claimId}/status")
    public ResponseEntity<InsuranceClaimResponse> updateClaimStatus(
            @Parameter(description = "Claim ID") @PathVariable Long claimId,
            @Parameter(description = "New status") @RequestParam InsuranceClaim.ClaimStatus status) {
        
        log.info("Updating claim {} status to: {}", claimId, status);
        InsuranceClaimResponse response = insuranceService.updateClaimStatus(claimId, status);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Assign claim to assessor", 
               description = "Assign a claim to a specific assessor")
    @PutMapping("/{claimId}/assign")
    public ResponseEntity<InsuranceClaimResponse> assignClaimToAssessor(
            @Parameter(description = "Claim ID") @PathVariable Long claimId,
            @Parameter(description = "Assessor ID") @RequestParam Long assessorId,
            @Parameter(description = "Assessor name") @RequestParam String assessorName) {
        
        log.info("Assigning claim {} to assessor: {} ({})", claimId, assessorName, assessorId);
        InsuranceClaimResponse response = insuranceService.assignClaimToAssessor(claimId, assessorId, assessorName);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Assess claim", 
               description = "Complete assessment of a claim with assessed amount")
    @PutMapping("/{claimId}/assess")
    public ResponseEntity<InsuranceClaimResponse> assessClaim(
            @Parameter(description = "Claim ID") @PathVariable Long claimId,
            @Parameter(description = "Assessed amount") @RequestParam BigDecimal assessedAmount,
            @Parameter(description = "Assessment notes") @RequestParam String notes) {
        
        log.info("Assessing claim {} with amount: {}", claimId, assessedAmount);
        InsuranceClaimResponse response = insuranceService.assessClaim(claimId, assessedAmount, notes);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Approve claim", 
               description = "Approve a claim for payment")
    @PutMapping("/{claimId}/approve")
    public ResponseEntity<InsuranceClaimResponse> approveClaim(
            @Parameter(description = "Claim ID") @PathVariable Long claimId,
            @Parameter(description = "Approved amount") @RequestParam BigDecimal approvedAmount,
            @Parameter(description = "Approver ID") @RequestParam Long approverId,
            @Parameter(description = "Approver name") @RequestParam String approverName,
            @Parameter(description = "Approval notes") @RequestParam String notes) {
        
        log.info("Approving claim {} for amount: {} by: {}", claimId, approvedAmount, approverName);
        InsuranceClaimResponse response = insuranceService.approveClaim(claimId, approvedAmount, approverId, approverName, notes);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Reject claim", 
               description = "Reject a claim with reason")
    @PutMapping("/{claimId}/reject")
    public ResponseEntity<InsuranceClaimResponse> rejectClaim(
            @Parameter(description = "Claim ID") @PathVariable Long claimId,
            @Parameter(description = "Rejection reason") @RequestParam String reason) {
        
        log.info("Rejecting claim {} with reason: {}", claimId, reason);
        InsuranceClaimResponse response = insuranceService.rejectClaim(claimId, reason);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Pay claim", 
               description = "Process payment for an approved claim")
    @PutMapping("/{claimId}/pay")
    public ResponseEntity<InsuranceClaimResponse> payClaim(
            @Parameter(description = "Claim ID") @PathVariable Long claimId,
            @Parameter(description = "Payment amount") @RequestParam BigDecimal paidAmount) {
        
        log.info("Processing payment for claim {} amount: {}", claimId, paidAmount);
        InsuranceClaimResponse response = insuranceService.payClaim(claimId, paidAmount);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get insurance statistics", 
               description = "Get comprehensive insurance and claim statistics")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getInsuranceStatistics() {
        
        log.debug("Generating insurance statistics");
        Map<String, Object> response = insuranceService.getInsuranceStatistics();
        return ResponseEntity.ok(response);
    }
}