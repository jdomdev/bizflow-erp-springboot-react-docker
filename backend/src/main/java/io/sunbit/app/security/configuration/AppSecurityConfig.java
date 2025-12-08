
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
    @org.springframework.beans.factory.annotation.Autowired
    private io.sunbit.app.security.jwt.JwtAuthenticationFilter jwtAuthFilter;

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
            // Allow signup and login without authentication
            .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/check-email", "/api/v1/auth/login").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            // All other requests require authentication
            .anyRequest().authenticated()
        );

        // Registrar el filtro JWT antes del UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
