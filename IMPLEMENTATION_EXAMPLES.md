# Implementation Examples - Critical Fixes

This document provides ready-to-use code examples for fixing the most critical DDD issues identified in the code review.

---

## 1. Value Object Transformation Examples

### Example 1: DocumentId (Identity Value Object)

**Before:**
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentId {
    private UUID value;
}
```

**After:**
```java
import lombok.Value;
import java.util.UUID;
import java.util.Objects;

@Value // Immutable - all fields final and private
public class DocumentId {
    UUID value;
    
    private DocumentId(UUID value) {
        this.value = Objects.requireNonNull(value, "DocumentId cannot be null");
    }
    
    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }
    
    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }
    
    public static DocumentId fromString(String value) {
        try {
            return new DocumentId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DocumentId format: " + value, e);
        }
    }
}
```

### Example 2: Email (With Validation)

**Before:**
```java
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Email {
    private String value;
}
```

**After:**
```java
import lombok.Value;
import java.util.Objects;
import java.util.regex.Pattern;

@Value
public class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    String value;
    
    private Email(String value) {
        this.value = value;
    }
    
    public static Email of(String value) {
        Objects.requireNonNull(value, "Email cannot be null");
        String normalized = value.trim().toLowerCase();
        
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        
        return new Email(normalized);
    }
    
    public String getDomain() {
        int atIndex = value.indexOf('@');
        return value.substring(atIndex + 1);
    }
    
    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        return value.substring(0, atIndex);
    }
    
    public boolean isFromDomain(String domain) {
        return getDomain().equalsIgnoreCase(domain);
    }
}
```

### Example 3: Filename (With Validation and Behavior)

**Before:**
```java
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Filename {
    private String value;
}
```

**After:**
```java
import lombok.Value;
import java.util.Objects;
import java.util.Set;

@Value
public class Filename {
    private static final int MAX_LENGTH = 255;
    private static final Set<String> FORBIDDEN_CHARS = Set.of("/", "\\", ":", "*", "?", "\"", "<", ">", "|");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "txt", "md", "csv", "xlsx", "ppt", "pptx"
    );
    
    String value;
    
    private Filename(String value) {
        this.value = value;
    }
    
    public static Filename of(String value) {
        Objects.requireNonNull(value, "Filename cannot be null");
        String trimmed = value.trim();
        
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Filename too long (max %d characters): %s", MAX_LENGTH, value)
            );
        }
        
        // Check for forbidden characters
        for (String forbidden : FORBIDDEN_CHARS) {
            if (trimmed.contains(forbidden)) {
                throw new IllegalArgumentException(
                    String.format("Filename contains forbidden character '%s': %s", forbidden, value)
                );
            }
        }
        
        // Validate extension
        String extension = getExtensionFrom(trimmed);
        if (extension.isEmpty()) {
            throw new IllegalArgumentException("Filename must have an extension: " + value);
        }
        
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                String.format("Unsupported file extension '%s'. Allowed: %s", 
                    extension, ALLOWED_EXTENSIONS)
            );
        }
        
        return new Filename(trimmed);
    }
    
    public String getExtension() {
        return getExtensionFrom(value);
    }
    
    public String getNameWithoutExtension() {
        int lastDot = value.lastIndexOf('.');
        return lastDot > 0 ? value.substring(0, lastDot) : value;
    }
    
    public boolean hasExtension(String extension) {
        return getExtension().equalsIgnoreCase(extension);
    }
    
    public Filename withNewExtension(String newExtension) {
        String nameWithoutExt = getNameWithoutExtension();
        return Filename.of(nameWithoutExt + "." + newExtension);
    }
    
    private static String getExtensionFrom(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 && lastDot < filename.length() - 1 
            ? filename.substring(lastDot + 1) 
            : "";
    }
}
```

### Example 4: FileSize (With Behavior and Business Rules)

**Before:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSize {
    private Long bytes;
}
```

