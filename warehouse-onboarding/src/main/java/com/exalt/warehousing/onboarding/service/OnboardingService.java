package com.exalt.warehousing.onboarding.service;

import com.exalt.warehousing.onboarding.dto.OnboardingRequestDTO;
import com.exalt.warehousing.onboarding.dto.OnboardingStatusUpdateDTO;
import com.exalt.warehousing.onboarding.model.OnboardingStatus;
import com.exalt.warehousing.onboarding.model.PartnerOnboardingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Onboarding Service Interface
 * 
 * Provides comprehensive warehouse partner onboarding services
 */
public interface OnboardingService {

    /**
     * Submit a new onboarding request
     */
    PartnerOnboardingRequest submitOnboardingRequest(OnboardingRequestDTO requestDTO, String submittedBy);

    /**
     * Get onboarding request by ID
     */
    PartnerOnboardingRequest getOnboardingRequest(String requestId);

    /**
     * Get onboarding request with full details including documents and verifications
     */
    PartnerOnboardingRequest getOnboardingRequestWithDetails(String requestId);

    /**
     * Update onboarding request status
     */
    PartnerOnboardingRequest updateOnboardingStatus(String requestId, OnboardingStatusUpdateDTO statusUpdate, String updatedBy);

    /**
     * Get onboarding requests by status
     */
    List<PartnerOnboardingRequest> getOnboardingRequestsByStatus(OnboardingStatus status);

    /**
     * Get onboarding requests with filtering and pagination
     */
    Page<PartnerOnboardingRequest> getOnboardingRequests(OnboardingStatus status, String country, 
                                                         String companyName, Pageable pageable);

    /**
     * Get onboarding requests requiring attention
     */
    List<PartnerOnboardingRequest> getRequestsRequiringAttention();

    /**
     * Get expired onboarding requests
     */
    List<PartnerOnboardingRequest> getExpiredRequests(int daysOld);

    /**
     * Approve onboarding request
     */
    PartnerOnboardingRequest approveOnboardingRequest(String requestId, String approvedBy, String notes);

    /**
     * Reject onboarding request
     */
    PartnerOnboardingRequest rejectOnboardingRequest(String requestId, String rejectedBy, String reason);

    /**
     * Suspend onboarding request
     */
    PartnerOnboardingRequest suspendOnboardingRequest(String requestId, String suspendedBy, String reason);

    /**
     * Resume suspended onboarding request
     */
    PartnerOnboardingRequest resumeOnboardingRequest(String requestId, String resumedBy);

    /**
     * Withdraw onboarding request
     */
    PartnerOnboardingRequest withdrawOnboardingRequest(String requestId, String withdrawnBy);

    /**
     * Start KYC verification process
     */
    void startKYCVerification(String requestId, String initiatedBy);

    /**
     * Start compliance checking process
     */
    void startComplianceChecking(String requestId, String initiatedBy);

    /**
     * Schedule facility inspection
     */
    void scheduleFacilityInspection(String requestId, LocalDateTime inspectionDate, String scheduledBy);

    /**
     * Complete facility inspection
     */
    void completeFacilityInspection(String requestId, boolean passed, String inspectorNotes, String completedBy);

    /**
     * Get onboarding statistics
     */
    Map<String, Object> getOnboardingStatistics();

    /**
     * Get onboarding statistics by date range
     */
    Map<String, Object> getOnboardingStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get recently approved partners
     */
    List<PartnerOnboardingRequest> getRecentlyApprovedPartners(int days);

    /**
     * Validate business registration uniqueness
     */
    boolean isBusinessRegistrationUnique(String businessRegistrationNumber);

    /**
     * Validate tax identification uniqueness
     */
    boolean isTaxIdentificationUnique(String taxIdentificationNumber);

    /**
     * Validate contact email uniqueness
     */
    boolean isContactEmailUnique(String email);

    /**
     * Process automatic expiry of old requests
     */
    void processExpiredRequests();

    /**
     * Send onboarding status notifications
     */
    void sendStatusNotification(String requestId, OnboardingStatus newStatus, String additionalMessage);

    /**
     * Generate onboarding report
     */
    byte[] generateOnboardingReport(String requestId);

    /**
     * Export onboarding requests to CSV
     */
    byte[] exportOnboardingRequestsToCSV(OnboardingStatus status, LocalDateTime startDate, LocalDateTime endDate);
}