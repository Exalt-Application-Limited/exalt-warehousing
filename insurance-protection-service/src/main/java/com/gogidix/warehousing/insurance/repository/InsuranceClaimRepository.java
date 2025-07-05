package com.gogidix.warehousing.insurance.repository;

import com.gogidix.warehousing.insurance.entity.InsuranceClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Insurance Claim Repository
 * 
 * Data access layer for insurance claim operations.
 */
@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
    
    /**
     * Find claim by claim number
     */
    Optional<InsuranceClaim> findByClaimNumber(String claimNumber);
    
    /**
     * Find claims by insurance policy
     */
    Page<InsuranceClaim> findByInsurancePolicyId(Long policyId, Pageable pageable);
    
    /**
     * Find claims by customer
     */
    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.insurancePolicy.customerId = :customerId")
    Page<InsuranceClaim> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    /**
     * Find claims by status
     */
    Page<InsuranceClaim> findByStatus(InsuranceClaim.ClaimStatus status, Pageable pageable);
    
    /**
     * Find claims assigned to assessor
     */
    Page<InsuranceClaim> findByAssessorId(Long assessorId, Pageable pageable);
    
    /**
     * Find claims by incident date range
     */
    Page<InsuranceClaim> findByIncidentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find claims submitted within date range
     */
    Page<InsuranceClaim> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find pending claims (requiring action)
     */
    List<InsuranceClaim> findByStatusIn(List<InsuranceClaim.ClaimStatus> statuses);
    
    /**
     * Find overdue claims
     */
    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.status IN ('SUBMITTED', 'UNDER_REVIEW') " +
           "AND ic.submittedAt < :overdueThreshold")
    List<InsuranceClaim> findOverdueClaims(@Param("overdueThreshold") LocalDateTime overdueThreshold);
    
    /**
     * Find high-value claims
     */
    @Query("SELECT ic FROM InsuranceClaim ic WHERE ic.claimedAmount >= :threshold")
    List<InsuranceClaim> findHighValueClaims(@Param("threshold") BigDecimal threshold);
    
    /**
     * Get claim statistics
     */
    @Query("SELECT ic.status, COUNT(ic) FROM InsuranceClaim ic GROUP BY ic.status")
    List<Object[]> getClaimStatsByStatus();
    
    @Query("SELECT ic.incidentType, COUNT(ic) FROM InsuranceClaim ic GROUP BY ic.incidentType")
    List<Object[]> getClaimStatsByIncidentType();
    
    /**
     * Calculate total claimed amount
     */
    @Query("SELECT SUM(ic.claimedAmount) FROM InsuranceClaim ic WHERE ic.status = :status")
    BigDecimal getTotalClaimedAmountByStatus(@Param("status") InsuranceClaim.ClaimStatus status);
    
    /**
     * Calculate total paid amount
     */
    @Query("SELECT SUM(ic.paidAmount) FROM InsuranceClaim ic WHERE ic.status = 'PAID'")
    BigDecimal getTotalPaidAmount();
    
    /**
     * Count claims by customer
     */
    @Query("SELECT COUNT(ic) FROM InsuranceClaim ic WHERE ic.insurancePolicy.customerId = :customerId")
    long countClaimsByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find claims requiring assessment
     */
    List<InsuranceClaim> findByStatusAndAssessorIdIsNull(InsuranceClaim.ClaimStatus status);
}