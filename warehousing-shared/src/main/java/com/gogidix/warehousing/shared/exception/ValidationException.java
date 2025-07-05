package com.gogidix.warehousing.shared.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exception thrown when validation fails
 * 
 * This exception is used to communicate validation errors back to the client,
 * supporting both single field errors and multiple validation errors.
 */
@Getter
public class ValidationException extends WarehouseException {

    private static final long serialVersionUID = 1L;

    /**
     * List of individual validation errors
     */
    private final List<ValidationError> validationErrors;

    /**
     * Constructor for single validation error
     * 
     * @param message the validation error message
     */
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(new ValidationError(null, message));
    }

    /**
     * Constructor for single field validation error
     * 
     * @param field the field that failed validation
     * @param message the validation error message
     */
    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message), "VALIDATION_ERROR");
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(new ValidationError(field, message));
    }

    /**
     * Constructor for multiple validation errors
     * 
     * @param validationErrors list of validation errors
     */
    public ValidationException(List<ValidationError> validationErrors) {
        super(buildMessageFromErrors(validationErrors), "VALIDATION_ERROR");
        this.validationErrors = new ArrayList<>(validationErrors);
    }

    /**
     * Constructor for validation errors from a map
     * 
     * @param errorMap map of field names to error messages
     */
    public ValidationException(Map<String, String> errorMap) {
        super(buildMessageFromMap(errorMap), "VALIDATION_ERROR");
        this.validationErrors = new ArrayList<>();
        errorMap.forEach((field, message) -> 
            this.validationErrors.add(new ValidationError(field, message)));
    }

    /**
     * Add a validation error to the list
     * 
     * @param field the field name
     * @param message the error message
     */
    public void addValidationError(String field, String message) {
        this.validationErrors.add(new ValidationError(field, message));
    }

    /**
     * Check if there are any validation errors
     * 
     * @return true if there are validation errors
     */
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }

    /**
     * Get the number of validation errors
     * 
     * @return number of validation errors
     */
    public int getErrorCount() {
        return validationErrors.size();
    }

    /**
     * Get validation errors for a specific field
     * 
     * @param field the field name
     * @return list of validation errors for the field
     */
    public List<ValidationError> getErrorsForField(String field) {
        return validationErrors.stream()
                .filter(error -> field.equals(error.getField()))
                .toList();
    }

    /**
     * Check if a specific field has validation errors
     * 
     * @param field the field name
     * @return true if the field has validation errors
     */
    public boolean hasErrorsForField(String field) {
        return validationErrors.stream()
                .anyMatch(error -> field.equals(error.getField()));
    }

    /**
     * Build error message from list of validation errors
     * 
     * @param errors list of validation errors
     * @return combined error message
     */
    private static String buildMessageFromErrors(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "Validation failed";
        }

        if (errors.size() == 1) {
            ValidationError error = errors.get(0);
            if (error.getField() != null) {
                return String.format("Validation failed for field '%s': %s", error.getField(), error.getMessage());
            } else {
                return error.getMessage();
            }
        }

        StringBuilder sb = new StringBuilder("Validation failed for multiple fields: ");
        for (int i = 0; i < errors.size(); i++) {
            ValidationError error = errors.get(i);
            if (i > 0) {
                sb.append(", ");
            }
            if (error.getField() != null) {
                sb.append(error.getField()).append(" (").append(error.getMessage()).append(")");
            } else {
                sb.append(error.getMessage());
            }
        }
        return sb.toString();
    }

    /**
     * Build error message from error map
     * 
     * @param errorMap map of field names to error messages
     * @return combined error message
     */
    private static String buildMessageFromMap(Map<String, String> errorMap) {
        if (errorMap == null || errorMap.isEmpty()) {
            return "Validation failed";
        }

        if (errorMap.size() == 1) {
            Map.Entry<String, String> entry = errorMap.entrySet().iterator().next();
            return String.format("Validation failed for field '%s': %s", entry.getKey(), entry.getValue());
        }

        StringBuilder sb = new StringBuilder("Validation failed for multiple fields: ");
        int i = 0;
        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(" (").append(entry.getValue()).append(")");
            i++;
        }
        return sb.toString();
    }

    /**
     * Static factory method for required field validation
     * 
     * @param field the field name
     * @return ValidationException instance
     */
    public static ValidationException requiredField(String field) {
        return new ValidationException(field, "Field is required");
    }

    /**
     * Static factory method for invalid format validation
     * 
     * @param field the field name
     * @param expectedFormat the expected format
     * @return ValidationException instance
     */
    public static ValidationException invalidFormat(String field, String expectedFormat) {
        return new ValidationException(field, "Invalid format. Expected: " + expectedFormat);
    }

    /**
     * Static factory method for value out of range validation
     * 
     * @param field the field name
     * @param min minimum value
     * @param max maximum value
     * @return ValidationException instance
     */
    public static ValidationException outOfRange(String field, Object min, Object max) {
        return new ValidationException(field, String.format("Value must be between %s and %s", min, max));
    }

    /**
     * Static factory method for invalid value validation
     * 
     * @param field the field name
     * @param value the invalid value
     * @return ValidationException instance
     */
    public static ValidationException invalidValue(String field, Object value) {
        return new ValidationException(field, "Invalid value: " + value);
    }

    /**
     * Static factory method for duplicate value validation
     * 
     * @param field the field name
     * @param value the duplicate value
     * @return ValidationException instance
     */
    public static ValidationException duplicateValue(String field, Object value) {
        return new ValidationException(field, "Duplicate value: " + value);
    }

    /**
     * Inner class representing a single validation error
     */
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;

        /**
         * Constructor for validation error
         * 
         * @param field the field name (can be null for general errors)
         * @param message the error message
         */
        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        @Override
        public String toString() {
            if (field != null) {
                return String.format("%s: %s", field, message);
            } else {
                return message;
            }
        }
    }
}