**After:**
```java
import lombok.Value;

@Value
public class FileSize {
    private static final long MAX_FILE_SIZE_BYTES = 100L * 1024 * 1024; // 100 MB
    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = KILOBYTE * 1024;
    private static final long GIGABYTE = MEGABYTE * 1024;
    
    long bytes;
    
    private FileSize(long bytes) {
        this.bytes = bytes;
    }
    
    public static FileSize ofBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + bytes);
        }
        if (bytes > MAX_FILE_SIZE_BYTES) {
            throw new FileSizeExceededException(
                String.format("File size %d bytes exceeds maximum allowed %d bytes", 
                    bytes, MAX_FILE_SIZE_BYTES)
            );
        }
        return new FileSize(bytes);
    }
    
    public static FileSize ofKilobytes(long kb) {
        return ofBytes(kb * KILOBYTE);
    }
    
    public static FileSize ofMegabytes(long mb) {
        return ofBytes(mb * MEGABYTE);
    }
    
    public static FileSize zero() {
        return new FileSize(0);
    }
    
    public double toKilobytes() {
        return (double) bytes / KILOBYTE;
    }
    
    public double toMegabytes() {
        return (double) bytes / MEGABYTE;
    }
    
    public double toGigabytes() {
        return (double) bytes / GIGABYTE;
    }
    
    public String toHumanReadable() {
        if (bytes < KILOBYTE) {
            return bytes + " B";
        } else if (bytes < MEGABYTE) {
            return String.format("%.2f KB", toKilobytes());
        } else if (bytes < GIGABYTE) {
            return String.format("%.2f MB", toMegabytes());
        } else {
            return String.format("%.2f GB", toGigabytes());
        }
    }
    
    public boolean isLargerThan(FileSize other) {
        return this.bytes > other.bytes;
    }
    
    public boolean isSmallerThan(FileSize other) {
        return this.bytes < other.bytes;
    }
    
    public FileSize add(FileSize other) {
        return ofBytes(this.bytes + other.bytes);
    }
    
    public FileSize subtract(FileSize other) {
        return ofBytes(this.bytes - other.bytes);
    }
}

// Custom exception
public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(String message) {
        super(message);
    }
}
```

---

## 2. Enum Transformation Examples

### Example 1: DocumentStatus

**Before:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatus {
    private String status; // UPLOADED, PROCESSING, INDEXED, FAILED
}
```

**After:**
```java
public enum DocumentStatus {
    UPLOADED("Document uploaded, awaiting processing"),
    PROCESSING("Document is being processed and indexed"),
    INDEXED("Document successfully processed and indexed"),
    FAILED("Document processing failed");
    
    private final String description;
    
    DocumentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTerminal() {
        return this == INDEXED || this == FAILED;
    }
    
    public boolean canTransitionTo(DocumentStatus newStatus) {
        return switch (this) {
            case UPLOADED -> newStatus == PROCESSING;
            case PROCESSING -> newStatus == INDEXED || newStatus == FAILED;
            case FAILED -> newStatus == PROCESSING; // Allow retry
            case INDEXED -> false; // Terminal state - no transitions
        };
    }
    
    public void validateTransition(DocumentStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(
                String.format("Cannot transition from %s to %s", this, newStatus)
            );
        }
    }
}

// Custom exception
public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
```

### Example 2: WorkspaceRole

**Before:**
```java
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkspaceRole {
    private String role; // ADMIN, MEMBER, VIEWER
}
```

**After:**
```java
import java.util.Set;

public enum WorkspaceRole {
    ADMIN("Administrator", Set.of(
        Permission.READ, Permission.WRITE, Permission.DELETE, 
        Permission.SHARE, Permission.MANAGE_MEMBERS
    )),
    MEMBER("Member", Set.of(
        Permission.READ, Permission.WRITE, Permission.SHARE
    )),
    VIEWER("Viewer", Set.of(
        Permission.READ
    ));
    
    private final String displayName;
    private final Set<Permission> permissions;
    
