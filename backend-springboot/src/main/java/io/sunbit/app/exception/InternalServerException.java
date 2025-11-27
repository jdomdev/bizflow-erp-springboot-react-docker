package io.sunbit.app.exception;

/**
 * Exception thrown when an internal server error occurs
 * Maps to HTTP 500 Internal Server Error
 * Used for database errors, unexpected runtime exceptions, etc.
 */
public class InternalServerException extends RuntimeException {

    private String errorCode;
    private String details;

    /**
     * Constructor with basic message
     */
    public InternalServerException(String message) {
        super(message);
    }

    /**
     * Constructor with error code and message
     */
    public InternalServerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code, message and details
     */
    public InternalServerException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Constructor with message and cause
     */
    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with error code, message and cause
     */
    public InternalServerException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}
