package com.gogidix.warehousing.pricing.repository;

import com.gogidix.warehousing.pricing.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PricingRule entity operations.
 */
@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    /**
     * Find active pricing rules by facility ID
     */
    @Query("SELECT pr FROM PricingRule pr WHERE pr.facilityId = :facilityId AND pr.isActive = true ORDER BY pr.priority ASC")
    List<PricingRule> findActivePricingRulesByFacilityId(@Param("facilityId") Long facilityId);

    /**
     * Find pricing rules by unit type
     */
    @Query("SELECT pr FROM PricingRule pr WHERE pr.unitType = :unitType AND pr.isActive = true")
    List<PricingRule> findByUnitTypeAndActive(@Param("unitType") String unitType);

    /**
     * Find pricing rule by facility and rule type
     */
    @Query("SELECT pr FROM PricingRule pr WHERE pr.facilityId = :facilityId AND pr.ruleType = :ruleType AND pr.isActive = true")
    Optional<PricingRule> findByFacilityIdAndRuleTypeAndActive(@Param("facilityId") Long facilityId, @Param("ruleType") String ruleType);

    /**
     * Find seasonal pricing rules for a specific period
     */
    @Query("SELECT pr FROM PricingRule pr WHERE pr.isActive = true AND " +
           "((pr.seasonalStartDate <= CURRENT_DATE AND pr.seasonalEndDate >= CURRENT_DATE) OR " +
           "(pr.seasonalStartDate IS NULL AND pr.seasonalEndDate IS NULL))")
    List<PricingRule> findCurrentSeasonalRules();

    /**
     * Count active rules by facility
     */
    @Query("SELECT COUNT(pr) FROM PricingRule pr WHERE pr.facilityId = :facilityId AND pr.isActive = true")
    Long countActivePricingRulesByFacilityId(@Param("facilityId") Long facilityId);
}