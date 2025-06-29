package com.exalt.warehousing.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object for address information.
 * Used across multiple services in the warehousing domain.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Street address
     */
    @NotBlank(message = "Street address is required")
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    private String streetAddress;

    /**
     * Additional address line (apartment, suite, etc.)
     */
    @Size(max = 100, message = "Address line 2 cannot exceed 100 characters")
    private String addressLine2;

    /**
     * City name
     */
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    /**
     * State or province
     */
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;

    /**
     * Postal or ZIP code
     */
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    /**
     * Country code (ISO 3166-1 alpha-3)
     */
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 3, message = "Country code must be 2-3 characters")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country code must be uppercase letters")
    private String countryCode;

    /**
     * Latitude for location services
     */
    private BigDecimal latitude;

    /**
     * Longitude for location services
     */
    private BigDecimal longitude;

    /**
     * Formatted address for display
     */
    private String formattedAddress;

    /**
     * Address verification status
     */
    private String verificationStatus;

    /**
     * Get formatted single-line address
     * 
     * @return String containing formatted address
     */
    public String getOneLine() {
        StringBuilder sb = new StringBuilder(streetAddress);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city).append(", ")
          .append(state).append(" ").append(postalCode);
        if (countryCode != null && !countryCode.isBlank()) {
            sb.append(", ").append(countryCode);
        }
        return sb.toString();
    }
}