package io.sunbit.app.config;

import io.sunbit.app.entity.Role;
import io.sunbit.app.entity.User;
import io.sunbit.app.security.dao.RoleRepository;
import io.sunbit.app.security.dao.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * DataLoader Component
 * Automatically loads initial data (roles) on application startup
 * Security: Only creates roles, NOT admin user with hardcoded credentials
 * Admin user should be created via:
 *   1. Signup endpoint (POST /api/auth/signup)
 *   2. Manual database insertion with secure password
 *   3. Environment variables or config management
 */
@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("========== Starting DataLoader ==========");

            // Create default roles (idempotent - safe to run multiple times)
            long roleCount = roleRepository.count();
            if (roleCount == 0) {
                log.info("No roles found. Creating default roles...");
                createDefaultRoles();
                log.info("✓ Default roles created successfully");
            } else {
                log.info("Roles already exist. Skipping role creation.");
            }

            // Security: Do NOT create admin user here
            // Use signup endpoint or manual setup instead
            long userCount = userRepository.count();
            if (userCount == 0) {
                log.warn("========== IMPORTANT SETUP INSTRUCTIONS ==========");
                log.warn("No users found in database. To create first admin user:");
                log.warn("1. Use POST /api/auth/signup endpoint (no auth required)");
                log.warn("2. Then assign ADMIN role via database");
                log.warn("3. Or manually insert user with secure password");
                log.warn("⚠️  NEVER hardcode admin credentials in code or files");
                log.warn("================================================");
            }

            log.info("========== DataLoader Completed Successfully ==========");
        } catch (Exception e) {
            log.error("Error during DataLoader execution", e);
            throw new RuntimeException("DataLoader failed to initialize application", e);
        }
    }

    /**
     * Creates default roles required by the application
     * Idempotent operation - safe to run multiple times
     */
    private void createDefaultRoles() {
        try {
            // Create ADMIN role
            if (!roleRepository.findByName("ADMIN").isPresent()) {
                Role adminRole = Role.builder()
                        .name("ADMIN")
                        .description("Administrator with full permissions")
                        .build();
                roleRepository.save(adminRole);
                log.info("✓ ADMIN role created");
            }

            // Create USER role
            if (!roleRepository.findByName("USER").isPresent()) {
                Role userRole = Role.builder()
                        .name("USER")
                        .description("Regular user with standard permissions")
                        .build();
                roleRepository.save(userRole);
                log.info("✓ USER role created");
            }

            // Create MANAGER role
            if (!roleRepository.findByName("MANAGER").isPresent()) {
                Role managerRole = Role.builder()
                        .name("MANAGER")
                        .description("Manager with team oversight permissions")
                        .build();
                roleRepository.save(managerRole);
                log.info("✓ MANAGER role created");
            }

        } catch (Exception e) {
            log.error("Error creating default roles", e);
            throw e;
        }
    }
}
