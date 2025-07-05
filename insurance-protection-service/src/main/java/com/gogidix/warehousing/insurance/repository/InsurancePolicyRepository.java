package com.gogidix.warehousing.insurance.repository;

import com.gogidix.warehousing.insurance.entity.InsurancePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Insurance Policy Repository
 * 
 * Data access layer for insurance policy operations.
 */
@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    
    /**
     * Find policy by policy number
     */
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    
    /**
     * Find policies by customer ID
     */
    Page<InsurancePolicy> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find policies by customer email
     */
    Page<InsurancePolicy> findByCustomerEmail(String customerEmail, Pageable pageable);
    
    /**
     * Find policy by storage unit ID
     */
    Optional<InsurancePolicy> findByStorageUnitId(Long storageUnitId);
    
    /**
     * Find policies by status
     */
    Page<InsurancePolicy> findByStatus(InsurancePolicy.PolicyStatus status, Pageable pageable);
    
    /**
     * Find policies by coverage plan
     */
    Page<InsurancePolicy> findByCoveragePlanId(Long coveragePlanId, Pageable pageable);
    
    /**
     * Find policies expiring within date range
     */
    @Query("SELECT ip FROM InsurancePolicy ip WHERE ip.expirationDate BETWEEN :startDate AND :endDate")
    List<InsurancePolicy> findPoliciesExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
    
    /**
     * Find expired policies
     */
    @Query("SELECT ip FROM InsurancePolicy ip WHERE ip.expirationDate < :currentDate AND ip.status = 'ACTIVE'")
    List<InsurancePolicy> findExpiredPolicies(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find policies due for renewal
     */
    @Query("SELECT ip FROM InsurancePolicy ip WHERE ip.expirationDate BETWEEN :currentDate AND :renewalDate " +
           "AND ip.status = 'ACTIVE'")
    List<InsurancePolicy> findPoliciesDueForRenewal(@Param("currentDate") LocalDate currentDate, 
                                                    @Param("renewalDate") LocalDate renewalDate);
    
    /**
     * Get policy statistics
     */
    @Query("SELECT ip.status, COUNT(ip) FROM InsurancePolicy ip GROUP BY ip.status")
    List<Object[]> getPolicyStatsByStatus();
    
    @Query("SELECT cp.planName, COUNT(ip) FROM InsurancePolicy ip JOIN ip.coveragePlan cp GROUP BY cp.planName")
    List<Object[]> getPolicyStatsByCoveragePlan();
    
    /**
     * Calculate total coverage amount by customer
     */
    @Query("SELECT SUM(ip.coverageAmount) FROM InsurancePolicy ip WHERE ip.customerId = :customerId " +
           "AND ip.status = 'ACTIVE'")
    BigDecimal getTotalCoverageByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total monthly premiums for customer
     */
    @Query("SELECT SUM(ip.monthlyPremium) FROM InsurancePolicy ip WHERE ip.customerId = :customerId " +
           "AND ip.status = 'ACTIVE'")
    BigDecimal getTotalMonthlyPremiumsByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Count active policies
     */
    long countByStatus(InsurancePolicy.PolicyStatus status);
    
    /**
     * Find policies with pending payments
     */
    @Query("SELECT DISTINCT ip FROM InsurancePolicy ip JOIN ip.payments pp " +
           "WHERE pp.status IN ('PENDING', 'FAILED') AND ip.status = 'ACTIVE'")
    List<InsurancePolicy> findPoliciesWithPendingPayments();
}