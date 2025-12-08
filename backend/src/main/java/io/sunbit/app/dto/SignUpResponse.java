package io.sunbit.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for signup response
 * Contains user details after successful registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {

    /**
     * User ID
     */
    private Long id;

    /**
     * Name
     */
    private String name;

    /**
     * Surname
     */
    private String surname;

    /**
     * Email
     */
    private String email;

    /**
     * Success message
     */
    private String message;
}
