# Quick Start Guide - Implementing DDD Recommendations

This guide provides a step-by-step approach to implementing the DDD recommendations from the code review.

---

## Phase 1: Foundation (Week 1) - Critical Fixes

### Step 1: Fix Build Configuration (Day 1)
**Priority:** 🔴 Critical  
**Effort:** 15 minutes

**Issue:** Java version mismatch (POM configured for Java 21, environment has Java 17)

**Fix:**
```xml
<!-- File: business_logic/lumina_business_logic/pom.xml -->
<properties>
    <java.version>17</java.version>  <!-- Changed from 21 to 17 -->
</properties>
```

**Verify:**
```bash
cd business_logic/lumina_business_logic
./mvnw clean compile
```

---

### Step 2: Transform Value Objects to Immutable (Days 1-2)
**Priority:** 🔴 Critical  
**Effort:** 4-8 hours

**Files to Update:** All 35 value objects in `domain/model/valueobject/`

**Template:**
```java
// Before
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XYZ {
    private String value;
}

// After
import lombok.Value;

@Value
public class XYZ {
    String value;
    
    private XYZ(String value) {
        this.value = value;
    }
    
    public static XYZ of(String value) {
        // Add validation
        Objects.requireNonNull(value, "XYZ cannot be null");
        // Add more validation as needed
        return new XYZ(value);
    }
}
```

**Order of Implementation:**
1. Identity value objects (DocumentId, UserId, etc.) - 10 files
2. Simple value objects (Email, Timestamp) - 5 files  
3. Domain-specific value objects (Filename, FileSize, etc.) - 20 files

**Testing:**
```java
@Test
void valueShouldBeImmutable() {
    Filename filename = Filename.of("test.pdf");
    // filename.setValue("hack"); // Should not compile
    assertEquals("test.pdf", filename.getValue());
}

@Test
void shouldValidateInput() {
    assertThrows(IllegalArgumentException.class, 
        () -> Filename.of(""));
}
```

---

### Step 3: Convert String-Based Types to Enums (Day 3)
**Priority:** 🔴 Critical  
**Effort:** 2-3 hours

**Files to Convert:**
- `DocumentStatus` → enum
- `WorkspaceRole` → enum
- `MessageRole` → enum
- `ActivityType` → enum

**Template:**
```java
// Before
@Data
public class DocumentStatus {
    private String status; // UPLOADED, PROCESSING, INDEXED, FAILED
}

// After
public enum DocumentStatus {
    UPLOADED("Document uploaded, awaiting processing"),
    PROCESSING("Document being processed"),
    INDEXED("Document successfully indexed"),
    FAILED("Document processing failed");
    
    private final String description;
    
    DocumentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canTransitionTo(DocumentStatus newStatus) {
        return switch (this) {
            case UPLOADED -> newStatus == PROCESSING;
            case PROCESSING -> newStatus == INDEXED || newStatus == FAILED;
            case FAILED -> newStatus == PROCESSING;
            case INDEXED -> false;
        };
    }
}
```

**Update References:**
After converting to enum, update all usages in:
- Aggregate classes (Document, Workspace, ChatSession)
- Entity classes
- Any test files

---

### Step 4: Create Repository Interfaces (Day 4)
**Priority:** 🔴 Critical  
**Effort:** 3-4 hours

**Create Directory:**
```bash
mkdir -p src/main/java/lumina/snapshot/lumina_business_logic/domain/repository
```

**Create Repository Interfaces:**
1. `DocumentRepository.java`
2. `WorkspaceRepository.java`
3. `ChatSessionRepository.java`

**Template:**
```java
package lumina.snapshot.lumina_business_logic.domain.repository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository {
    Optional<Document> findById(DocumentId documentId);
    List<Document> findByWorkspaceId(WorkspaceId workspaceId);
    void save(Document document);
    void delete(DocumentId documentId);
    boolean existsById(DocumentId documentId);
}
```

---

### Step 5: Remove @Data from Aggregates (Day 5)
**Priority:** 🔴 Critical  
**Effort:** 2-3 hours

**Files to Update:**
- `Document.java`
- `Workspace.java`
- `ChatSession.java`

