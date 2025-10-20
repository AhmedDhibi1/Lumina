package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.PermissionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

import java.util.List;

public interface DocumentAccessControlService {

    // Comprehensive access check (combines ownership and permissions)
    Boolean canAccess(DocumentId documentId, UserId userId, PermissionId requiredPermission);

    // Check if user can read document
    Boolean canRead(DocumentId documentId, UserId userId);

    // Check if user can write/update document
    Boolean canWrite(DocumentId documentId, UserId userId);

    // Check if user can delete document (owner only)
    Boolean canDelete(DocumentId documentId, UserId userId);

    // Check if user can share document (owner only)
    Boolean canShare(DocumentId documentId, UserId userId);

    // Get effective permissions (combining ownership and ACL)
    List<PermissionId> getEffectivePermissions(DocumentId documentId, UserId userId);

    // Enforce access control (throws exception if denied)
    void enforceAccess(DocumentId documentId, UserId userId, PermissionId requiredPermission);
}
