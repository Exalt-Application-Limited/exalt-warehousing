package com.gogidix.warehousing.marketplace.controller;

import com.gogidix.warehousing.marketplace.entity.StorageFacility;
import com.gogidix.warehousing.marketplace.repository.StorageFacilityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for StorageMarketplaceController
 */
@WebMvcTest(StorageMarketplaceController.class)
@DisplayName("Storage Marketplace Controller Tests")
class StorageMarketplaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageFacilityRepository facilityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private StorageFacility testFacility;
    private List<StorageFacility> testFacilities;

    @BeforeEach
    void setUp() {
        testFacility = createTestFacility(1L, "FAC001", "Premium Storage Center", 
                "Atlanta", "GA", new BigDecimal("99.99"));
        
        StorageFacility facility2 = createTestFacility(2L, "FAC002", "SecureStore Atlanta", 
                "Atlanta", "GA", new BigDecimal("89.99"));
        
        testFacilities = Arrays.asList(testFacility, facility2);
    }

    @Test
    @DisplayName("Should search facilities with search term")
    void shouldSearchFacilitiesWithSearchTerm() throws Exception {
        // Given
        String searchTerm = "Premium";
        Page<StorageFacility> facilityPage = new PageImpl<>(
                List.of(testFacility), PageRequest.of(0, 20), 1);
        
        when(facilityRepository.searchFacilities(eq(searchTerm), any(Pageable.class)))
                .thenReturn(facilityPage);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/search")
                        .param("search", searchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Premium Storage Center"))
                .andExpect(jsonPath("$.content[0].facilityCode").value("FAC001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should search facilities by city and state")
    void shouldSearchFacilitiesByCityAndState() throws Exception {
        // Given
        Page<StorageFacility> facilityPage = new PageImpl<>(
                testFacilities, PageRequest.of(0, 20), 2);
        
        when(facilityRepository.findWithFilters(
                eq("Atlanta"), eq("GA"), any(), any(), any(), any(), eq(false), any(), any(Pageable.class)))
                .thenReturn(facilityPage);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/search")
                        .param("city", "Atlanta")
                        .param("state", "GA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpected(jsonPath("$.content").value(hasSize(2)))
                .andExpect(jsonPath("$.content[0].city").value("Atlanta"))
                .andExpect(jsonPath("$.content[0].state").value("GA"));
    }

    @Test
    @DisplayName("Should find nearby facilities")
    void shouldFindNearbyFacilities() throws Exception {
        // Given
        BigDecimal latitude = new BigDecimal("33.7490");
        BigDecimal longitude = new BigDecimal("-84.3880");
        double radius = 25.0;
        
        when(facilityRepository.findFacilitiesWithinRadius(latitude, longitude, radius))
                .thenReturn(testFacilities);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/nearby")
                        .param("latitude", latitude.toString())
                        .param("longitude", longitude.toString())
                        .param("radiusKm", String.valueOf(radius))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Premium Storage Center"));
    }

    @Test
    @DisplayName("Should get facility by ID")
    void shouldGetFacilityById() throws Exception {
        // Given
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId))
                .thenReturn(Optional.of(testFacility));

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/{facilityId}", facilityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facilityId))
                .andExpect(jsonPath("$.name").value("Premium Storage Center"))
                .andExpect(jsonPath("$.facilityCode").value("FAC001"));
    }

    @Test
    @DisplayName("Should return 404 when facility not found by ID")
    void shouldReturn404WhenFacilityNotFoundById() throws Exception {
        // Given
        Long facilityId = 999L;
        when(facilityRepository.findById(facilityId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/{facilityId}", facilityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get facility by code")
    void shouldGetFacilityByCode() throws Exception {
        // Given
        String facilityCode = "FAC001";
        when(facilityRepository.findByFacilityCode(facilityCode))
                .thenReturn(Optional.of(testFacility));

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/code/{facilityCode}", facilityCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilityCode").value(facilityCode))
                .andExpect(jsonPath("$.name").value("Premium Storage Center"));
    }

    @Test
    @DisplayName("Should get facilities by location")
    void shouldGetFacilitiesByLocation() throws Exception {
        // Given
        String city = "Atlanta";
        String state = "GA";
        Page<StorageFacility> facilityPage = new PageImpl<>(
                testFacilities, PageRequest.of(0, 20), 2);
        
        when(facilityRepository.findByCityIgnoreCaseAndStateIgnoreCaseAndIsActiveTrue(
                eq(city), eq(state), any(Pageable.class)))
                .thenReturn(facilityPage);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/location")
                        .param("city", city)
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.content").isArray())
                .andExpected(jsonPath("$.content").value(hasSize(2)))
                .andExpected(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should get top-rated facilities")
    void shouldGetTopRatedFacilities() throws Exception {
        // Given
        int minReviews = 5;
        Page<StorageFacility> facilityPage = new PageImpl<>(
                List.of(testFacility), PageRequest.of(0, 10), 1);
        
        when(facilityRepository.findTopRatedFacilities(eq(minReviews), any(Pageable.class)))
                .thenReturn(facilityPage);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/top-rated")
                        .param("minReviews", String.valueOf(minReviews))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.content").isArray())
                .andExpected(jsonPath("$.content[0].averageRating").value(4.5));
    }

    @Test
    @DisplayName("Should get facilities by price range")
    void shouldGetFacilitiesByPriceRange() throws Exception {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("150.00");
        Page<StorageFacility> facilityPage = new PageImpl<>(
                testFacilities, PageRequest.of(0, 20), 2);
        
        when(facilityRepository.findByPriceRange(eq(minPrice), eq(maxPrice), any(Pageable.class)))
                .thenReturn(facilityPage);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/price-range")
                        .param("minPrice", minPrice.toString())
                        .param("maxPrice", maxPrice.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.content").isArray())
                .andExpected(jsonPath("$.content").value(hasSize(2)));
    }

    @Test
    @DisplayName("Should get facilities with amenities")
    void shouldGetFacilitiesWithAmenities() throws Exception {
        // Given
        List<StorageFacility.FacilityAmenity> amenities = 
                Arrays.asList(StorageFacility.FacilityAmenity.CLIMATE_CONTROL);
        
        when(facilityRepository.findByAmenitiesIn(amenities))
                .thenReturn(testFacilities);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/amenities")
                        .param("amenities", "CLIMATE_CONTROL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)));
    }

    @Test
    @DisplayName("Should get facilities by ZIP code")
    void shouldGetFacilitiesByZipCode() throws Exception {
        // Given
        String zipCode = "30309";
        when(facilityRepository.findByZipCodeAndIsActiveTrue(zipCode))
                .thenReturn(testFacilities);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/zip/{zipCode}", zipCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].zipCode").value("30309"));
    }

    @Test
    @DisplayName("Should handle search with pagination parameters")
    void shouldHandleSearchWithPaginationParameters() throws Exception {
        // Given
        Page<StorageFacility> facilityPage = new PageImpl<>(
                List.of(testFacility), PageRequest.of(1, 5), 10);
        
        when(facilityRepository.findAll(any(Pageable.class)))
                .thenReturn(facilityPage);

        // When & Then
        mockMvc.perform(get("/api/v1/marketplace/facilities/search")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "name")
                        .param("sortDirection", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.content").isArray())
                .andExpected(jsonPath("$.totalElements").value(10))
                .andExpected(jsonPath("$.size").value(5))
                .andExpected(jsonPath("$.number").value(1));
    }

    private StorageFacility createTestFacility(Long id, String facilityCode, String name, 
                                             String city, String state, BigDecimal basePrice) {
        StorageFacility facility = new StorageFacility();
        facility.setId(id);
        facility.setFacilityCode(facilityCode);
        facility.setName(name);
        facility.setDescription("Test storage facility description");
        facility.setAddress("123 Test Street");
        facility.setCity(city);
        facility.setState(state);
        facility.setZipCode("30309");
        facility.setCountry("USA");
        facility.setLatitude(new BigDecimal("33.7490"));
        facility.setLongitude(new BigDecimal("-84.3880"));
        facility.setContactPhone("555-0123");
        facility.setContactEmail("test@example.com");
        facility.setFacilityType(StorageFacility.FacilityType.SELF_STORAGE);
        facility.setTotalUnits(100);
        facility.setAvailableUnits(25);
        facility.setTotalSquareFootage(new BigDecimal("10000"));
        facility.setOperatingHours("24/7");
        facility.setBasePrice(basePrice);
        facility.setPricingModel(StorageFacility.PricingModel.FIXED_MONTHLY);
        facility.setStatus(StorageFacility.FacilityStatus.ACTIVE);
        facility.setIsActive(true);
        facility.setAverageRating(new BigDecimal("4.5"));
        facility.setTotalReviews(15);
        facility.setOwnerId("owner123");
        facility.setCreatedAt(LocalDateTime.now());
        facility.setUpdatedAt(LocalDateTime.now());
        
        return facility;
    }
}