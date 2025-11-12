package com.snapshot.lumina.businesslogic.presentation.auth;

import com.snapshot.lumina.businesslogic.infrastructure.config.security.KeycloakUserPrincipal;
import com.snapshot.lumina.businesslogic.presentation.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for extracting authenticated user information from Spring Security context
 *
 * Best Practice: Always use SecurityContext instead of re-parsing tokens
 * The JwtAuthenticationFilter has already validated the token and stored user info
 */
@Service
@Slf4j
public class AuthenticationService {

    /**
     * Extract authenticated user ID from Spring Security context
     *
     * @return User ID (UUID)
     * @throws UnauthorizedException if user is not authenticated
     */
    public UUID getAuthenticatedUserId() {
        KeycloakUserPrincipal principal = getAuthenticatedPrincipal();
        try {
            return UUID.fromString(principal.getUserId());
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for user ID: {}", principal.getUserId(), e);
            throw new UnauthorizedException("Invalid user ID format");
        }
    }

    /**
     * Extract authenticated username from Spring Security context
     *
     * @return Username
     * @throws UnauthorizedException if user is not authenticated
     */
    public String getAuthenticatedUsername() {
        return getAuthenticatedPrincipal().getUsername();
    }

    /**
     * Extract authenticated user email from Spring Security context
     *
     * @return Email address
     * @throws UnauthorizedException if user is not authenticated
     */
    public String getAuthenticatedEmail() {
        return getAuthenticatedPrincipal().getEmail();
    }

    /**
     * Get the full principal with all user details
     *
     * @return KeycloakUserPrincipal
     * @throws UnauthorizedException if user is not authenticated
     */
    public KeycloakUserPrincipal getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found in security context");
            throw new UnauthorizedException("Authentication required. Please log in.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof KeycloakUserPrincipal)) {
            log.error("Unexpected principal type: {}", principal.getClass().getName());
            throw new UnauthorizedException("Invalid authentication principal");
        }

        return (KeycloakUserPrincipal) principal;
    }

    /**
     * Check if user has a specific role
     *
     * @param role Role name (without ROLE_ prefix)
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        try {
            return getAuthenticatedPrincipal().hasRole(role);
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    /**
     * Check if user has any of the specified roles
     *
     * @param roles Role names (without ROLE_ prefix)
     * @return true if user has any of the roles
     */
    public boolean hasAnyRole(String... roles) {
        try {
            return getAuthenticatedPrincipal().hasAnyRole(roles);
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    /**
     * Check if a user is authenticated
     *
     * @return true if authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof KeycloakUserPrincipal;
    }
}