package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentACL;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.PermissionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.Permissions;

import java.util.List;

public interface DocumentPermissionService {

    // Check if user has specific permission on document
    Boolean hasPermission(DocumentId documentId, UserId userId, PermissionId permissionId);

    // Check if user has any of the specified permissions
    Boolean hasAnyPermission(DocumentId documentId, UserId userId, List<PermissionId> permissionIds);

    // Check if user has all specified permissions
    Boolean hasAllPermissions(DocumentId documentId, UserId userId, List<PermissionId> permissionIds);

    // Get all permissions a user has on a document
    List<Permissions> getUserPermissions(DocumentId documentId, UserId userId);

    // Grant permission to user (only by owner)
    DocumentACL grantPermission(DocumentId documentId, UserId userId, PermissionId permissionId, UserId grantedBy);

    // Grant a List of permissions to user (only by owner)
    DocumentACL grantPermissionList(DocumentId documentId, UserId userId, List<PermissionId> permissionIdList, UserId grantedBy);

    // Revoke permission from user
    void revokePermission(DocumentId documentId, UserId userId, PermissionId permissionId);

    // Revoke all permissions for a user on a document
    void revokeAllPermissions(DocumentId documentId, UserId userId);

    // Check if permissions are expired
    Boolean isPermissionExpired(DocumentACL acl);

    // Clean up expired permissions
    void cleanupExpiredPermissions();
}