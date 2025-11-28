package io.sunbit.app.exception;

/**
 * Exception thrown when request validation fails
 * Maps to HTTP 400 Bad Request
 * Used for invalid input, constraint violations, etc.
 */
public class BadRequestException extends RuntimeException {

    private String fieldName;
    private Object rejectedValue;
    private String reason;

    /**
     * Constructor with basic message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor with field details
     */
    public BadRequestException(String fieldName, Object rejectedValue, String reason) {
        super(String.format("Invalid value for field '%s': %s. Reason: %s", fieldName, rejectedValue, reason));
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
        this.reason = reason;
    }

    /**
     * Constructor with message and cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters
    public String getFieldName() {
        return fieldName;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public String getReason() {
        return reason;
    }
}
