package io.sunbit.app.exception;

/**
 * Exception thrown when authentication fails or is missing
 * Maps to HTTP 401 Unauthorized
 * Used for invalid credentials, missing/expired JWT tokens, etc.
 */
public class UnauthorizedException extends RuntimeException {

    private String reason;

    /**
     * Constructor with basic message
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructor with reason
     */
    public UnauthorizedException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    /**
     * Constructor with message and cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters
    public String getReason() {
        return reason;
    }
}
