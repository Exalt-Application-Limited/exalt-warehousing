package com.gogidix.warehousing.shared.exception;

/**
 * Exception thrown when service integration operations fail
 */
public class ServiceIntegrationException extends RuntimeException {

    public ServiceIntegrationException(String message) {
        super(message);
    }

    public ServiceIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceIntegrationException(Throwable cause) {
        super(cause);
    }
}
