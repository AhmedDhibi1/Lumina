package com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// domain/model/valueobject/workspace/WorkspaceRole.java
//@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkspaceRole {

    private static final Set<String> VALID_ROLES = Set.of("OWNER", "ADMIN", "MEMBER", "VIEWER");

    private String role;

    public WorkspaceRole(String role) {
        validateRole(role);
    }

    private void validateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        String upperRole = role.trim().toUpperCase();
        if (!VALID_ROLES.contains(upperRole)) {
            throw new IllegalArgumentException(
                    "Invalid role: " + role + ". Must be one of: " + VALID_ROLES);
        }

        this.role = upperRole;
    }

    // Permission checks
    public boolean isOwner() {
        return "OWNER".equals(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isMember() {
        return "MEMBER".equals(role);
    }

    public boolean isViewer() {
        return "VIEWER".equals(role);
    }

    // High-level permission methods
    public boolean canManageWorkspace() {
        return isOwner() || isAdmin();
    }

    public boolean canManageMembers() {
        return isOwner() || isAdmin();
    }

    public boolean canManageDocuments() {
        return isOwner() || isAdmin() || isMember();
    }

    public boolean canViewDocuments() {
        return true;  // All roles can view
    }

    public boolean hasHigherPrivilegesThan(WorkspaceRole other) {
        return getRoleLevel() > other.getRoleLevel();
    }

    private int getRoleLevel() {
        return switch (role) {
            case "OWNER" -> 4;
            case "ADMIN" -> 3;
            case "MEMBER" -> 2;
            case "VIEWER" -> 1;
            default -> 0;
        };
    }

    @Override
    public String toString() {
        return role;
    }
}