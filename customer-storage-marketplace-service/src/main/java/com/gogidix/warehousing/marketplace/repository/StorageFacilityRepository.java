package com.gogidix.warehousing.marketplace.repository;

import com.gogidix.warehousing.marketplace.entity.StorageFacility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StorageFacility entities.
 * 
 * Provides data access methods for storage facility operations including
 * location-based searches, filtering, and availability queries.
 */
@Repository
public interface StorageFacilityRepository extends JpaRepository<StorageFacility, Long> {

    /**
     * Find facility by unique facility code
     */
    Optional<StorageFacility> findByFacilityCode(String facilityCode);

    /**
     * Find all active facilities
     */
    List<StorageFacility> findByIsActiveTrueAndStatus(StorageFacility.FacilityStatus status);

    /**
     * Find facilities by city and state
     */
    Page<StorageFacility> findByCityIgnoreCaseAndStateIgnoreCaseAndIsActiveTrue(
            String city, String state, Pageable pageable);

    /**
     * Find facilities by zip code
     */
    List<StorageFacility> findByZipCodeAndIsActiveTrue(String zipCode);

    /**
     * Find facilities within a geographic radius using Haversine formula
     */
    @Query("""
        SELECT f FROM StorageFacility f 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(f.latitude)) * 
             cos(radians(f.longitude) - radians(:longitude)) + 
             sin(radians(:latitude)) * sin(radians(f.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(f.latitude)) * 
                 cos(radians(f.longitude) - radians(:longitude)) + 
                 sin(radians(:latitude)) * sin(radians(f.latitude))))
        """)
    List<StorageFacility> findFacilitiesWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") double radiusKm);

    /**
     * Find facilities with available units
     */
    @Query("SELECT f FROM StorageFacility f WHERE f.isActive = true AND f.availableUnits > 0")
    Page<StorageFacility> findFacilitiesWithAvailableUnits(Pageable pageable);

    /**
     * Find facilities by facility type
     */
    Page<StorageFacility> findByFacilityTypeAndIsActiveTrueAndStatus(
            StorageFacility.FacilityType facilityType, 
            StorageFacility.FacilityStatus status, 
            Pageable pageable);

    /**
     * Find facilities with specific amenities
     */
    @Query("""
        SELECT DISTINCT f FROM StorageFacility f 
        JOIN f.amenities a 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND a IN :amenities
        """)
    List<StorageFacility> findByAmenitiesIn(@Param("amenities") List<StorageFacility.FacilityAmenity> amenities);

    /**
     * Find facilities within price range
     */
    @Query("""
        SELECT f FROM StorageFacility f 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND f.basePrice BETWEEN :minPrice AND :maxPrice
        ORDER BY f.basePrice ASC
        """)
    Page<StorageFacility> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice, 
            Pageable pageable);

    /**
     * Find top-rated facilities
     */
    @Query("""
        SELECT f FROM StorageFacility f 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND f.totalReviews >= :minReviews
        ORDER BY f.averageRating DESC, f.totalReviews DESC
        """)
    Page<StorageFacility> findTopRatedFacilities(@Param("minReviews") int minReviews, Pageable pageable);

    /**
     * Search facilities by name or description
     */
    @Query("""
        SELECT f FROM StorageFacility f 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND (LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
             OR LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        ORDER BY f.averageRating DESC
        """)
    Page<StorageFacility> searchFacilities(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find facilities by owner
     */
    List<StorageFacility> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    /**
     * Count active facilities by city
     */
    @Query("SELECT COUNT(f) FROM StorageFacility f WHERE f.city = :city AND f.isActive = true")
    long countActiveFacilitiesByCity(@Param("city") String city);

    /**
     * Find facilities needing maintenance
     */
    @Query("""
        SELECT f FROM StorageFacility f 
        WHERE f.isActive = true 
        AND (f.nextInspectionDate < CURRENT_DATE 
             OR f.status = 'UNDER_MAINTENANCE')
        ORDER BY f.nextInspectionDate ASC
        """)
    List<StorageFacility> findFacilitiesNeedingMaintenance();

    /**
     * Find facilities with high occupancy
     */
    @Query("""
        SELECT f FROM StorageFacility f 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND ((f.totalUnits - f.availableUnits) * 100.0 / f.totalUnits) >= :occupancyThreshold
        ORDER BY ((f.totalUnits - f.availableUnits) * 100.0 / f.totalUnits) DESC
        """)
    List<StorageFacility> findHighOccupancyFacilities(@Param("occupancyThreshold") double occupancyThreshold);

    /**
     * Advanced search with multiple filters
     */
    @Query("""
        SELECT DISTINCT f FROM StorageFacility f 
        LEFT JOIN f.amenities a 
        WHERE f.isActive = true 
        AND f.status = 'ACTIVE'
        AND (:city IS NULL OR LOWER(f.city) = LOWER(:city))
        AND (:state IS NULL OR LOWER(f.state) = LOWER(:state))
        AND (:facilityType IS NULL OR f.facilityType = :facilityType)
        AND (:minPrice IS NULL OR f.basePrice >= :minPrice)
        AND (:maxPrice IS NULL OR f.basePrice <= :maxPrice)
        AND (:minRating IS NULL OR f.averageRating >= :minRating)
        AND (:hasAvailableUnits = false OR f.availableUnits > 0)
        AND (:amenity IS NULL OR a = :amenity)
        ORDER BY f.averageRating DESC, f.basePrice ASC
        """)
    Page<StorageFacility> findWithFilters(
            @Param("city") String city,
            @Param("state") String state,
            @Param("facilityType") StorageFacility.FacilityType facilityType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") BigDecimal minRating,
            @Param("hasAvailableUnits") boolean hasAvailableUnits,
            @Param("amenity") StorageFacility.FacilityAmenity amenity,
            Pageable pageable);
}