package com.snapshot.lumina.businesslogic.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Value("${jwt.cookie.access-token.name:accessToken}")
    private String accessTokenCookieName;

    @Value("${jwt.cookie.refresh-token.name:refreshToken}")
    private String refreshTokenCookieName;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extract JWT from cookie
            String jwt = extractJwtFromCookie(request, accessTokenCookieName);

            if (StringUtils.hasText(jwt)) {
                // Validate token
                if (jwtValidator.validateToken(jwt)) {
                    // Extract user details from token
                    String userId = jwtValidator.getUserIdFromToken(jwt);
                    String username = jwtValidator.getUsernameFromToken(jwt);
                    String email = jwtValidator.getEmailFromToken(jwt);

                    // Extract roles and create authorities
                    List<String> roles = jwtValidator.getAllRolesFromToken(jwt);
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList());

                    // Create custom principal with user details
                    KeycloakUserPrincipal principal = KeycloakUserPrincipal.builder()
                            .userId(userId)
                            .username(username)
                            .email(email)
                            .roles(roles)
                            .build();

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    jwt, // Store token as credentials for later use if needed
                                    authorities
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Successfully authenticated user: {} (ID: {})", username, userId);
                } else {
                    log.debug("Invalid JWT token in request");
                    // Don't throw exception, let the request proceed without authentication
                    // Spring Security will handle unauthorized access
                }
            } else {
                log.debug("No JWT token found in cookies");
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
            // Clear security context on error
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT from cookie
     */
    private String extractJwtFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    /**
     * Check if request path should skip authentication
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Skip filter for public endpoints
        return path.startsWith("/actuator/health") ||
                path.startsWith("/actuator/info") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/error");
    }
}