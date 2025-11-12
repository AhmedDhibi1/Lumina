package com.snapshot.lumina.businesslogic.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration for JWT-based authentication with Keycloak
 *
 * Features:
 * - Stateless session management
 * - Cookie-based JWT authentication
 * - CORS configuration for cross-origin requests
 * - Method-level security with @PreAuthorize
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF - not needed for stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        // Swagger/OpenAPI documentation - public access
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Error endpoint
                        .requestMatchers("/error").permitAll()

                        // All API endpoints require authentication
                        .requestMatchers("/api/v1/**").authenticated()

                        // Default: require authentication for all other endpoints
                        .anyRequest().authenticated()
                )

                // Stateless session - no session creation
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Custom authentication entry point for 401 errors
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // Add JWT filter before Spring Security's authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration for cross-origin requests
     *
     * IMPORTANT:
     * - allowCredentials(true) is REQUIRED for cookie-based authentication
     * - When using allowCredentials(true), you CANNOT use "*" for origins
     * - Must specify exact origins or use allowedOriginPatterns
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins - configure in application.yml for different environments
        // Development: http://localhost:3000
        // Production: https://yourdomain.com
        configuration.setAllowedOriginPatterns(allowedOrigins);

        // Allowed HTTP methods
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // Allowed headers - be specific for security
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));

        // Expose headers that the client can read
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        // CRITICAL: Required for cookies to be sent cross-origin
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}