package com.gogidix.warehousing.insurance.repository;

import com.gogidix.warehousing.insurance.entity.CoveragePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Coverage Plan Repository
 * 
 * Data access layer for coverage plan operations.
 */
@Repository
public interface CoveragePlanRepository extends JpaRepository<CoveragePlan, Long> {
    
    /**
     * Find coverage plan by plan code
     */
    Optional<CoveragePlan> findByPlanCode(String planCode);
    
    /**
     * Find active coverage plans
     */
    Page<CoveragePlan> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find plans by coverage type
     */
    Page<CoveragePlan> findByCoverageType(CoveragePlan.CoverageType coverageType, Pageable pageable);
    
    /**
     * Find active plans by coverage type
     */
    Page<CoveragePlan> findByCoverageTypeAndIsActiveTrue(CoveragePlan.CoverageType coverageType, Pageable pageable);
    
    /**
     * Get all active plans
     */
    List<CoveragePlan> findByIsActiveTrueOrderByPremiumRateAsc();
    
    /**
     * Get coverage plan statistics
     */
    @Query("SELECT cp.coverageType, COUNT(cp) FROM CoveragePlan cp WHERE cp.isActive = true GROUP BY cp.coverageType")
    List<Object[]> getCoveragePlanStatsByType();
    
    /**
     * Count active plans
     */
    long countByIsActiveTrue();
    
    /**
     * Find plans with policies
     */
    @Query("SELECT DISTINCT cp FROM CoveragePlan cp WHERE EXISTS (SELECT 1 FROM InsurancePolicy ip WHERE ip.coveragePlan = cp)")
    List<CoveragePlan> findPlansWithPolicies();
}