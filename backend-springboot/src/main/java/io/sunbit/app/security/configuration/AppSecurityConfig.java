
package io.sunbit.app.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// @EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true)
public class AppSecurityConfig {
    /*
     * @Autowired
     * private IUserDao userDao;
     * 
     * @Autowired
     * private JwtAuthenticationFilter jwtAuthFilter;
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Crear el bean de AuthenticationManager usando AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint = new CustomAuthenticationEntryPoint();

    // Inyección del AuthenticationEntryPoint personalizado
    public void SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    // Definir el SecurityFilterChain para configurar la seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configurationSource(request -> {
            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
            corsConfig.setAllowedOriginPatterns(java.util.List.of("*"));
            corsConfig.setAllowedMethods(java.util.List.of("*"));
            corsConfig.setAllowedHeaders(java.util.List.of("*"));
            corsConfig.setAllowCredentials(true);
            return corsConfig;
        }));
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(handling -> handling.authenticationEntryPoint(customAuthenticationEntryPoint));
        
        // Configure authorization rules
        http.authorizeHttpRequests(auth -> auth
            // Allow public endpoints
            .requestMatchers("/", "/health").permitAll()
            // Allow signup without authentication
            .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/check-username", "/api/v1/auth/check-email").permitAll()
            // Allow all other /api/v1/** endpoints (for now - will be secured later)
            .requestMatchers("/api/v1/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            // All other requests require authentication
            .anyRequest().authenticated()
        );
        
        return http.build();
    }
}
