package com.exalt.warehousing.shared.exception;

import lombok.Getter;

/**
 * Base exception class for all warehousing domain exceptions
 * 
 * This class serves as the parent for all custom exceptions in the warehousing domain,
 * providing common functionality like error codes, timestamps, and context information.
 */
@Getter
public class WarehouseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code for categorizing the exception
     */
    private final String errorCode;

    /**
     * Timestamp when the exception occurred
     */
    private final long timestamp;

    /**
     * Context information about where the exception occurred
     */
    private final String context;

    /**
     * Additional details about the exception
     */
    private final Object details;

    /**
     * Constructor with message only
     * 
     * @param message the error message
     */
    public WarehouseException(String message) {
        super(message);
        this.errorCode = "WAREHOUSE_ERROR";
        this.timestamp = System.currentTimeMillis();
        this.context = null;
        this.details = null;
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public WarehouseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "WAREHOUSE_ERROR";
        this.timestamp = System.currentTimeMillis();
        this.context = null;
        this.details = null;
    }

    /**
     * Constructor with message and error code
     * 
     * @param message the error message
     * @param errorCode the error code
     */
    public WarehouseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
        this.context = null;
        this.details = null;
    }

    /**
     * Constructor with message, error code, and cause
     * 
     * @param message the error message
     * @param errorCode the error code
     * @param cause the underlying cause
     */
    public WarehouseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
        this.context = null;
        this.details = null;
    }

    /**
     * Full constructor with all parameters
     * 
     * @param message the error message
     * @param errorCode the error code
     * @param context context information
     * @param details additional details
     * @param cause the underlying cause
     */
    public WarehouseException(String message, String errorCode, String context, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
        this.context = context;
        this.details = details;
    }

    /**
     * Get a formatted error message including all available information
     * 
     * @return formatted error message
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorCode).append("] ");
        sb.append(getMessage());
        
        if (context != null && !context.trim().isEmpty()) {
            sb.append(" (Context: ").append(context).append(")");
        }
        
        if (details != null) {
            sb.append(" (Details: ").append(details.toString()).append(")");
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(getFormattedMessage());
        
        if (getCause() != null) {
            sb.append(" caused by ").append(getCause().toString());
        }
        
        return sb.toString();
    }
}