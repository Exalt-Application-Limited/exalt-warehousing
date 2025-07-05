package com.gogidix.warehousing.marketplace.controller;

import com.gogidix.warehousing.marketplace.entity.StorageFacility;
import com.gogidix.warehousing.marketplace.repository.StorageFacilityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Storage Marketplace Controller
 * 
 * REST API endpoints for customer storage marketplace functionality.
 * Provides search, discovery, and comparison features for storage facilities.
 */
@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Storage Marketplace", description = "Customer storage facility marketplace APIs")
public class StorageMarketplaceController {

    private final StorageFacilityRepository facilityRepository;

    @Operation(summary = "Search storage facilities", 
               description = "Search for storage facilities with various filters and sorting options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved facilities"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/facilities/search")
    public ResponseEntity<Page<StorageFacility>> searchFacilities(
            @Parameter(description = "Search term for facility name or description")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "City name")
            @RequestParam(required = false) String city,
            
            @Parameter(description = "State abbreviation")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "ZIP code")
            @RequestParam(required = false) String zipCode,
            
            @Parameter(description = "Facility type")
            @RequestParam(required = false) StorageFacility.FacilityType facilityType,
            
            @Parameter(description = "Minimum monthly price")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum monthly price")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Minimum rating (1-5)")
            @RequestParam(required = false) BigDecimal minRating,
            
            @Parameter(description = "Required amenity")
            @RequestParam(required = false) StorageFacility.FacilityAmenity amenity,
            
            @Parameter(description = "Only show facilities with available units")
            @RequestParam(defaultValue = "false") boolean hasAvailableUnits,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "averageRating") String sortBy,
            
            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.info("Searching facilities with filters: city={}, state={}, facilityType={}, priceRange={}-{}", 
                city, state, facilityType, minPrice, maxPrice);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<StorageFacility> facilities;

        if (search != null && !search.trim().isEmpty()) {
            facilities = facilityRepository.searchFacilities(search.trim(), pageable);
        } else if (city != null || state != null || facilityType != null || 
                   minPrice != null || maxPrice != null || minRating != null || amenity != null) {
            facilities = facilityRepository.findWithFilters(
                city, state, facilityType, minPrice, maxPrice, 
                minRating, hasAvailableUnits, amenity, pageable);
        } else if (hasAvailableUnits) {
            facilities = facilityRepository.findFacilitiesWithAvailableUnits(pageable);
        } else {
            facilities = facilityRepository.findAll(pageable);
        }

        log.info("Found {} facilities matching search criteria", facilities.getTotalElements());
        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "Find facilities near location", 
               description = "Find storage facilities within a specified radius of coordinates")
    @GetMapping("/facilities/nearby")
    public ResponseEntity<List<StorageFacility>> findNearbyFacilities(
            @Parameter(description = "Latitude coordinate", required = true)
            @RequestParam BigDecimal latitude,
            
            @Parameter(description = "Longitude coordinate", required = true)
            @RequestParam BigDecimal longitude,
            
            @Parameter(description = "Search radius in kilometers")
            @RequestParam(defaultValue = "25") double radiusKm) {
        
        log.info("Finding facilities within {}km of coordinates: {}, {}", 
                radiusKm, latitude, longitude);

        List<StorageFacility> facilities = facilityRepository.findFacilitiesWithinRadius(
                latitude, longitude, radiusKm);

        log.info("Found {} facilities within radius", facilities.size());
        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "Get facility by ID", 
               description = "Retrieve detailed information about a specific storage facility")
    @GetMapping("/facilities/{facilityId}")
    public ResponseEntity<StorageFacility> getFacilityById(
            @Parameter(description = "Facility ID", required = true)
            @PathVariable Long facilityId) {
        
        log.info("Retrieving facility details for ID: {}", facilityId);

        Optional<StorageFacility> facility = facilityRepository.findById(facilityId);
        
        if (facility.isPresent()) {
            log.info("Found facility: {}", facility.get().getName());
            return ResponseEntity.ok(facility.get());
        } else {
            log.warn("Facility not found with ID: {}", facilityId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get facility by code", 
               description = "Retrieve facility information using facility code")
    @GetMapping("/facilities/code/{facilityCode}")
    public ResponseEntity<StorageFacility> getFacilityByCode(
            @Parameter(description = "Facility code", required = true)
            @PathVariable String facilityCode) {
        
        log.info("Retrieving facility details for code: {}", facilityCode);

        Optional<StorageFacility> facility = facilityRepository.findByFacilityCode(facilityCode);
        
        if (facility.isPresent()) {
            log.info("Found facility: {}", facility.get().getName());
            return ResponseEntity.ok(facility.get());
        } else {
            log.warn("Facility not found with code: {}", facilityCode);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get facilities by location", 
               description = "Find all facilities in a specific city and state")
    @GetMapping("/facilities/location")
    public ResponseEntity<Page<StorageFacility>> getFacilitiesByLocation(
            @Parameter(description = "City name", required = true)
            @RequestParam String city,
            
            @Parameter(description = "State abbreviation", required = true)
            @RequestParam String state,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Finding facilities in {}, {}", city, state);

        Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.DESC, "averageRating"));
        
        Page<StorageFacility> facilities = facilityRepository
                .findByCityIgnoreCaseAndStateIgnoreCaseAndIsActiveTrue(city, state, pageable);

        log.info("Found {} facilities in {}, {}", facilities.getTotalElements(), city, state);
        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "Get top-rated facilities", 
               description = "Retrieve highest-rated storage facilities")
    @GetMapping("/facilities/top-rated")
    public ResponseEntity<Page<StorageFacility>> getTopRatedFacilities(
            @Parameter(description = "Minimum number of reviews required")
            @RequestParam(defaultValue = "5") int minReviews,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Retrieving top-rated facilities with minimum {} reviews", minReviews);

        Pageable pageable = PageRequest.of(page, size);
        Page<StorageFacility> facilities = facilityRepository
                .findTopRatedFacilities(minReviews, pageable);

        log.info("Found {} top-rated facilities", facilities.getTotalElements());
        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "Get facilities by price range", 
               description = "Find facilities within a specific price range")
    @GetMapping("/facilities/price-range")
    public ResponseEntity<Page<StorageFacility>> getFacilitiesByPriceRange(
            @Parameter(description = "Minimum monthly price", required = true)
            @RequestParam BigDecimal minPrice,
            
            @Parameter(description = "Maximum monthly price", required = true)
            @RequestParam BigDecimal maxPrice,
            
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Finding facilities with price range: ${} - ${}", minPrice, maxPrice);

        Pageable pageable = PageRequest.of(page, size);
        Page<StorageFacility> facilities = facilityRepository
                .findByPriceRange(minPrice, maxPrice, pageable);

        log.info("Found {} facilities in price range", facilities.getTotalElements());
        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "Get facilities with specific amenities", 
               description = "Find facilities that offer specific amenities")
    @GetMapping("/facilities/amenities")
    public ResponseEntity<List<StorageFacility>> getFacilitiesWithAmenities(
            @Parameter(description = "Required amenities")
            @RequestParam List<StorageFacility.FacilityAmenity> amenities) {
        
        log.info("Finding facilities with amenities: {}", amenities);

        List<StorageFacility> facilities = facilityRepository.findByAmenitiesIn(amenities);

        log.info("Found {} facilities with specified amenities", facilities.size());
        return ResponseEntity.ok(facilities);
    }

    @Operation(summary = "Get facilities by ZIP code", 
               description = "Find all facilities in a specific ZIP code area")
    @GetMapping("/facilities/zip/{zipCode}")
    public ResponseEntity<List<StorageFacility>> getFacilitiesByZipCode(
            @Parameter(description = "ZIP code", required = true)
            @PathVariable String zipCode) {
        
        log.info("Finding facilities in ZIP code: {}", zipCode);

        List<StorageFacility> facilities = facilityRepository.findByZipCodeAndIsActiveTrue(zipCode);

        log.info("Found {} facilities in ZIP code {}", facilities.size(), zipCode);
        return ResponseEntity.ok(facilities);
    }
}