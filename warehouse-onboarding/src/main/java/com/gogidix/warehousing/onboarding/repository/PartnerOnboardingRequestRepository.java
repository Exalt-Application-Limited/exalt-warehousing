package com.gogidix.warehousing.onboarding.repository;

import com.gogidix.warehousing.onboarding.model.OnboardingStatus;
import com.gogidix.warehousing.onboarding.model.PartnerOnboardingRequest;
import com.gogidix.warehousing.onboarding.model.KYCStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Partner Onboarding Request operations
 */
@Repository
public interface PartnerOnboardingRequestRepository extends JpaRepository<PartnerOnboardingRequest, Long> {

    /**
     * Find onboarding request by request ID
     */
    Optional<PartnerOnboardingRequest> findByRequestId(String requestId);

    /**
     * Find onboarding requests by status
     */
    List<PartnerOnboardingRequest> findByStatus(OnboardingStatus status);

    /**
     * Find onboarding requests by KYC status
     */
    List<PartnerOnboardingRequest> findByKycStatus(KYCStatus kycStatus);

    /**
     * Find onboarding requests by company name (case-insensitive)
     */
    @Query("SELECT p FROM PartnerOnboardingRequest p WHERE LOWER(p.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))")
    List<PartnerOnboardingRequest> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);

    /**
     * Find onboarding requests by business registration number
     */
    Optional<PartnerOnboardingRequest> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    /**
     * Find onboarding requests by tax identification number
     */
    Optional<PartnerOnboardingRequest> findByTaxIdentificationNumber(String taxIdentificationNumber);

    /**
     * Find onboarding requests by primary contact email
     */
    Optional<PartnerOnboardingRequest> findByPrimaryContactEmail(String email);

    /**
     * Find onboarding requests by country
     */
    List<PartnerOnboardingRequest> findByCountry(String country);

    /**
     * Find onboarding requests created within date range
     */
    @Query("SELECT p FROM PartnerOnboardingRequest p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<PartnerOnboardingRequest> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Find expired onboarding requests (submitted more than 30 days ago and still pending)
     */
    @Query("SELECT p FROM PartnerOnboardingRequest p WHERE p.status IN :pendingStatuses AND p.createdAt < :cutoffDate")
    List<PartnerOnboardingRequest> findExpiredRequests(@Param("pendingStatuses") List<OnboardingStatus> pendingStatuses,
                                                       @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find onboarding requests requiring attention (suspended or requiring review)
     */
    @Query("SELECT p FROM PartnerOnboardingRequest p WHERE p.status = 'SUSPENDED' OR p.kycStatus = 'MANUAL_REVIEW'")
    List<PartnerOnboardingRequest> findRequestsRequiringAttention();

    /**
     * Find onboarding requests by workflow instance ID
     */
    Optional<PartnerOnboardingRequest> findByWorkflowInstanceId(String workflowInstanceId);

    /**
     * Count onboarding requests by status
     */
    @Query("SELECT p.status, COUNT(p) FROM PartnerOnboardingRequest p GROUP BY p.status")
    List<Object[]> countByStatus();

    /**
     * Count onboarding requests by KYC status
     */
    @Query("SELECT p.kycStatus, COUNT(p) FROM PartnerOnboardingRequest p GROUP BY p.kycStatus")
    List<Object[]> countByKycStatus();

    /**
     * Find onboarding requests with incomplete document verification
     */
    @Query("SELECT DISTINCT p FROM PartnerOnboardingRequest p " +
           "LEFT JOIN p.documents d " +
           "WHERE d.status != 'VERIFIED' OR d.status IS NULL")
    List<PartnerOnboardingRequest> findWithIncompleteDocuments();

    /**
     * Find onboarding requests with failed KYC verifications
     */
    @Query("SELECT DISTINCT p FROM PartnerOnboardingRequest p " +
           "JOIN p.kycVerifications k " +
           "WHERE k.status = 'FAILED'")
    List<PartnerOnboardingRequest> findWithFailedKycVerifications();

    /**
     * Find onboarding requests with non-compliant checks
     */
    @Query("SELECT DISTINCT p FROM PartnerOnboardingRequest p " +
           "JOIN p.complianceChecks c " +
           "WHERE c.isCompliant = false OR c.status = 'FAILED'")
    List<PartnerOnboardingRequest> findWithNonCompliantChecks();

    /**
     * Find onboarding requests approved within date range
     */
    @Query("SELECT p FROM PartnerOnboardingRequest p WHERE p.status = 'APPROVED' AND p.approvedAt BETWEEN :startDate AND :endDate")
    List<PartnerOnboardingRequest> findApprovedBetween(@Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find onboarding requests with pagination and filtering
     */
    @Query("SELECT p FROM PartnerOnboardingRequest p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:kycStatus IS NULL OR p.kycStatus = :kycStatus) AND " +
           "(:country IS NULL OR p.country = :country) AND " +
           "(:companyName IS NULL OR LOWER(p.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))")
    Page<PartnerOnboardingRequest> findWithFilters(@Param("status") OnboardingStatus status,
                                                   @Param("kycStatus") KYCStatus kycStatus,
                                                   @Param("country") String country,
                                                   @Param("companyName") String companyName,
                                                   Pageable pageable);

    /**
     * Check if business registration number already exists
     */
    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    /**
     * Check if tax identification number already exists
     */
    boolean existsByTaxIdentificationNumber(String taxIdentificationNumber);

    /**
     * Check if primary contact email already exists
     */
    boolean existsByPrimaryContactEmail(String email);
}