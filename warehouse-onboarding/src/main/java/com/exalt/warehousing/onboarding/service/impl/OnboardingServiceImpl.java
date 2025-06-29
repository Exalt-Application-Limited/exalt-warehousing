package com.exalt.warehousing.onboarding.service.impl;

import com.exalt.warehousing.onboarding.dto.OnboardingRequestDTO;
import com.exalt.warehousing.onboarding.dto.OnboardingStatusUpdateDTO;
import com.exalt.warehousing.onboarding.model.*;
import com.exalt.warehousing.onboarding.repository.PartnerOnboardingRequestRepository;
import com.exalt.warehousing.onboarding.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Onboarding Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OnboardingServiceImpl implements OnboardingService {

    private final PartnerOnboardingRequestRepository onboardingRepository;

    @Override
    public PartnerOnboardingRequest submitOnboardingRequest(OnboardingRequestDTO requestDTO, String submittedBy) {
        log.info("Processing onboarding request submission for company: {}", requestDTO.getCompanyName());

        // Validate uniqueness
        validateUniqueConstraints(requestDTO);

        // Build the onboarding request
        PartnerOnboardingRequest request = PartnerOnboardingRequest.builder()
                .requestId(generateRequestId())
                .companyName(requestDTO.getCompanyName())
                .legalBusinessName(requestDTO.getLegalBusinessName())
                .businessRegistrationNumber(requestDTO.getBusinessRegistrationNumber())
                .taxIdentificationNumber(requestDTO.getTaxIdentificationNumber())
                .businessType(requestDTO.getBusinessType())
                .countryOfIncorporation(requestDTO.getCountryOfIncorporation())
                .dateOfIncorporation(requestDTO.getDateOfIncorporation())
                .primaryContactName(requestDTO.getPrimaryContactName())
                .primaryContactEmail(requestDTO.getPrimaryContactEmail())
                .primaryContactPhone(requestDTO.getPrimaryContactPhone())
                .businessAddress(requestDTO.getBusinessAddress())
                .city(requestDTO.getCity())
                .state(requestDTO.getState())
                .country(requestDTO.getCountry())
                .postalCode(requestDTO.getPostalCode())
                .totalStorageCapacity(requestDTO.getTotalStorageCapacity())
                .availableStorageCapacity(requestDTO.getAvailableStorageCapacity())
                .supportedStorageTypes(requestDTO.getSupportedStorageTypes())
                .serviceCapabilities(requestDTO.getServiceCapabilities())
                .hasTemperatureControl(requestDTO.getHasTemperatureControl())
                .hasSecuritySystems(requestDTO.getHasSecuritySystems())
                .hasInventoryManagementSystem(requestDTO.getHasInventoryManagementSystem())
                .proposedPricingPerCubicMeter(requestDTO.getProposedPricingPerCubicMeter())
                .minimumOrderValue(requestDTO.getMinimumOrderValue())
                .preferredPaymentTerms(requestDTO.getPreferredPaymentTerms())
                .status(OnboardingStatus.SUBMITTED)
                .kycStatus(KYCStatus.NOT_STARTED)
                .createdBy(submittedBy)
                .updatedBy(submittedBy)
                .build();

        PartnerOnboardingRequest savedRequest = onboardingRepository.save(request);
        log.info("Onboarding request submitted successfully with ID: {}", savedRequest.getRequestId());

        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerOnboardingRequest getOnboardingRequest(String requestId) {
        return onboardingRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Onboarding request not found: " + requestId));
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerOnboardingRequest getOnboardingRequestWithDetails(String requestId) {
        // For now, return same as getOnboardingRequest
        // In a full implementation, this would use JOIN FETCH to load all related entities
        return getOnboardingRequest(requestId);
    }

    @Override
    public PartnerOnboardingRequest updateOnboardingStatus(String requestId, OnboardingStatusUpdateDTO statusUpdate, String updatedBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        // Validate status transition
        validateStatusTransition(request.getStatus(), statusUpdate.getNewStatus());
        
        OnboardingStatus oldStatus = request.getStatus();
        request.setStatus(statusUpdate.getNewStatus());
        request.setUpdatedBy(updatedBy);
        
        if (statusUpdate.getReason() != null) {
            request.setRejectionReason(statusUpdate.getReason());
        }
        
        PartnerOnboardingRequest updatedRequest = onboardingRepository.save(request);
        
        log.info("Onboarding request {} status updated from {} to {} by {}", 
                requestId, oldStatus, statusUpdate.getNewStatus(), updatedBy);
        
        return updatedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerOnboardingRequest> getOnboardingRequestsByStatus(OnboardingStatus status) {
        return onboardingRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartnerOnboardingRequest> getOnboardingRequests(OnboardingStatus status, String country, 
                                                               String companyName, Pageable pageable) {
        return onboardingRepository.findWithFilters(status, null, country, companyName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerOnboardingRequest> getRequestsRequiringAttention() {
        return onboardingRepository.findRequestsRequiringAttention();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerOnboardingRequest> getExpiredRequests(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<OnboardingStatus> pendingStatuses = Arrays.asList(
            OnboardingStatus.SUBMITTED,
            OnboardingStatus.UNDER_REVIEW,
            OnboardingStatus.DOCUMENT_VERIFICATION,
            OnboardingStatus.KYC_IN_PROGRESS,
            OnboardingStatus.COMPLIANCE_CHECK
        );
        return onboardingRepository.findExpiredRequests(pendingStatuses, cutoffDate);
    }

    @Override
    public PartnerOnboardingRequest approveOnboardingRequest(String requestId, String approvedBy, String notes) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (request.getStatus().isTerminal()) {
            throw new RuntimeException("Cannot approve request in terminal status: " + request.getStatus());
        }
        
        request.setStatus(OnboardingStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());
        request.setApprovedBy(approvedBy);
        request.setUpdatedBy(approvedBy);
        
        PartnerOnboardingRequest approvedRequest = onboardingRepository.save(request);
        
        log.info("Onboarding request {} approved by {} at {}", requestId, approvedBy, LocalDateTime.now());
        
        return approvedRequest;
    }

    @Override
    public PartnerOnboardingRequest rejectOnboardingRequest(String requestId, String rejectedBy, String reason) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (request.getStatus().isTerminal()) {
            throw new RuntimeException("Cannot reject request in terminal status: " + request.getStatus());
        }
        
        request.setStatus(OnboardingStatus.REJECTED);
        request.setRejectionReason(reason);
        request.setUpdatedBy(rejectedBy);
        
        PartnerOnboardingRequest rejectedRequest = onboardingRepository.save(request);
        
        log.info("Onboarding request {} rejected by {} with reason: {}", requestId, rejectedBy, reason);
        
        return rejectedRequest;
    }

    @Override
    public PartnerOnboardingRequest suspendOnboardingRequest(String requestId, String suspendedBy, String reason) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (request.getStatus().isTerminal()) {
            throw new RuntimeException("Cannot suspend request in terminal status: " + request.getStatus());
        }
        
        request.setStatus(OnboardingStatus.SUSPENDED);
        request.setRejectionReason(reason);
        request.setUpdatedBy(suspendedBy);
        
        PartnerOnboardingRequest suspendedRequest = onboardingRepository.save(request);
        
        log.info("Onboarding request {} suspended by {} with reason: {}", requestId, suspendedBy, reason);
        
        return suspendedRequest;
    }

    @Override
    public PartnerOnboardingRequest resumeOnboardingRequest(String requestId, String resumedBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (request.getStatus() != OnboardingStatus.SUSPENDED) {
            throw new RuntimeException("Can only resume suspended requests");
        }
        
        request.setStatus(OnboardingStatus.UNDER_REVIEW);
        request.setRejectionReason(null);
        request.setUpdatedBy(resumedBy);
        
        PartnerOnboardingRequest resumedRequest = onboardingRepository.save(request);
        
        log.info("Onboarding request {} resumed by {}", requestId, resumedBy);
        
        return resumedRequest;
    }

    @Override
    public PartnerOnboardingRequest withdrawOnboardingRequest(String requestId, String withdrawnBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (request.getStatus().isTerminal()) {
            throw new RuntimeException("Cannot withdraw request in terminal status: " + request.getStatus());
        }
        
        request.setStatus(OnboardingStatus.WITHDRAWN);
        request.setUpdatedBy(withdrawnBy);
        
        PartnerOnboardingRequest withdrawnRequest = onboardingRepository.save(request);
        
        log.info("Onboarding request {} withdrawn by {}", requestId, withdrawnBy);
        
        return withdrawnRequest;
    }

    @Override
    public void startKYCVerification(String requestId, String initiatedBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (request.getKycStatus() != KYCStatus.NOT_STARTED) {
            throw new RuntimeException("KYC verification already started or completed");
        }
        
        request.setKycStatus(KYCStatus.PENDING);
        request.setStatus(OnboardingStatus.KYC_IN_PROGRESS);
        request.setUpdatedBy(initiatedBy);
        
        onboardingRepository.save(request);
        
        log.info("KYC verification started for request {} by {}", requestId, initiatedBy);
    }

    @Override
    public void startComplianceChecking(String requestId, String initiatedBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        request.setStatus(OnboardingStatus.COMPLIANCE_CHECK);
        request.setUpdatedBy(initiatedBy);
        
        onboardingRepository.save(request);
        
        log.info("Compliance checking started for request {} by {}", requestId, initiatedBy);
    }

    @Override
    public void scheduleFacilityInspection(String requestId, LocalDateTime inspectionDate, String scheduledBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        request.setStatus(OnboardingStatus.FACILITY_INSPECTION_SCHEDULED);
        request.setUpdatedBy(scheduledBy);
        
        onboardingRepository.save(request);
        
        log.info("Facility inspection scheduled for request {} on {} by {}", requestId, inspectionDate, scheduledBy);
    }

    @Override
    public void completeFacilityInspection(String requestId, boolean passed, String inspectorNotes, String completedBy) {
        PartnerOnboardingRequest request = getOnboardingRequest(requestId);
        
        if (passed) {
            request.setStatus(OnboardingStatus.FACILITY_INSPECTION_COMPLETED);
        } else {
            request.setStatus(OnboardingStatus.REJECTED);
            request.setRejectionReason("Facility inspection failed: " + inspectorNotes);
        }
        
        request.setUpdatedBy(completedBy);
        
        onboardingRepository.save(request);
        
        log.info("Facility inspection completed for request {} with result: {} by {}", 
                requestId, passed ? "PASSED" : "FAILED", completedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOnboardingStatistics() {
        List<Object[]> statusCounts = onboardingRepository.countByStatus();
        List<Object[]> kycStatusCounts = onboardingRepository.countByKycStatus();
        
        Map<String, Long> statusDistribution = statusCounts.stream()
                .collect(Collectors.toMap(
                    row -> row[0].toString(),
                    row -> (Long) row[1]
                ));
        
        Map<String, Long> kycStatusDistribution = kycStatusCounts.stream()
                .collect(Collectors.toMap(
                    row -> row[0].toString(),
                    row -> (Long) row[1]
                ));
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("statusDistribution", statusDistribution);
        statistics.put("kycStatusDistribution", kycStatusDistribution);
        statistics.put("totalRequests", onboardingRepository.count());
        statistics.put("requestsRequiringAttention", getRequestsRequiringAttention().size());
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOnboardingStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<PartnerOnboardingRequest> requests = onboardingRepository.findByCreatedAtBetween(startDate, endDate);
        List<PartnerOnboardingRequest> approved = onboardingRepository.findApprovedBetween(startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRequestsInPeriod", requests.size());
        statistics.put("approvedInPeriod", approved.size());
        statistics.put("approvalRate", requests.size() > 0 ? (double) approved.size() / requests.size() : 0.0);
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerOnboardingRequest> getRecentlyApprovedPartners(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return onboardingRepository.findApprovedBetween(cutoffDate, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBusinessRegistrationUnique(String businessRegistrationNumber) {
        return !onboardingRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTaxIdentificationUnique(String taxIdentificationNumber) {
        return !onboardingRepository.existsByTaxIdentificationNumber(taxIdentificationNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isContactEmailUnique(String email) {
        return !onboardingRepository.existsByPrimaryContactEmail(email);
    }

    @Override
    public void processExpiredRequests() {
        List<PartnerOnboardingRequest> expiredRequests = getExpiredRequests(30);
        
        for (PartnerOnboardingRequest request : expiredRequests) {
            request.setStatus(OnboardingStatus.EXPIRED);
            request.setUpdatedBy("SYSTEM");
            onboardingRepository.save(request);
        }
        
        log.info("Processed {} expired onboarding requests", expiredRequests.size());
    }

    @Override
    public void sendStatusNotification(String requestId, OnboardingStatus newStatus, String additionalMessage) {
        // Placeholder for notification service integration
        log.info("Sending status notification for request {} with status {} and message: {}", 
                requestId, newStatus, additionalMessage);
    }

    @Override
    public byte[] generateOnboardingReport(String requestId) {
        // Placeholder for report generation
        log.info("Generating onboarding report for request: {}", requestId);
        return "Onboarding Report Placeholder".getBytes();
    }

    @Override
    public byte[] exportOnboardingRequestsToCSV(OnboardingStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        // Placeholder for CSV export
        log.info("Exporting onboarding requests to CSV with status: {} between {} and {}", 
                status, startDate, endDate);
        return "CSV Export Placeholder".getBytes();
    }

    // Private helper methods

    private void validateUniqueConstraints(OnboardingRequestDTO requestDTO) {
        if (!isBusinessRegistrationUnique(requestDTO.getBusinessRegistrationNumber())) {
            throw new RuntimeException("Business registration number already exists: " + 
                    requestDTO.getBusinessRegistrationNumber());
        }
        
        if (!isTaxIdentificationUnique(requestDTO.getTaxIdentificationNumber())) {
            throw new RuntimeException("Tax identification number already exists: " + 
                    requestDTO.getTaxIdentificationNumber());
        }
        
        if (!isContactEmailUnique(requestDTO.getPrimaryContactEmail())) {
            throw new RuntimeException("Contact email already exists: " + 
                    requestDTO.getPrimaryContactEmail());
        }
    }

    private void validateStatusTransition(OnboardingStatus currentStatus, OnboardingStatus newStatus) {
        OnboardingStatus[] allowedTransitions = currentStatus.getNextPossibleStatuses();
        boolean isValidTransition = Arrays.asList(allowedTransitions).contains(newStatus);
        
        if (!isValidTransition) {
            throw new RuntimeException(String.format("Invalid status transition from %s to %s", 
                    currentStatus, newStatus));
        }
    }

    private String generateRequestId() {
        return "WH-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", new Random().nextInt(10000));
    }
}