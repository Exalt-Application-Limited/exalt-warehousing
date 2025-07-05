package com.gogidix.warehousing.onboarding.controller;

import com.gogidix.warehousing.onboarding.dto.OnboardingRequestDTO;
import com.gogidix.warehousing.onboarding.model.OnboardingStatus;
import com.gogidix.warehousing.onboarding.model.PartnerOnboardingRequest;
import com.gogidix.warehousing.onboarding.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Warehouse Partner Onboarding
 * 
 * Provides endpoints for managing warehouse partner onboarding process
 */
@RestController
@RequestMapping("/api/v1/warehousing/onboarding")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouse Onboarding", description = "Warehouse Partner Onboarding Management API")
public class OnboardingController {

    private final OnboardingService onboardingService;

    /**
     * Submit new onboarding request
     */
    @PostMapping("/requests")
    @Operation(summary = "Submit new onboarding request", description = "Submit a new warehouse partner onboarding request")
    public ResponseEntity<PartnerOnboardingRequest> submitOnboardingRequest(
            @Valid @RequestBody OnboardingRequestDTO requestDTO,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Submitting new onboarding request for company: {}", requestDTO.getCompanyName());
        PartnerOnboardingRequest request = onboardingService.submitOnboardingRequest(requestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    /**
     * Get onboarding request by ID
     */
    @GetMapping("/requests/{requestId}")
    @Operation(summary = "Get onboarding request", description = "Retrieve onboarding request by ID")
    @PreAuthorize("hasRole('ONBOARDING_VIEWER') or hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<PartnerOnboardingRequest> getOnboardingRequest(
            @PathVariable String requestId) {
        
        log.info("Retrieving onboarding request: {}", requestId);
        PartnerOnboardingRequest request = onboardingService.getOnboardingRequest(requestId);
        return ResponseEntity.ok(request);
    }

    /**
     * Get onboarding request with full details
     */
    @GetMapping("/requests/{requestId}/details")
    @Operation(summary = "Get detailed onboarding request", description = "Retrieve onboarding request with full details including documents and verifications")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<PartnerOnboardingRequest> getOnboardingRequestWithDetails(
            @PathVariable String requestId) {
        
        log.info("Retrieving detailed onboarding request: {}", requestId);
        PartnerOnboardingRequest request = onboardingService.getOnboardingRequestWithDetails(requestId);
        return ResponseEntity.ok(request);
    }

    /**
     * Get onboarding requests with filtering and pagination
     */
    @GetMapping("/requests")
    @Operation(summary = "Get onboarding requests", description = "Retrieve onboarding requests with filtering and pagination")
    @PreAuthorize("hasRole('ONBOARDING_VIEWER') or hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<Page<PartnerOnboardingRequest>> getOnboardingRequests(
            @Parameter(description = "Filter by status") @RequestParam(required = false) OnboardingStatus status,
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by company name") @RequestParam(required = false) String companyName,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        log.info("Retrieving onboarding requests with status: {}, country: {}, page: {}, size: {}", 
                status, country, page, size);
        
        Page<PartnerOnboardingRequest> requests = onboardingService.getOnboardingRequests(
                status, country, companyName, pageable);
        return ResponseEntity.ok(requests);
    }

    /**
     * Get requests requiring attention
     */
    @GetMapping("/requests/attention-required")
    @Operation(summary = "Get requests requiring attention", description = "Retrieve onboarding requests that require manual attention")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<List<PartnerOnboardingRequest>> getRequestsRequiringAttention() {
        
        log.info("Retrieving requests requiring attention");
        List<PartnerOnboardingRequest> requests = onboardingService.getRequestsRequiringAttention();
        return ResponseEntity.ok(requests);
    }

    /**
     * Approve onboarding request
     */
    @PostMapping("/requests/{requestId}/approve")
    @Operation(summary = "Approve onboarding request", description = "Approve a warehouse partner onboarding request")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<PartnerOnboardingRequest> approveOnboardingRequest(
            @PathVariable String requestId,
            @RequestParam(required = false) String notes,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Approving onboarding request: {} by user: {}", requestId, userId);
        PartnerOnboardingRequest request = onboardingService.approveOnboardingRequest(requestId, userId, notes);
        return ResponseEntity.ok(request);
    }

    /**
     * Reject onboarding request
     */
    @PostMapping("/requests/{requestId}/reject")
    @Operation(summary = "Reject onboarding request", description = "Reject a warehouse partner onboarding request")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<PartnerOnboardingRequest> rejectOnboardingRequest(
            @PathVariable String requestId,
            @RequestParam String reason,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Rejecting onboarding request: {} by user: {} with reason: {}", requestId, userId, reason);
        PartnerOnboardingRequest request = onboardingService.rejectOnboardingRequest(requestId, userId, reason);
        return ResponseEntity.ok(request);
    }

    /**
     * Suspend onboarding request
     */
    @PostMapping("/requests/{requestId}/suspend")
    @Operation(summary = "Suspend onboarding request", description = "Suspend a warehouse partner onboarding request")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<PartnerOnboardingRequest> suspendOnboardingRequest(
            @PathVariable String requestId,
            @RequestParam String reason,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Suspending onboarding request: {} by user: {} with reason: {}", requestId, userId, reason);
        PartnerOnboardingRequest request = onboardingService.suspendOnboardingRequest(requestId, userId, reason);
        return ResponseEntity.ok(request);
    }

    /**
     * Resume suspended onboarding request
     */
    @PostMapping("/requests/{requestId}/resume")
    @Operation(summary = "Resume onboarding request", description = "Resume a suspended warehouse partner onboarding request")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<PartnerOnboardingRequest> resumeOnboardingRequest(
            @PathVariable String requestId,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Resuming onboarding request: {} by user: {}", requestId, userId);
        PartnerOnboardingRequest request = onboardingService.resumeOnboardingRequest(requestId, userId);
        return ResponseEntity.ok(request);
    }

    /**
     * Start KYC verification
     */
    @PostMapping("/requests/{requestId}/kyc/start")
    @Operation(summary = "Start KYC verification", description = "Start KYC verification process for onboarding request")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<Void> startKYCVerification(
            @PathVariable String requestId,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Starting KYC verification for request: {} by user: {}", requestId, userId);
        onboardingService.startKYCVerification(requestId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Start compliance checking
     */
    @PostMapping("/requests/{requestId}/compliance/start")
    @Operation(summary = "Start compliance checking", description = "Start compliance checking process for onboarding request")
    @PreAuthorize("hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<Void> startComplianceChecking(
            @PathVariable String requestId,
            @RequestHeader("X-User-ID") String userId) {
        
        log.info("Starting compliance checking for request: {} by user: {}", requestId, userId);
        onboardingService.startComplianceChecking(requestId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get onboarding statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get onboarding statistics", description = "Retrieve onboarding statistics and metrics")
    @PreAuthorize("hasRole('ONBOARDING_VIEWER') or hasRole('ONBOARDING_ADMIN')")
    public ResponseEntity<Map<String, Object>> getOnboardingStatistics() {
        
        log.info("Retrieving onboarding statistics");
        Map<String, Object> statistics = onboardingService.getOnboardingStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Validate business registration uniqueness
     */
    @GetMapping("/validate/business-registration")
    @Operation(summary = "Validate business registration", description = "Check if business registration number is unique")
    public ResponseEntity<Map<String, Boolean>> validateBusinessRegistration(
            @RequestParam String businessRegistrationNumber) {
        
        boolean isUnique = onboardingService.isBusinessRegistrationUnique(businessRegistrationNumber);
        Map<String, Boolean> response = Map.of("isUnique", isUnique);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate tax identification uniqueness
     */
    @GetMapping("/validate/tax-identification")
    @Operation(summary = "Validate tax identification", description = "Check if tax identification number is unique")
    public ResponseEntity<Map<String, Boolean>> validateTaxIdentification(
            @RequestParam String taxIdentificationNumber) {
        
        boolean isUnique = onboardingService.isTaxIdentificationUnique(taxIdentificationNumber);
        Map<String, Boolean> response = Map.of("isUnique", isUnique);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate contact email uniqueness
     */
    @GetMapping("/validate/contact-email")
    @Operation(summary = "Validate contact email", description = "Check if contact email is unique")
    public ResponseEntity<Map<String, Boolean>> validateContactEmail(
            @RequestParam String email) {
        
        boolean isUnique = onboardingService.isContactEmailUnique(email);
        Map<String, Boolean> response = Map.of("isUnique", isUnique);
        return ResponseEntity.ok(response);
    }
}