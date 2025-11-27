package io.sunbit.app.security.controller;

import io.sunbit.app.dto.SignUpRequest;
import io.sunbit.app.dto.SignUpResponse;
import io.sunbit.app.exception.BadRequestException;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.security.entity.Role;
import io.sunbit.app.security.dao.IRoleDao;
import io.sunbit.app.security.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Authentication Controller
 * Handles signup and authentication-related endpoints
 * These endpoints are public (no authentication required)
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleDao roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Signup endpoint - Create new user account
     * 
     * POST /api/v1/auth/signup
     * No authentication required
     * 
     * @param signUpRequest containing username, email, password
     * @return SignUpResponse with user details
     * @throws BadRequestException if username or email already exists
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            log.info("Processing signup for username: {}", signUpRequest.getUsername());

            // Validate email not already taken
            Optional<ExpenseUser> existingUser = userService.findByEmail(signUpRequest.getEmail());
            if (existingUser.isPresent()) {
                log.warn("Signup failed - email already registered: {}", signUpRequest.getEmail());
                throw new BadRequestException("email", signUpRequest.getEmail(), 
                    "Email already registered");
            }

            // Create new user
            // Note: ExpenseUser requires name, surname, email, password
            ExpenseUser newUser = new ExpenseUser();
            newUser.setName(signUpRequest.getUsername());
            newUser.setSurname(signUpRequest.getUsername()); // Use username as surname
            newUser.setEmail(signUpRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            // isEnabled() always returns true in ExpenseUser

            // Assign default USER role
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        log.warn("USER role not found, creating it");
                        Role newRole = new Role("USER");
                        return roleRepository.save(newRole);
                    });

            Collection<Role> roles = new ArrayList<>();
            roles.add(userRole);
            newUser.setRoles(roles);

            // Save user
            ExpenseUser savedUser = userService.save(newUser);
            log.info("User registered successfully - username: {}, email: {}", 
                savedUser.getName(), savedUser.getEmail());

            // Return response
            SignUpResponse response = SignUpResponse.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getName())
                    .email(savedUser.getEmail())
                    .message("User registered successfully")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BadRequestException e) {
            log.warn("Bad request during signup: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during signup", e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new BadRequestException("signup", "unknown", "Error during registration: " + errorMessage);
        }
    }

    /**
     * Check if username is available
     * 
     * GET /api/v1/auth/check-username?username=john
     * No authentication required
     * 
     * @param username to check
     * @return true if available, false if taken
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            Optional<ExpenseUser> user = userService.findByEmail(username + "@temp");
            boolean available = user.isEmpty();
            return ResponseEntity.ok().body(new CheckAvailabilityResponse(available));
        } catch (Exception e) {
            log.error("Error checking username availability", e);
            return ResponseEntity.ok().body(new CheckAvailabilityResponse(false));
        }
    }

    /**
     * Check if email is available
     * 
     * GET /api/v1/auth/check-email?email=user@example.com
     * No authentication required
     * 
     * @param email to check
     * @return true if available, false if taken
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            Optional<ExpenseUser> user = userService.findByEmail(email);
            boolean available = user.isEmpty();
            return ResponseEntity.ok().body(new CheckAvailabilityResponse(available));
        } catch (Exception e) {
            log.error("Error checking email availability", e);
            return ResponseEntity.ok().body(new CheckAvailabilityResponse(false));
        }
    }

    /**
     * Simple DTO for availability checks
     */
    public static class CheckAvailabilityResponse {
        public boolean available;

        public CheckAvailabilityResponse(boolean available) {
            this.available = available;
        }

        public boolean isAvailable() {
            return available;
        }
    }
}
