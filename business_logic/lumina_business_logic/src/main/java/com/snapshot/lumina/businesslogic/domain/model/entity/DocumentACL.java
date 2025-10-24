package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.AclId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.Permission;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentACL {
    private AclId aclId;
    private DocumentId documentId;
    private UserId userId;
    private List<Permission> permissions;
    private UserId grantedBy;
    private Timestamp grantedAt;
    private Timestamp expiresAt;

    // Check if user has specific permission on document
    public boolean hasPermission(Permission permission) {
        return permissions != null && permissions.contains(permission);
    }

    // Check if user has any of the specified permissions
    public boolean hasAnyPermission(List<Permission> requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        return requiredPermissions.stream().anyMatch(permissions::contains);
    }

    // Check if user has all specified permissions
    public boolean hasAllPermissions(List<Permission> requiredPermissions) {
        if (permissions == null || requiredPermissions == null) {
            return false;
        }
        return permissions.containsAll(requiredPermissions);
    }

    // Add permissions to this ACL entry
    public void addPermissions(List<Permission> newPermissions) {
        if (this.permissions == null) {
            this.permissions = new ArrayList<>();
        }

        for (Permission p : newPermissions) {
            if (!this.permissions.contains(p)) {
                this.permissions.add(p);
            }
        }
    }

    public void removePermissions(List<Permission> permissionsToRemove) {
        if (this.permissions != null) {
            this.permissions.removeAll(permissionsToRemove);
        }
    }

    public boolean isExpired() {
        if (expiresAt == null) {
            return false; // No expiration
        }
        return expiresAt.getValue().isBefore(java.time.LocalDateTime.now());
    }

    public boolean isValid() {
        return !isExpired();
    }
}
