package io.sunbit.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response DTO for all API errors
 * Maps to HTTP error responses with consistent structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * HTTP status code (e.g., 404, 400, 500)
     */
    private int status;

    /**
     * Error message for client
     */
    private String message;

    /**
     * Short error code (e.g., "RESOURCE_NOT_FOUND", "INVALID_INPUT")
     */
    private String errorCode;

    /**
     * Detailed error information (for debugging)
     */
    private String details;

    /**
     * Timestamp when error occurred
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Path of the request that caused the error
     */
    private String path;

    /**
     * Builder method for quick construction
     */
    public static ErrorResponse of(int status, String message, String errorCode) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Builder method with full details
     */
    public static ErrorResponse of(int status, String message, String errorCode, String details, String path) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .details(details)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
