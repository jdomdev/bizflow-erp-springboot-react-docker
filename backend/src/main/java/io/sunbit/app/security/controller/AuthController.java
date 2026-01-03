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
import java.util.Collections;

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
         * @param signUpRequest containing name, surname, email, password
     * @return SignUpResponse with user details
         * @throws BadRequestException if email already exists
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            log.info("Processing signup for email: {}", signUpRequest.getEmail());

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
            newUser.setName(signUpRequest.getName());
            newUser.setSurname(signUpRequest.getSurname());
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

            // Save user (pass roleIds)
            java.util.List<Long> roleIds = java.util.Collections.singletonList(userRole.getId());
            ExpenseUser savedUser = userService.save(newUser, roleIds);
            log.info("User registered successfully - name: {}, surname: {}, email: {}", 
                savedUser.getName(), savedUser.getSurname(), savedUser.getEmail());

            // Return response
                SignUpResponse response = SignUpResponse.builder()
                    .id(savedUser.getId())
                    .name(savedUser.getName())
                    .surname(savedUser.getSurname())
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
            return ResponseEntity.ok().body(Collections.singletonMap("available", available));
        } catch (Exception e) {
            log.error("Error checking email availability", e);
            return ResponseEntity.ok().body(Collections.singletonMap("available", false));
        }
    }



}