**Changes:**
```java
// Before
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private DocumentId documentId;
    private Filename filename;
    // ...
}

// After
import lombok.Getter;
import lombok.AccessLevel;

@Getter
public class Document {
    private final DocumentId documentId;
    private Filename filename;
    
    // Protected constructor for JPA
    protected Document() {
        this.documentId = null;
    }
    
    // Private constructor for internal use
    private Document(DocumentId documentId, Filename filename, 
                    UserId ownerId, WorkspaceId workspaceId) {
        this.documentId = documentId;
        this.filename = filename;
        // ...
    }
    
    // Factory method - primary creation method
    public static Document create(DocumentId documentId, Filename filename,
                                 UserId ownerId, WorkspaceId workspaceId) {
        return new Document(documentId, filename, ownerId, workspaceId);
    }
    
    // Override equals/hashCode based on ID
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
}
```

---

## Phase 2: Add Behavior (Week 2) - High Priority

### Step 6: Add Business Methods to Document Aggregate (Days 1-2)
**Priority:** 🟡 High  
**Effort:** 6-8 hours

**Add Methods:**
```java
public class Document {
    
    // Business behavior
    public void updateStatus(DocumentStatus newStatus, UserId userId) {
        ensureUserIsOwner(userId);
        status.validateTransition(newStatus);
        this.status = newStatus;
        this.updatedAt = Timestamp.now();
    }
    
    public void grantAccess(UserId targetUserId, Set<Permission> permissions, 
                          UserId grantedBy) {
        ensureUserIsOwner(grantedBy);
        
        if (hasAccess(targetUserId)) {
            throw new DuplicateAccessException("User already has access");
        }
        
        DocumentACL acl = DocumentACL.create(
            this.documentId, targetUserId, permissions, grantedBy
        );
        this.accessControlList.add(acl);
    }
    
    public DocumentVersion createNewVersion(FilePath filePath, FileSize fileSize,
                                           UserId uploadedBy, 
                                           ChangeDescription description) {
        ensureUserHasPermission(uploadedBy, Permission.WRITE);
        
        VersionNumber nextVersion = calculateNextVersionNumber();
        DocumentVersion version = DocumentVersion.create(
            this.documentId, nextVersion, filePath, fileSize, 
            uploadedBy, description
        );
        
        this.versions.add(version);
        return version;
    }
    
    public void rename(Filename newFilename, UserId userId) {
        ensureUserHasPermission(userId, Permission.WRITE);
        this.filename = newFilename;
        this.updatedAt = Timestamp.now();
    }
    
    // Query methods
    public boolean hasAccess(UserId userId) {
        return ownerId.equals(userId) || 
               accessControlList.stream()
                   .anyMatch(acl -> acl.getUserId().equals(userId));
    }
    
    public boolean hasPermission(UserId userId, Permission permission) {
        if (ownerId.equals(userId)) return true;
        
        return accessControlList.stream()
            .filter(acl -> acl.getUserId().equals(userId))
            .anyMatch(acl -> acl.hasPermission(permission));
    }
    
    // Private helpers
    private void ensureUserIsOwner(UserId userId) {
        if (!ownerId.equals(userId)) {
            throw new UnauthorizedAccessException("Only owner can perform this");
        }
    }
    
    private void ensureUserHasPermission(UserId userId, Permission permission) {
        if (!hasPermission(userId, permission)) {
            throw new UnauthorizedAccessException("Missing permission: " + permission);
        }
    }
    
    private VersionNumber calculateNextVersionNumber() {
        return versions.isEmpty() 
            ? VersionNumber.initial() 
            : versions.get(versions.size() - 1).getVersionNumber().next();
    }
}
```

**Testing:**
```java
@Test
void shouldAllowOwnerToGrantAccess() {
    Document doc = Document.create(docId, filename, ownerId, workspaceId);
    UserId targetUser = UserId.generate();
    
    doc.grantAccess(targetUser, Set.of(Permission.READ), ownerId);
    
    assertTrue(doc.hasAccess(targetUser));
    assertTrue(doc.hasPermission(targetUser, Permission.READ));
}

@Test
void shouldPreventNonOwnerFromGrantingAccess() {
    Document doc = Document.create(docId, filename, ownerId, workspaceId);
    UserId nonOwner = UserId.generate();
    UserId targetUser = UserId.generate();
    
    assertThrows(UnauthorizedAccessException.class,
        () -> doc.grantAccess(targetUser, Set.of(Permission.READ), nonOwner));
}
```

---

### Step 7: Add Behavior to Workspace Aggregate (Day 3)
**Priority:** 🟡 High  
**Effort:** 4-5 hours

