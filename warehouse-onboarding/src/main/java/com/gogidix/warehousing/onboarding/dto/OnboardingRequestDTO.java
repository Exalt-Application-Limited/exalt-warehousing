package com.gogidix.warehousing.onboarding.dto;

import com.gogidix.warehousing.onboarding.model.BusinessType;
import com.gogidix.warehousing.onboarding.model.ServiceCapability;
import com.gogidix.warehousing.onboarding.model.StorageType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for Partner Onboarding Request submission
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequestDTO {

    // Basic Company Information
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    @NotBlank(message = "Legal business name is required")
    @Size(max = 200, message = "Legal business name must not exceed 200 characters")
    private String legalBusinessName;

    @NotBlank(message = "Business registration number is required")
    @Size(max = 50, message = "Business registration number must not exceed 50 characters")
    private String businessRegistrationNumber;

    @NotBlank(message = "Tax identification number is required")
    @Size(max = 50, message = "Tax identification number must not exceed 50 characters")
    private String taxIdentificationNumber;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    @NotBlank(message = "Country of incorporation is required")
    @Size(max = 100, message = "Country of incorporation must not exceed 100 characters")
    private String countryOfIncorporation;

    @NotNull(message = "Date of incorporation is required")
    private LocalDateTime dateOfIncorporation;

    // Contact Information
    @NotBlank(message = "Primary contact name is required")
    @Size(max = 100, message = "Primary contact name must not exceed 100 characters")
    private String primaryContactName;

    @NotBlank(message = "Primary contact email is required")
    @Email(message = "Primary contact email must be valid")
    @Size(max = 100, message = "Primary contact email must not exceed 100 characters")
    private String primaryContactEmail;

    @NotBlank(message = "Primary contact phone is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,20}$", message = "Primary contact phone must be valid")
    private String primaryContactPhone;

    @NotBlank(message = "Business address is required")
    @Size(max = 300, message = "Business address must not exceed 300 characters")
    private String businessAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    // Warehouse Capabilities
    @NotNull(message = "Total storage capacity is required")
    @DecimalMin(value = "0.1", message = "Total storage capacity must be greater than 0")
    private BigDecimal totalStorageCapacity;

    @NotNull(message = "Available storage capacity is required")
    @DecimalMin(value = "0.1", message = "Available storage capacity must be greater than 0")
    private BigDecimal availableStorageCapacity;

    @NotEmpty(message = "At least one storage type must be specified")
    private Set<StorageType> supportedStorageTypes;

    @NotEmpty(message = "At least one service capability must be specified")
    private Set<ServiceCapability> serviceCapabilities;

    @NotNull(message = "Temperature control capability must be specified")
    private Boolean hasTemperatureControl;

    @NotNull(message = "Security systems capability must be specified")
    private Boolean hasSecuritySystems;

    @NotNull(message = "Inventory management system capability must be specified")
    private Boolean hasInventoryManagementSystem;

    // Financial Information
    @NotNull(message = "Proposed pricing per cubic meter is required")
    @DecimalMin(value = "0.01", message = "Proposed pricing must be greater than 0")
    private BigDecimal proposedPricingPerCubicMeter;

    @NotNull(message = "Minimum order value is required")
    @DecimalMin(value = "0.01", message = "Minimum order value must be greater than 0")
    private BigDecimal minimumOrderValue;

    @NotBlank(message = "Preferred payment terms are required")
    @Size(max = 100, message = "Preferred payment terms must not exceed 100 characters")
    private String preferredPaymentTerms;

    /**
     * Validate that available capacity doesn't exceed total capacity
     */
    @AssertTrue(message = "Available storage capacity cannot exceed total storage capacity")
    public boolean isAvailableCapacityValid() {
        if (totalStorageCapacity == null || availableStorageCapacity == null) {
            return true; // Let @NotNull handle null validation
        }
        return availableStorageCapacity.compareTo(totalStorageCapacity) <= 0;
    }

    /**
     * Validate date of incorporation is not in the future
     */
    @AssertTrue(message = "Date of incorporation cannot be in the future")
    public boolean isDateOfIncorporationValid() {
        if (dateOfIncorporation == null) {
            return true; // Let @NotNull handle null validation
        }
        return !dateOfIncorporation.isAfter(LocalDateTime.now());
    }
}