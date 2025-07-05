package com.gogidix.warehousing.logistics.service;

import com.gogidix.warehousing.logistics.model.TransferRequest;

import java.util.List;
import java.util.Map;

/**
 * Service interface for validating transfer requests
 */
public interface TransferValidationService {
    
    /**
     * Validate a transfer request
     *
     * @param transferRequest the transfer request to validate
     * @return validation result with any errors
     */
    ValidationResult validateTransferRequest(TransferRequest transferRequest);
    
    /**
     * Validate source and destination warehouses exist
     *
     * @param transferRequest the transfer request to validate
     * @return validation result with any errors
     */
    ValidationResult validateWarehouses(TransferRequest transferRequest);
    
    /**
     * Validate inventory items exist and have sufficient quantity
     *
     * @param transferRequest the transfer request to validate
     * @return validation result with any errors
     */
    ValidationResult validateInventory(TransferRequest transferRequest);
    
    /**
     * Class representing a validation result
     */
    class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final Map<String, Object> metadata;
        
        public ValidationResult(boolean valid, List<String> errors, Map<String, Object> metadata) {
            this.valid = valid;
            this.errors = errors;
            this.metadata = metadata;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
} 
