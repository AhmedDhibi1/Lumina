package com.snapshot.lumina.businesslogic.domain.model.aggregate;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.*;
import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.entity.DocumentACL;
import com.snapshot.lumina.businesslogic.domain.model.entity.DocumentMetadata;
import com.snapshot.lumina.businesslogic.domain.model.entity.DocumentOwnership;
import com.snapshot.lumina.businesslogic.domain.model.entity.DocumentVersion;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.AclId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.entity.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.Permission;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.Permission.PermissionName.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private DocumentId documentId;
    private Filename filename;
    private FilePath filePath;
    private FileSize fileSize;
    private MimeType mimeType;
    private PageCount pageCount;
    private UserId ownerId;
    private WorkspaceId workspaceId;
    private DocumentStatus status;
    private ChunkCount chunkCount;
    private EmbeddingModel embeddingModel ;
    private Timestamp indexedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private List<DocumentOwnership> ownerships;
    private List<DocumentACL> accessControlList;
    private DocumentMetadata metadata;
    private List<DocumentVersion> versions;

    public boolean isOwner(UserId userId) {
        return this.ownerId.equals(userId);
    }

    public void assignToWorkspace(WorkspaceId workspaceId, UserId assignedBy) {
        if (!this.ownerId.equals(assignedBy)) {
            throw new SecurityException("Only document owner can assign to workspace");
        }
        this.workspaceId = workspaceId;
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    public void removeFromWorkspace(UserId removedBy) {
        if (!this.ownerId.equals(removedBy)) {
            throw new SecurityException("Only document owner can remove from workspace");
        }
        this.workspaceId = null;
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }


    /**
     * Check if user can read this document
     * Rules: Owner OR has READ permission in valid ACL
     */
    public boolean canRead(UserId userId) {
        if (isOwner(userId)) {
            return true;
        }
        return hasPermissionInACL(userId, new Permission(null, READ));

    }

    /**
     * Check if user can write/update this document
     * Rules: Owner OR has WRITE permission in valid ACL
     */
    public boolean canWrite(UserId userId) {
        if (isOwner(userId)) {
            return true;
        }
        return hasPermissionInACL(userId, new Permission(null, WRITE));
    }

    /**
     * Check if user can delete this document
     * Rules: Owner only
     */
    public boolean canDelete(UserId userId) {
        return isOwner(userId);
    }

    /**
     * Check if user can share/manage permissions
     * Rules: Owner only
     */
    public boolean canShare(UserId userId) {
        return isOwner(userId);
    }

    /**
     * Generic access check for any permission
     */
    public boolean canAccess(UserId userId, Permission requiredPermission) {
        return switch (requiredPermission.getPermissionName().value()) {
            case "READ" -> canRead(userId);
            case "WRITE" -> canWrite(userId);
            case "DELETE" -> canDelete(userId);
            case "SHARE" -> canShare(userId);
            default -> throw new IllegalStateException(
                    "Unexpected permission: " + requiredPermission.getPermissionName().value()
            );
        };
    }



    /**
     * Get all effective permissions for a user
     * Combines ownership (all permissions) and ACL permissions
     */
    public List<Permission> getEffectivePermissions(UserId userId) {
        if (isOwner(userId)) {
            return List.of(
                    new Permission(null, Permission.PermissionName.READ),
                    new Permission(null, Permission.PermissionName.WRITE),
                    new Permission(null, Permission.PermissionName.DELETE),
                    new Permission(null, Permission.PermissionName.SHARE)
            );
        }

        return accessControlList.stream()
                .filter(acl -> acl.getUserId().equals(userId))
                .filter(DocumentACL::isValid) // Only valid (non-expired) ACLs
                .flatMap(acl -> acl.getPermissions().stream())
                .distinct() // relies on equals/hashCode in Permission
                .collect(Collectors.toList());
    }


    /**
     * Enforce access control - throws exception if denied
     */
    public void enforceAccess(UserId userId, Permission requiredPermission) {
        if (!canAccess(userId, requiredPermission)) {
            throw new SecurityException(
                    String.format("User %s does not have %s permission on document %s",
                            userId.getValue(), requiredPermission, documentId.getValue())
            );
        }
    }


    /**
     * Grant permissions to a user
     */
    public void grantPermissions(UserId targetUserId, List<Permission> permissions, UserId grantedBy) {
        enforceAccess(grantedBy,new Permission(null,new Permission.PermissionName("SHARE")));

        if (targetUserId.equals(this.ownerId)) {
            throw new IllegalArgumentException("Cannot modify owner's permissions");
        }

        // Check if ACL entry exists
        Optional<DocumentACL> existingACL = findACLEntry(targetUserId);

        if (existingACL.isPresent()) {
            existingACL.get().addPermissions(permissions);
        } else {
            DocumentACL newACL = DocumentACL.builder()
                    .aclId(AclId.builder().value(UUID.randomUUID()).build())
                    .documentId(this.documentId)
                    .userId(targetUserId)
                    .permissions(new ArrayList<>(permissions))
                    .grantedBy(grantedBy)
                    .grantedAt(Timestamp.builder().value(LocalDateTime.now()).build())
                    .build();

            if (this.accessControlList == null) {
                this.accessControlList = new ArrayList<>();
            }
            this.accessControlList.add(newACL);
        }

        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    /**
     * Grant single permission to a user
     */
    public void grantPermission(UserId targetUserId, Permission permission, UserId grantedBy) {
        grantPermissions(targetUserId, List.of(permission), grantedBy);
    }

    /**
     * Revoke permissions from a user
     */
    public void revokePermissions(UserId targetUserId, List<Permission> permissions, UserId revokedBy) {
        enforceAccess(revokedBy, new Permission(null,new Permission.PermissionName("SHARE")));

        if (targetUserId.equals(this.ownerId)) {
            throw new IllegalArgumentException("Cannot modify owner's permissions");
        }

        DocumentACL acl = findACLEntry(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("No ACL entry found for user"));

        acl.removePermissions(permissions);

        // Remove ACL entry if no permissions left
        if (acl.getPermissions().isEmpty()) {
            this.accessControlList.remove(acl);
        }

        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    /**
     * Remove all permissions for a user
     */
    public void revokeAllPermissions(UserId targetUserId, UserId revokedBy) {
        enforceAccess(revokedBy, new Permission(null,new Permission.PermissionName("SHARE")));

        this.accessControlList.removeIf(acl -> acl.getUserId().equals(targetUserId));
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    /**
     * Initialize document after upload
     */
    public static Document initializeDocument(
            Filename filename,
            FilePath filePath,
            FileSize fileSize,
            MimeType mimeType,
            UserId ownerId,
            WorkspaceId workspaceId) {

        LocalDateTime now = LocalDateTime.now();
        DocumentStatus pendingStatus= DocumentStatus.pending();

        return Document.builder()
                .documentId(DocumentId.builder().value(UUID.randomUUID()).build())
                .filename(filename)
                .filePath(filePath)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .ownerId(ownerId)
                .workspaceId(workspaceId)
                .status(pendingStatus)
                .createdAt(Timestamp.builder().value(now).build())
                .updatedAt(Timestamp.builder().value(now).build())
                .accessControlList(new ArrayList<>())
                .ownerships(new ArrayList<>())
                .versions(new ArrayList<>())
                .build();
    }

    /**
     * Mark document as processing
     */
    public void markAsProcessing() {
        DocumentStatus newStatus = DocumentStatus.processing();
        this.status.validateTransition(newStatus);  // ✅ Validated
        this.status = newStatus;  // ✅ New immutable instance
        this.updatedAt = Timestamp.now();
    }

    /**
     * Mark document as indexed (after embeddings created)
     */
    public void markAsIndexed(ChunkCount chunkCount, EmbeddingModel model) {
        DocumentStatus newStatus = DocumentStatus.indexed();
        this.status.validateTransition(newStatus);  // ✅ Validated
        this.status = newStatus;  // ✅ New immutable instance
        this.chunkCount = chunkCount;
        this.embeddingModel = model;
        this.indexedAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    /**
     * Mark document as failed
     */
    public void markAsFailed(String errorMessage) {
        DocumentStatus newStatus = DocumentStatus.failed();
        this.status.validateTransition(newStatus);  // ✅ Validated
        this.status = newStatus;  // ✅ New immutable instance
        this.updatedAt = Timestamp.now();
    }

    /**
     * Archive document (soft delete)
     */
    public void archiveDocument(UserId requesterId) {
        DocumentStatus newStatus = DocumentStatus.archived();
        this.status.validateTransition(newStatus);  // ✅ Validated
        this.status = newStatus;  // ✅ New immutable instance
        this.updatedAt = Timestamp.now();
    }

    /**
     * Restore archived document
     */
    public void restoreDocument(UserId requesterId) {
        if (!isOwner(requesterId)) {
            throw new SecurityException("Only owner can restore document");
        }

        if (!this.status.isArchived()) {
            throw new IllegalStateException("Only archived documents can be restored");
        }
        DocumentStatus newStatus = DocumentStatus.pending();
        this.status.validateTransition(newStatus);  // ✅ Validated
        this.status = newStatus;
        this.updatedAt = Timestamp.now();
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    private boolean hasPermissionInACL(UserId userId, Permission permission) {
        if (accessControlList == null) {
            return false;
        }

        return accessControlList.stream()
                .filter(acl -> acl.getUserId().equals(userId))
                .filter(DocumentACL::isValid) // Only check valid (non-expired) ACLs
                .anyMatch(acl -> acl.hasPermission(permission));
    }

    private Optional<DocumentACL> findACLEntry(UserId userId) {
        if (accessControlList == null) {
            return Optional.empty();
        }

        return accessControlList.stream()
                .filter(acl -> acl.getUserId().equals(userId))
                .findFirst();
    }

    // Validate file upload
    //void validateUpload(Filename filename, FileSize fileSize, MimeType mimeType);
    // Check for duplicate documents
    //Boolean isDuplicate(UserId userId, Filename filename, FileSize fileSize);
    // Validate document state for operation
    //void validateDocumentState(DocumentId documentId, DocumentStatus requiredStatus);




}