**Add Methods:**
```java
public class Workspace {
    private static final int MAX_MEMBERS = 100;
    
    public void addMember(UserId userId, WorkspaceRole role, UserId addedBy) {
        validateCanAddMember(userId, addedBy);
        
        WorkspaceMember member = WorkspaceMember.create(
            this.workspaceId, userId, role
        );
        this.members.add(member);
        this.updatedAt = Timestamp.now();
    }
    
    public void removeMember(UserId userId, UserId removedBy) {
        ensureMemberIsAdmin(removedBy);
        
        if (userId.equals(createdBy)) {
            throw new IllegalOperationException("Cannot remove workspace creator");
        }
        
        boolean removed = members.removeIf(m -> m.getUserId().equals(userId));
        if (!removed) {
            throw new MemberNotFoundException("User is not a member");
        }
        this.updatedAt = Timestamp.now();
    }
    
    public void updateMemberRole(UserId userId, WorkspaceRole newRole, 
                                UserId updatedBy) {
        ensureMemberIsAdmin(updatedBy);
        
        WorkspaceMember member = findMember(userId)
            .orElseThrow(() -> new MemberNotFoundException("User is not a member"));
        
        member.updateRole(newRole);
        this.updatedAt = Timestamp.now();
    }
    
    // Query methods
    public boolean isMember(UserId userId) {
        return members.stream().anyMatch(m -> m.getUserId().equals(userId));
    }
    
    public Optional<WorkspaceMember> findMember(UserId userId) {
        return members.stream()
            .filter(m -> m.getUserId().equals(userId))
            .findFirst();
    }
    
    private void validateCanAddMember(UserId userId, UserId addedBy) {
        if (members.size() >= MAX_MEMBERS) {
            throw new WorkspaceCapacityExceededException(
                "Maximum members reached: " + MAX_MEMBERS
            );
        }
        
        ensureMemberIsAdmin(addedBy);
        
        if (isMember(userId)) {
            throw new DuplicateMemberException("User already a member");
        }
    }
    
    private void ensureMemberIsAdmin(UserId userId) {
        WorkspaceMember member = findMember(userId)
            .orElseThrow(() -> new UnauthorizedException("User not a member"));
        
        if (member.getRole() != WorkspaceRole.ADMIN) {
            throw new UnauthorizedException("Only admins can perform this action");
        }
    }
}
```

---

### Step 8: Create Application Services (Days 4-5)
**Priority:** 🟡 High  
**Effort:** 6-8 hours

**Create Directory:**
```bash
mkdir -p src/main/java/lumina/snapshot/lumina_business_logic/application/service
mkdir -p src/main/java/lumina/snapshot/lumina_business_logic/application/dto/command
mkdir -p src/main/java/lumina/snapshot/lumina_business_logic/application/dto/response
```

**Create DTOs:**
```java
// UploadDocumentCommand.java
@Value
public class UploadDocumentCommand {
    WorkspaceId workspaceId;
    UserId userId;
    String filename;
    byte[] content;
}

// DocumentResponse.java
@Value
public class DocumentResponse {
    UUID documentId;
    String filename;
    String status;
    LocalDateTime createdAt;
    
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
            document.getDocumentId().getValue(),
            document.getFilename().getValue(),
            document.getStatus().name(),
            document.getCreatedAt().getValue()
        );
    }
}
```

**Create Service:**
```java
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentApplicationService {
    
    private final DocumentRepository documentRepository;
    private final WorkspaceRepository workspaceRepository;
    
    public DocumentResponse uploadDocument(UploadDocumentCommand command) {
        // 1. Validate workspace and permissions
        Workspace workspace = workspaceRepository.findById(command.getWorkspaceId())
            .orElseThrow(() -> new WorkspaceNotFoundException());
        
        workspace.ensureMemberHasPermission(command.getUserId(), Permission.WRITE);
        
        // 2. Create document
        Document document = Document.create(
            DocumentId.generate(),
            Filename.of(command.getFilename()),
            command.getUserId(),
            command.getWorkspaceId()
        );
        
        // 3. Save
        documentRepository.save(document);
        
        return DocumentResponse.from(document);
    }
}
```

---

## Phase 3: Infrastructure (Week 3) - Medium Priority

### Step 9: Implement Repository with Spring Data JPA
**Priority:** 🟢 Medium  
**Effort:** 8-10 hours

**Add JPA Annotations to Aggregates:**
```java
@Entity
@Table(name = "documents")
@Getter
public class Document {
    
    @EmbeddedId
    private DocumentId documentId;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "filename"))
    private Filename filename;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    
    // ... other fields
}
```