    WorkspaceRole(String displayName, Set<Permission> permissions) {
        this.displayName = displayName;
        this.permissions = permissions;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Set<Permission> getPermissions() {
        return Set.copyOf(permissions); // Return immutable copy
    }
    
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    
    public boolean canManageMembers() {
        return this == ADMIN;
    }
    
    public boolean canDelete() {
        return hasPermission(Permission.DELETE);
    }
    
    public boolean isHigherThan(WorkspaceRole other) {
        return this.ordinal() < other.ordinal(); // Lower ordinal = higher privilege
    }
}

// Permission enum
public enum Permission {
    READ,
    WRITE,
    DELETE,
    SHARE,
    MANAGE_MEMBERS
}
```

### Example 3: MessageRole

**Before:**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRole {
    private String role; // USER, ASSISTANT
}
```

**After:**
```java
public enum MessageRole {
    USER("User", "Message from the user"),
    ASSISTANT("Assistant", "Response from the AI assistant"),
    SYSTEM("System", "System-generated message");
    
    private final String displayName;
    private final String description;
    
    MessageRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFromUser() {
        return this == USER;
    }
    
    public boolean isFromAssistant() {
        return this == ASSISTANT;
    }
    
    public boolean isSystem() {
        return this == SYSTEM;
    }
}
```

---

## 3. Aggregate Transformation Example

### Document Aggregate - Adding Behavior

**Before (Anemic):**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private DocumentId documentId;
    private Filename filename;
    private FilePath filePath;
    private FileSize fileSize;
    private DocumentStatus status;
    private UserId ownerId;
    private WorkspaceId workspaceId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<DocumentVersion> versions;
    private List<DocumentACL> accessControlList;
}
```

**After (Rich Domain Model):**
```java
import lombok.Getter;
import lombok.AccessLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class Document {
    private final DocumentId documentId;
    private Filename filename;
    private FilePath filePath;
    private FileSize fileSize;
    private MimeType mimeType;
    private DocumentStatus status;
    private final UserId ownerId;
    private final WorkspaceId workspaceId;
    private final Timestamp createdAt;
    private Timestamp updatedAt;
    
    @Getter(AccessLevel.NONE)
    private final List<DocumentVersion> versions = new ArrayList<>();
    
    @Getter(AccessLevel.NONE)
    private final List<DocumentACL> accessControlList = new ArrayList<>();
    
    // Protected constructor for JPA/Hibernate
    protected Document() {
        this.documentId = null;
        this.ownerId = null;
        this.workspaceId = null;
        this.createdAt = null;
    }
    
    // Private constructor - use factory methods
    private Document(DocumentId documentId, Filename filename, 
                    UserId ownerId, WorkspaceId workspaceId) {
        this.documentId = documentId;
        this.filename = filename;
        this.ownerId = ownerId;
        this.workspaceId = workspaceId;
        this.status = DocumentStatus.UPLOADED;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }
    
    // Factory method - primary way to create Document
    public static Document create(DocumentId documentId, Filename filename,
                                 UserId ownerId, WorkspaceId workspaceId) {
        return new Document(documentId, filename, ownerId, workspaceId);
    }
    
    // Business behavior: Update document status
    public void updateStatus(DocumentStatus newStatus, UserId userId) {
        ensureUserIsOwner(userId);
        status.validateTransition(newStatus);
        this.status = newStatus;
        this.updatedAt = Timestamp.now();
        // TODO: Emit DocumentStatusChangedEvent
    }
    
    // Business behavior: Grant access to user
    public void grantAccess(UserId targetUserId, Set<Permission> permissions, UserId grantedBy) {
        ensureUserIsOwner(grantedBy);
        
        if (hasAccess(targetUserId)) {
            throw new DuplicateAccessException(
                "User " + targetUserId + " already has access to document"
            );
        }
        
        DocumentACL acl = DocumentACL.create(
            this.documentId,
            targetUserId,
            permissions,
            grantedBy
        );
        
        this.accessControlList.add(acl);
        this.updatedAt = Timestamp.now();
        // TODO: Emit AccessGrantedEvent
    }
    
    // Business behavior: Revoke access
    public void revokeAccess(UserId targetUserId, UserId revokedBy) {
        ensureUserIsOwner(revokedBy);
        
        boolean removed = accessControlList.removeIf(
            acl -> acl.getUserId().equals(targetUserId)
        );
        
        if (!removed) {
            throw new AccessNotFoundException(
                "User " + targetUserId + " does not have access to document"
            );
        }
        
        this.updatedAt = Timestamp.now();
        // TODO: Emit AccessRevokedEvent
    }
    
    // Business behavior: Create new version
    public DocumentVersion createNewVersion(FilePath filePath, FileSize fileSize,
                                           UserId uploadedBy, ChangeDescription description) {
        ensureUserHasPermission(uploadedBy, Permission.WRITE);
        
        VersionNumber nextVersion = calculateNextVersionNumber();
        
        DocumentVersion version = DocumentVersion.create(
            this.documentId,
            nextVersion,
            filePath,
            fileSize,
            uploadedBy,
            description
        );
        
        this.versions.add(version);
        this.updatedAt = Timestamp.now();
        // TODO: Emit NewVersionCreatedEvent
        
        return version;
    }
    
    // Business behavior: Update filename
    public void rename(Filename newFilename, UserId userId) {
        ensureUserHasPermission(userId, Permission.WRITE);
        this.filename = newFilename;
        this.updatedAt = Timestamp.now();
        // TODO: Emit DocumentRenamedEvent
    }
    
    // Query method: Check if user has access
    public boolean hasAccess(UserId userId) {
        return ownerId.equals(userId) || 
               accessControlList.stream()
                   .anyMatch(acl -> acl.getUserId().equals(userId));
    }
    
    // Query method: Check if user has specific permission
    public boolean hasPermission(UserId userId, Permission permission) {
        // Owner has all permissions
        if (ownerId.equals(userId)) {
            return true;
        }
        
        return accessControlList.stream()
            .filter(acl -> acl.getUserId().equals(userId))
            .anyMatch(acl -> acl.hasPermission(permission));
    }
    
    // Query method: Get latest version
    public Optional<DocumentVersion> getLatestVersion() {
        return versions.stream()
            .max((v1, v2) -> v1.getVersionNumber().compareTo(v2.getVersionNumber()));
    }
    
    // Query method: Get versions (immutable)
    public List<DocumentVersion> getVersions() {
        return Collections.unmodifiableList(versions);
    }
    
    // Query method: Get ACL (immutable)
    public List<DocumentACL> getAccessControlList() {
        return Collections.unmodifiableList(accessControlList);
    }
    
    // Query method: Is indexed
    public boolean isIndexed() {
        return status == DocumentStatus.INDEXED;
    }
    
    // Query method: Is processing
    public boolean isProcessing() {
        return status == DocumentStatus.PROCESSING;
    }
    
    // Private helper: Ensure user is owner
    private void ensureUserIsOwner(UserId userId) {
        if (!ownerId.equals(userId)) {
            throw new UnauthorizedAccessException(
                "Only document owner can perform this action"
            );
        }
    }
    
    // Private helper: Ensure user has permission
    private void ensureUserHasPermission(UserId userId, Permission required) {
        if (!hasPermission(userId, required)) {
            throw new UnauthorizedAccessException(
                "User does not have " + required + " permission"
            );
        }
    }
    
    // Private helper: Calculate next version number
    private VersionNumber calculateNextVersionNumber() {
        return versions.isEmpty() 
            ? VersionNumber.initial() 
            : getLatestVersion().get().getVersionNumber().next();
    }
    
    // Entity equality based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document that)) return false;
        return documentId != null && documentId.equals(that.documentId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Document{" +
               "documentId=" + documentId +
               ", filename=" + filename +
               ", status=" + status +
               '}';
    }
}
```

---

## 4. Repository Interface Examples

### DocumentRepository

```java
package lumina.snapshot.lumina_business_logic.domain.repository;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.DocumentStatus;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Document aggregate.
 * Provides collection-like interface for aggregate persistence.
 */
public interface DocumentRepository {
    
    /**
     * Find document by ID
     */
    Optional<Document> findById(DocumentId documentId);
    
    /**
     * Find all documents in a workspace
     */
    List<Document> findByWorkspaceId(WorkspaceId workspaceId);
    
    /**
     * Find all documents owned by a user
     */
    List<Document> findByOwnerId(UserId ownerId);
    
    /**
     * Find documents by status
     */
    List<Document> findByStatus(DocumentStatus status);
    
    /**
     * Find documents where user has access (owner or granted)
     */
    List<Document> findByUserIdWithAccess(UserId userId);
    
    /**
     * Save or update document
     */
    void save(Document document);
    
    /**
     * Delete document
     */
    void delete(DocumentId documentId);
    
    /**
     * Check if document exists
     */
    boolean existsById(DocumentId documentId);
    
    /**
     * Check if user owns document
     */
    boolean existsByIdAndOwnerId(DocumentId documentId, UserId ownerId);
    
    /**
     * Count documents in workspace
     */
    long countByWorkspaceId(WorkspaceId workspaceId);
}
```

### WorkspaceRepository

```java
package lumina.snapshot.lumina_business_logic.domain.repository;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Workspace;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Workspace aggregate
 */
public interface WorkspaceRepository {
    
    Optional<Workspace> findById(WorkspaceId workspaceId);
    
    List<Workspace> findByCreatedBy(UserId userId);
    
    List<Workspace> findByMemberId(UserId userId);
    
    void save(Workspace workspace);
    
    void delete(WorkspaceId workspaceId);
    
    boolean existsById(WorkspaceId workspaceId);
    
    long countByCreatedBy(UserId userId);
}
```

---

## 5. Application Service Example

### DocumentApplicationService

```java
package lumina.snapshot.lumina_business_logic.application.service;

import lombok.RequiredArgsConstructor;
import lumina.snapshot.lumina_business_logic.application.dto.command.*;
import lumina.snapshot.lumina_business_logic.application.dto.response.DocumentResponse;
import lumina.snapshot.lumina_business_logic.domain.event.DocumentUploadedEvent;
import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Workspace;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.repository.DocumentRepository;
import lumina.snapshot.lumina_business_logic.domain.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentApplicationService {
    
    private final DocumentRepository documentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final EventPublisher eventPublisher;
    
    /**
     * Upload a new document
     */
    public DocumentResponse uploadDocument(UploadDocumentCommand command) {
        // 1. Validate workspace exists and user has access
        Workspace workspace = workspaceRepository.findById(command.getWorkspaceId())
            .orElseThrow(() -> new WorkspaceNotFoundException(command.getWorkspaceId()));
        
        workspace.ensureMemberHasPermission(command.getUserId(), Permission.WRITE);
        
        // 2. Create domain objects
        DocumentId documentId = DocumentId.generate();
        Filename filename = Filename.of(command.getFilename());
        
        // 3. Create document aggregate
        Document document = Document.create(
            documentId,
            filename,
            command.getUserId(),
            command.getWorkspaceId()
        );
        
        // 4. Save to repository
        documentRepository.save(document);
        
        // 5. Publish domain event
        eventPublisher.publish(new DocumentUploadedEvent(
            documentId,
            command.getUserId(),
            command.getWorkspaceId()
        ));
        
        return DocumentResponse.from(document);
    }
    
    /**
     * Share document with another user
     */
    public void shareDocument(ShareDocumentCommand command) {
        Document document = documentRepository.findById(command.getDocumentId())
            .orElseThrow(() -> new DocumentNotFoundException(command.getDocumentId()));
        
        // Business logic delegated to aggregate
        document.grantAccess(
            command.getTargetUserId(),
            command.getPermissions(),
            command.getGrantedBy()
        );
        
        documentRepository.save(document);
        
        // Publish event
        eventPublisher.publish(new DocumentSharedEvent(
            document.getDocumentId(),
            command.getTargetUserId(),
            command.getGrantedBy()
        ));
    }
    
    /**
     * Update document status
     */
    public void updateDocumentStatus(UpdateStatusCommand command) {
        Document document = documentRepository.findById(command.getDocumentId())
            .orElseThrow(() -> new DocumentNotFoundException(command.getDocumentId()));
        
        document.updateStatus(command.getNewStatus(), command.getUserId());
        
        documentRepository.save(document);
    }
    
    /**
     * Get documents for user
     */
    @Transactional(readOnly = true)
    public List<DocumentResponse> getUserDocuments(UserId userId) {
        List<Document> documents = documentRepository.findByUserIdWithAccess(userId);
        return documents.stream()
            .map(DocumentResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * Get document by ID
     */
    @Transactional(readOnly = true)
    public DocumentResponse getDocument(DocumentId documentId, UserId userId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException(documentId));
        
        // Check access
        if (!document.hasAccess(userId)) {
            throw new UnauthorizedAccessException("User does not have access to document");
        }
        
        return DocumentResponse.from(document);
    }
}
```

---

## 6. Summary of Changes

### Value Objects
- ✅ Use `@Value` instead of `@Data` for immutability
- ✅ Add static factory methods (`.of()`, `.generate()`)
- ✅ Add validation in factory methods
- ✅ Make constructor private
- ✅ Add meaningful behavior methods

### Enums
- ✅ Convert string-based status/role fields to enums
- ✅ Add business methods to enums
- ✅ Add validation methods

### Aggregates
- ✅ Remove `@Data`, use `@Getter` only
- ✅ Make fields final where appropriate
- ✅ Add business methods instead of just setters
- ✅ Protect invariants
- ✅ Return immutable collections
- ✅ Use factory methods for creation

### Repositories
- ✅ Create repository interfaces in domain layer
- ✅ Define aggregate-oriented methods
- ✅ Keep queries related to aggregate needs

### Application Services
- ✅ Create service layer for use cases
- ✅ Coordinate between aggregates
- ✅ Publish domain events
- ✅ Handle transactions

This refactoring transforms the anemic domain model into a rich, behavior-centric DDD implementation.
