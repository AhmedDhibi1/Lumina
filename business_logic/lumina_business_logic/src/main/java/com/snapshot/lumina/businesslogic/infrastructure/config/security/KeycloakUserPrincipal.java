package com.snapshot.lumina.businesslogic.infrastructure.config.security;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

/**
 * Custom principal to hold Keycloak user information
 * This can be accessed in controllers via @AuthenticationPrincipal
 */
@Data
@Builder
public class KeycloakUserPrincipal implements Principal, Serializable {

    private String userId;        // sub claim
    private String username;      // preferred_username claim
    private String email;         // email claim
    private List<String> roles;   // realm_access + resource_access roles

    @Override
    public String getName() {
        return username;
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Check if user has any of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        if (this.roles == null) {
            return false;
        }
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