**Create Spring Data Repository:**
```java
public interface JpaDocumentRepository extends JpaRepository<Document, DocumentId>, 
                                               DocumentRepository {
    // Spring Data JPA auto-implements basic methods
    // Add custom queries as needed
    
    @Query("SELECT d FROM Document d WHERE d.workspaceId = :workspaceId")
    List<Document> findByWorkspaceId(@Param("workspaceId") WorkspaceId workspaceId);
}
```

---

### Step 10: Add Domain Events
**Priority:** 🟢 Medium  
**Effort:** 4-6 hours

**Create Event Base Class:**
```java
public abstract class DomainEvent {
    private final UUID eventId;
    private final Timestamp occurredAt;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Timestamp.now();
    }
}
```

**Create Specific Events:**
```java
@Value
public class DocumentUploadedEvent extends DomainEvent {
    DocumentId documentId;
    UserId uploadedBy;
    WorkspaceId workspaceId;
}
```

**Publish Events from Aggregates:**
```java
public class Document {
    @Transient
    private List<DomainEvent> domainEvents = new ArrayList<>();
    
    public void updateStatus(DocumentStatus newStatus, UserId userId) {
        // ... business logic
        domainEvents.add(new DocumentStatusChangedEvent(
            this.documentId, this.status, newStatus
        ));
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
```

---

## Testing Strategy

### Unit Tests (Aggregates & Value Objects)
```java
@Test
void documentShouldEnforceOwnershipForStatusUpdate() {
    Document doc = Document.create(docId, filename, ownerId, workspaceId);
    UserId nonOwner = UserId.generate();
    
    assertThrows(UnauthorizedAccessException.class,
        () -> doc.updateStatus(DocumentStatus.PROCESSING, nonOwner));
}

@Test
void filenameShouldRejectInvalidCharacters() {
    assertThrows(IllegalArgumentException.class,
        () -> Filename.of("file/name.pdf"));
}
```

### Integration Tests (Application Services)
```java
@SpringBootTest
@Transactional
class DocumentApplicationServiceTest {
    
    @Autowired
    private DocumentApplicationService service;
    
    @Test
    void shouldUploadDocument() {
        var command = new UploadDocumentCommand(
            workspaceId, userId, "test.pdf", content
        );
        
        DocumentResponse response = service.uploadDocument(command);
        
        assertNotNull(response.getDocumentId());
        assertEquals("test.pdf", response.getFilename());
    }
}
```

---

## Migration Checklist

### Pre-Migration
- [ ] Create feature branch: `git checkout -b feature/ddd-refactoring`
- [ ] Run existing tests to establish baseline
- [ ] Back up current code

### Phase 1 (Week 1)
- [ ] Fix Java version in POM
- [ ] Transform all value objects to immutable (@Value)
- [ ] Convert string types to enums
- [ ] Create repository interfaces
- [ ] Remove @Data from aggregates

### Phase 2 (Week 2)
- [ ] Add business methods to Document
- [ ] Add business methods to Workspace
- [ ] Add business methods to ChatSession
- [ ] Create application service layer
- [ ] Create DTOs and commands

### Phase 3 (Week 3)
- [ ] Add JPA annotations
- [ ] Implement Spring Data repositories
- [ ] Add domain events infrastructure
- [ ] Create event publishers

### Post-Migration
- [ ] Run all tests
- [ ] Code review
- [ ] Documentation update
- [ ] Merge to main branch

---

## Success Metrics

### Before Refactoring
- Lines of business logic in domain model: ~0
- Mutable value objects: 35
- Test coverage: Unknown
- Cyclomatic complexity: Low (no logic)

### After Refactoring
- Lines of business logic in domain model: 500+
- Mutable value objects: 0
- Test coverage: >80%
- Domain behavior: Rich and testable

---

## Common Pitfalls to Avoid

1. **Don't try to do everything at once** - Follow the phased approach
2. **Don't skip tests** - Write tests as you refactor
3. **Don't break existing functionality** - Ensure backward compatibility
4. **Don't forget JPA requirements** - Keep protected constructors
5. **Don't make everything immutable** - Aggregates need to change state
6. **Don't expose collections directly** - Return unmodifiable views
7. **Don't put business logic in services** - Keep it in domain model

---

## Questions & Support

If you have questions during implementation:
1. Refer to IMPLEMENTATION_EXAMPLES.md for detailed code samples
2. Refer to CODE_REVIEW.md for rationale behind changes
3. Check DDD books referenced in CODE_REVIEW.md section 7

Good luck with the refactoring! 🚀
