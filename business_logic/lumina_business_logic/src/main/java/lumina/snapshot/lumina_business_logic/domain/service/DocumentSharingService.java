package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.entity.SharingHistory;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.Permissions;

import java.util.List;

public interface DocumentSharingService {

    // Share document with another user
    SharingHistory shareDocument(DocumentId documentId, UserId sharedBy, UserId sharedWith, Permissions permissions);

    // Share document with multiple users
    List<SharingHistory> shareWithMultipleUsers(DocumentId documentId, UserId sharedBy, List<UserId> recipients, Permissions permissions);

    // Revoke shared access
    void revokeSharing(DocumentId documentId, UserId sharedBy, UserId sharedWith);

    // Get sharing history for a document
    List<SharingHistory> getSharingHistory(DocumentId documentId);

    // Get all users who have access to a document
    List<UserId> getDocumentAccessors(DocumentId documentId);

    // Validate if user can share (must be owner)
    void validateSharingPermission(DocumentId documentId, UserId userId);

    // Update sharing permissions
    SharingHistory updateSharingPermissions(DocumentId documentId, UserId sharedBy, UserId sharedWith, Permissions newPermissions);
}