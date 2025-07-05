package com.gogidix.warehousing.shared.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations across the warehousing domain
 * 
 * This class provides common validation methods used throughout the warehousing services
 * for data validation, business rule checking, and input sanitization.
 */
@UtilityClass
public class ValidationUtils {

    // Regular expression patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\-\\s\\(\\)]+$"
    );
    
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(
        "^[A-Za-z0-9\\s\\-]{3,10}$"
    );
    
    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile(
        "^[A-Z]{2}$"
    );
    
    private static final Pattern SKU_PATTERN = Pattern.compile(
        "^[A-Za-z0-9\\-_]{3,50}$"
    );
    
    private static final Pattern BARCODE_PATTERN = Pattern.compile(
        "^[0-9]{8,14}$"
    );

    /**
     * Validate if a string is not null and not empty (after trimming)
     * 
     * @param value the string to validate
     * @return true if the string is valid (not null or empty)
     */
    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validate if a string has a specific length range
     * 
     * @param value the string to validate
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return true if the string length is within the specified range
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return minLength <= 0;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate email format
     * 
     * @param email the email to validate
     * @return true if the email format is valid
     */
    public static boolean isValidEmail(String email) {
        return isNotNullOrEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone number format
     * 
     * @param phone the phone number to validate
     * @return true if the phone number format is valid
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotNullOrEmpty(phone)) {
            return false;
        }
        String cleanPhone = phone.trim();
        return cleanPhone.length() >= 7 && cleanPhone.length() <= 20 && 
               PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate postal code format
     * 
     * @param postalCode the postal code to validate
     * @return true if the postal code format is valid
     */
    public static boolean isValidPostalCode(String postalCode) {
        return isNotNullOrEmpty(postalCode) && 
               POSTAL_CODE_PATTERN.matcher(postalCode.trim()).matches();
    }

    /**
     * Validate country code format (ISO 3166-1 alpha-2)
     * 
     * @param countryCode the country code to validate
     * @return true if the country code format is valid
     */
    public static boolean isValidCountryCode(String countryCode) {
        return isNotNullOrEmpty(countryCode) && 
               COUNTRY_CODE_PATTERN.matcher(countryCode.trim()).matches();
    }

    /**
     * Validate SKU format
     * 
     * @param sku the SKU to validate
     * @return true if the SKU format is valid
     */
    public static boolean isValidSKU(String sku) {
        return isNotNullOrEmpty(sku) && SKU_PATTERN.matcher(sku.trim()).matches();
    }

    /**
     * Validate barcode format
     * 
     * @param barcode the barcode to validate
     * @return true if the barcode format is valid
     */
    public static boolean isValidBarcode(String barcode) {
        return isNotNullOrEmpty(barcode) && BARCODE_PATTERN.matcher(barcode.trim()).matches();
    }

    /**
     * Validate UUID format
     * 
     * @param uuid the UUID string to validate
     * @return true if the UUID format is valid
     */
    public static boolean isValidUUID(String uuid) {
        if (!isNotNullOrEmpty(uuid)) {
            return false;
        }
        try {
            UUID.fromString(uuid.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validate that a UUID is not null
     * 
     * @param uuid the UUID to validate
     * @return true if the UUID is not null
     */
    public static boolean isValidUUID(UUID uuid) {
        return uuid != null;
    }

    /**
     * Validate BigDecimal value within a range
     * 
     * @param value the value to validate
     * @param min minimum value (inclusive), null for no minimum
     * @param max maximum value (inclusive), null for no maximum
     * @return true if the value is within the specified range
     */
    public static boolean isValidRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return false;
        }
        
        if (min != null && value.compareTo(min) < 0) {
            return false;
        }
        
        if (max != null && value.compareTo(max) > 0) {
            return false;
        }
        
        return true;
    }

    /**
     * Validate that a BigDecimal is positive
     * 
     * @param value the value to validate
     * @return true if the value is positive (> 0)
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validate that a BigDecimal is non-negative
     * 
     * @param value the value to validate
     * @return true if the value is non-negative (>= 0)
     */
    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Validate that an integer is positive
     * 
     * @param value the value to validate
     * @return true if the value is positive (> 0)
     */
    public static boolean isPositive(Integer value) {
        return value != null && value > 0;
    }

    /**
     * Validate that an integer is non-negative
     * 
     * @param value the value to validate
     * @return true if the value is non-negative (>= 0)
     */
    public static boolean isNonNegative(Integer value) {
        return value != null && value >= 0;
    }

    /**
     * Validate that a collection is not null and not empty
     * 
     * @param collection the collection to validate
     * @return true if the collection is not null and not empty
     */
    public static boolean isNotNullOrEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Validate date range (start date before end date)
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return true if start date is before end date (both can be null)
     */
    public static boolean isValidDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return true; // Allow null dates
        }
        return startDate.isBefore(endDate) || startDate.isEqual(endDate);
    }

    /**
     * Validate that a date is in the future
     * 
     * @param date the date to validate
     * @return true if the date is in the future
     */
    public static boolean isFutureDate(LocalDateTime date) {
        return date != null && date.isAfter(LocalDateTime.now());
    }

    /**
     * Validate that a date is in the past
     * 
     * @param date the date to validate
     * @return true if the date is in the past
     */
    public static boolean isPastDate(LocalDateTime date) {
        return date != null && date.isBefore(LocalDateTime.now());
    }

    /**
     * Validate coordinate values (latitude and longitude)
     * 
     * @param latitude the latitude value
     * @param longitude the longitude value
     * @return true if both coordinates are valid
     */
    public static boolean areValidCoordinates(Double latitude, Double longitude) {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    /**
     * Validate latitude value
     * 
     * @param latitude the latitude value
     * @return true if latitude is valid (-90 to 90)
     */
    public static boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90.0 && latitude <= 90.0;
    }

    /**
     * Validate longitude value
     * 
     * @param longitude the longitude value
     * @return true if longitude is valid (-180 to 180)
     */
    public static boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Validate warehouse capacity values
     * 
     * @param usedCapacity the used capacity
     * @param totalCapacity the total capacity
     * @return true if capacity values are valid
     */
    public static boolean isValidCapacity(BigDecimal usedCapacity, BigDecimal totalCapacity) {
        if (!isNonNegative(usedCapacity) || !isPositive(totalCapacity)) {
            return false;
        }
        return usedCapacity.compareTo(totalCapacity) <= 0;
    }

    /**
     * Validate percentage value (0-100)
     * 
     * @param percentage the percentage value
     * @return true if percentage is valid (0-100)
     */
    public static boolean isValidPercentage(BigDecimal percentage) {
        return isValidRange(percentage, BigDecimal.ZERO, BigDecimal.valueOf(100));
    }

    /**
     * Validate dimensions (all positive values)
     * 
     * @param length the length
     * @param width the width
     * @param height the height
     * @return true if all dimensions are positive
     */
    public static boolean isValidDimensions(BigDecimal length, BigDecimal width, BigDecimal height) {
        return isPositive(length) && isPositive(width) && isPositive(height);
    }

    /**
     * Validate weight (positive value)
     * 
     * @param weight the weight
     * @return true if weight is positive
     */
    public static boolean isValidWeight(BigDecimal weight) {
        return isPositive(weight);
    }

    /**
     * Sanitize string input by trimming whitespace and handling nulls
     * 
     * @param input the input string
     * @return sanitized string or null if input was null
     */
    public static String sanitizeString(String input) {
        return input != null ? input.trim() : null;
    }

    /**
     * Sanitize string input and return empty string if null
     * 
     * @param input the input string
     * @return sanitized string or empty string if input was null
     */
    public static String sanitizeStringWithDefault(String input) {
        return StringUtils.defaultString(sanitizeString(input));
    }

    /**
     * Validate and sanitize name field (common validation for names)
     * 
     * @param name the name to validate
     * @param maxLength maximum allowed length
     * @return true if name is valid
     */
    public static boolean isValidName(String name, int maxLength) {
        if (!isNotNullOrEmpty(name)) {
            return false;
        }
        
        String sanitized = sanitizeString(name);
        if (!isValidLength(sanitized, 1, maxLength)) {
            return false;
        }
        
        // Name should contain only letters, spaces, hyphens, and apostrophes
        return sanitized.matches("^[a-zA-Z\\s\\-']+$");
    }

    /**
     * Validate business identifier (common validation for business IDs)
     * 
     * @param identifier the identifier to validate
     * @return true if identifier is valid
     */
    public static boolean isValidBusinessIdentifier(String identifier) {
        if (!isNotNullOrEmpty(identifier)) {
            return false;
        }
        
        String sanitized = sanitizeString(identifier);
        // Business identifier: alphanumeric, hyphens, underscores, 3-50 characters
        return sanitized.matches("^[A-Za-z0-9\\-_]{3,50}$");
    }

    /**
     * Validate that all required fields in an object are present
     * This is a generic validation that checks for null values
     * 
     * @param objects the objects to check for null values
     * @return true if none of the objects are null
     */
    public static boolean areAllFieldsPresent(Object... objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
            
            // Special handling for strings
            if (obj instanceof String && !isNotNullOrEmpty((String) obj)) {
                return false;
            }
            
            // Special handling for collections
            if (obj instanceof Collection && !isNotNullOrEmpty((Collection<?>) obj)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Validation result class for complex validations
     */
    @lombok.Data
    @lombok.Builder
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public static ValidationResult valid() {
            return ValidationResult.builder()
                    .valid(true)
                    .message("Validation passed")
                    .build();
        }
        
        public static ValidationResult invalid(String message) {
            return ValidationResult.builder()
                    .valid(false)
                    .message(message)
                    .build();
        }
    }
}