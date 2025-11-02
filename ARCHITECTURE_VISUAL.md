# Visual Architecture Summary

## Current State vs. Desired State

### Current Architecture (Anemic Domain Model)

```
┌─────────────────────────────────────────────────────────────┐
│                    MISSING LAYERS                            │
│  ❌ No Controllers                                           │
│  ❌ No Application Services                                  │
│  ❌ No Domain Events                                         │
│  ❌ No Repositories                                          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER (EXISTS)                     │
│                                                              │
│  Aggregates (Anemic - Just Data)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Document    │  │  Workspace   │  │ ChatSession  │     │
│  │  @Data ❌    │  │  @Data ❌    │  │  @Data ❌    │     │
│  │  No behavior │  │  No behavior │  │  No behavior │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                                                              │
│  Entities (Anemic - Just Data)                             │
│  ┌─────────────────────────────────────────────────┐       │
│  │ ChatMessage, DocumentACL, DocumentVersion, etc.│       │
│  │ @Data ❌  No behavior                           │       │
│  └─────────────────────────────────────────────────┘       │
│                                                              │
│  Value Objects (Mutable - WRONG!)                          │
│  ┌─────────────────────────────────────────────────┐       │
│  │ 35 Value Objects: DocumentId, Email, Filename,  │       │
│  │ FileSize, DocumentStatus, etc.                  │       │
│  │ @Data ❌  Mutable ❌  No validation ❌          │       │
│  └─────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘

Problems:
- Business logic will end up in service layer (anemic)
- No encapsulation (everything mutable via @Data)
- No validation at construction time
- String-based types instead of enums
- Missing infrastructure to support DDD
```

---

### Desired Architecture (Rich Domain Model)

```
┌─────────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER (Future)                     │
│  ┌──────────────────────────────────────────────┐           │
│  │  REST Controllers                             │           │
│  │  - DocumentController                         │           │
│  │  - WorkspaceController                        │           │
│  │  - ChatController                             │           │
│  └──────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────┘
                         ↓ DTOs/Commands
┌─────────────────────────────────────────────────────────────┐
│              APPLICATION LAYER (To Be Added)                 │
│  ┌──────────────────────────────────────────────┐           │
│  │  Application Services (Orchestration)         │           │
│  │  - DocumentApplicationService                 │           │
│  │  - WorkspaceApplicationService                │           │
│  │  - ChatApplicationService                     │           │
│  │                                                │           │
│  │  Use Cases:                                   │           │
│  │  • uploadDocument()                           │           │
│  │  • shareDocument()                            │           │
│  │  • createWorkspace()                          │           │
│  │  • addWorkspaceMember()                       │           │
│  └──────────────────────────────────────────────┘           │
│                                                              │
│  ┌──────────────────────────────────────────────┐           │
│  │  DTOs & Commands                              │           │
│  │  - UploadDocumentCommand                      │           │
│  │  - ShareDocumentCommand                       │           │
│  │  - DocumentResponse                           │           │
│  └──────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│              DOMAIN LAYER (To Be Enriched)                   │
│                                                              │
│  Domain Services (Cross-Aggregate Logic)                    │
│  ┌──────────────────────────────────────────────┐           │
│  │  - DocumentSharingService                     │           │
│  │  - WorkspaceAccessService                     │           │
│  └──────────────────────────────────────────────┘           │
│                                                              │
│  Aggregates (RICH with Business Behavior)                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Document    │  │  Workspace   │  │ ChatSession  │     │
│  │  @Getter ✅  │  │  @Getter ✅  │  │  @Getter ✅  │     │
│  │              │  │              │  │              │     │
│  │ Methods:     │  │ Methods:     │  │ Methods:     │     │
│  │ • updateStatus│ │ • addMember  │  │ • addMessage │     │
│  │ • grantAccess│  │ • removeMember│ │ • clearHistory│    │
│  │ • revokeAccess│ │ • updateRole │  │ • getSummary │     │
│  │ • createVersion│ │ • isMember  │  │              │     │
│  │ • rename     │  │ • hasPermission│ │            │     │
│  │ • hasAccess  │  │              │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                                                              │
│  Entities (With Behavior)                                   │
│  ┌─────────────────────────────────────────────────┐       │
│  │ • ChatMessage: validate(), isFromUser()         │       │
│  │ • DocumentVersion: markAsDeleted(), isNewer()   │       │
│  │ • WorkspaceMember: updateRole(), hasPermission()│       │
│  │ @Getter ✅  equals/hashCode on ID ✅           │       │
│  └─────────────────────────────────────────────────┘       │
│                                                              │
│  Value Objects (IMMUTABLE with Validation)                 │
│  ┌─────────────────────────────────────────────────┐       │
│  │ Identity VOs (15): DocumentId, UserId, etc.     │       │
│  │ - Immutable (@Value) ✅                         │       │
│  │ - Factory methods (.of(), .generate()) ✅       │       │
│  │                                                  │       │
│  │ Domain VOs (20): Filename, FileSize, Email      │       │
│  │ - Immutable (@Value) ✅                         │       │
│  │ - Validation in factory ✅                      │       │
│  │ - Business methods ✅                           │       │
│  │   • Filename: getExtension(), withNewExtension()│       │
│  │   • FileSize: toHumanReadable(), isLargerThan() │       │
│  │   • Email: getDomain(), isFromDomain()          │       │
│  │                                                  │       │
│  │ Enums (4): DocumentStatus, WorkspaceRole, etc. │       │
│  │ - Type safe ✅                                  │       │
│  │ - Business methods ✅                           │       │
│  │   • DocumentStatus.canTransitionTo()            │       │
│  │   • WorkspaceRole.hasPermission()               │       │
│  └─────────────────────────────────────────────────┘       │
│                                                              │
│  Domain Events (Decoupling)                                │
│  ┌─────────────────────────────────────────────────┐       │
│  │ • DocumentUploadedEvent                          │       │
│  │ • DocumentSharedEvent                            │       │
│  │ • DocumentStatusChangedEvent                     │       │
│  │ • WorkspaceCreatedEvent                          │       │
│  │ • MemberAddedEvent                               │       │
│  └─────────────────────────────────────────────────┘       │
│                                                              │
│  Repository Interfaces (Persistence Abstraction)           │
│  ┌─────────────────────────────────────────────────┐       │
│  │ • DocumentRepository                             │       │
│  │ • WorkspaceRepository                            │       │
│  │ • ChatSessionRepository                          │       │
│  └─────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│           INFRASTRUCTURE LAYER (To Be Added)                 │
│  ┌──────────────────────────────────────────────┐           │
│  │  Repository Implementations (Spring Data JPA) │           │
│  │  - JpaDocumentRepository                      │           │
│  │  - JpaWorkspaceRepository                     │           │
│  │  - JpaChatSessionRepository                   │           │
│  └──────────────────────────────────────────────┘           │
│                                                              │
│  ┌──────────────────────────────────────────────┐           │
│  │  Event Publishers                             │           │
│  │  - Spring ApplicationEventPublisher           │           │
│  └──────────────────────────────────────────────┘           │
│                                                              │
│  ┌──────────────────────────────────────────────┐           │
│  │  External Integrations                        │           │
│  │  - File Storage Service                       │           │
│  │  - Vector Database (for embeddings)           │           │
│  │  - Message Queue                              │           │
│  └──────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE                                  │
│  PostgreSQL (with JPA/Hibernate)                            │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Transformations Required

### 1. Value Objects: Mutable → Immutable

**Before:**
```java
@Data  // ❌ Generates setters
@AllArgsConstructor
@NoArgsConstructor
public class DocumentId {
    private UUID value;
}

// Problem: Can be changed!
DocumentId id = new DocumentId(uuid);
id.setValue(hackedUuid); // ❌ Should not be possible
```

**After:**
```java
@Value  // ✅ All fields final
public class DocumentId {
    UUID value;
    
    private DocumentId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }
    
    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }
}

// Now immutable!
DocumentId id = DocumentId.generate();
// id.setValue(...); // ✅ Does not compile!
```

---

### 2. Aggregates: Anemic → Rich

**Before:**
```java
@Data  // ❌ Just getters/setters
public class Document {
    private DocumentId documentId;
    private DocumentStatus status;
    private UserId ownerId;
}

// Business logic in service layer (anemic!)
@Service
class DocumentService {
    void updateStatus(Document doc, DocumentStatus newStatus, UserId userId) {
        if (!doc.getOwnerId().equals(userId)) {
            throw new UnauthorizedException();
        }
        doc.setStatus(newStatus); // ❌ No validation!
    }
}
```

**After:**
```java
@Getter  // ✅ Only getters, no setters
public class Document {
    private final DocumentId documentId;
    private DocumentStatus status;
    private final UserId ownerId;
    
    // Business logic IN the domain model
    public void updateStatus(DocumentStatus newStatus, UserId userId) {
        ensureUserIsOwner(userId);
        status.validateTransition(newStatus);  // ✅ Validation
        this.status = newStatus;
    }
    
    private void ensureUserIsOwner(UserId userId) {
        if (!ownerId.equals(userId)) {
            throw new UnauthorizedException("Only owner can update status");
        }
    }
}

// Service just orchestrates
@Service
class DocumentApplicationService {
    void updateStatus(UpdateStatusCommand cmd) {
        Document doc = repository.findById(cmd.getDocumentId());
        doc.updateStatus(cmd.getNewStatus(), cmd.getUserId()); // ✅ Calls domain logic
        repository.save(doc);
    }
}
```

---

### 3. String Types → Enums

**Before:**
```java
@Data
public class DocumentStatus {
    private String status; // ❌ "UPLOADED", "PROCESSING", etc.
}

// Problems:
// - No type safety
// - No validation
// - Typos possible: "PROCEESSING"
// - No transition rules
```

**After:**
```java
public enum DocumentStatus {  // ✅ Type safe
    UPLOADED("Document uploaded"),
    PROCESSING("Being processed"),
    INDEXED("Successfully indexed"),
    FAILED("Processing failed");
    
    private final String description;
    
    DocumentStatus(String description) {
        this.description = description;
    }
    
    // Business logic in enum
    public boolean canTransitionTo(DocumentStatus newStatus) {
        return switch (this) {
            case UPLOADED -> newStatus == PROCESSING;
            case PROCESSING -> newStatus == INDEXED || newStatus == FAILED;
            case FAILED -> newStatus == PROCESSING;
            case INDEXED -> false;
        };
    }
    
    public void validateTransition(DocumentStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(
                "Cannot transition from " + this + " to " + newStatus
            );
        }
    }
}
```

---

## Files Changed Summary

### Phase 1: Foundation (Week 1)
- ✏️ **1 file modified**: `pom.xml` (Java version)
- ✏️ **35 files modified**: All value objects (→ immutable)
- ✏️ **4 files modified**: Status/Role → enums
- ➕ **3 files created**: Repository interfaces
- ✏️ **3 files modified**: Aggregates (remove @Data)

### Phase 2: Behavior (Week 2)
- ✏️ **3 files modified**: Add business methods to aggregates
- ✏️ **10 files modified**: Add behavior to entities
- ➕ **5 files created**: Application services
- ➕ **10 files created**: DTOs/Commands/Responses

### Phase 3: Infrastructure (Week 3)
- ✏️ **3 files modified**: Add JPA annotations to aggregates
- ➕ **3 files created**: JPA repository implementations
- ➕ **10 files created**: Domain events
- ➕ **2 files created**: Event publisher infrastructure

**Total Changes:**
- Modified: ~60 files
- Created: ~33 files
- Lines of code: +2,000 (business logic)
- Time: 3 weeks (120 hours)

---

## Benefits of Rich Domain Model

### Before (Anemic)
```
Service Layer: 80% of business logic ❌
Domain Layer: 20% of business logic ❌
Testability: Hard (need database) ❌
Maintainability: Low (logic scattered) ❌
```

### After (Rich)
```
Service Layer: 20% of business logic ✅ (orchestration only)
Domain Layer: 80% of business logic ✅ (where it belongs)
Testability: Easy (pure domain tests) ✅
Maintainability: High (logic in one place) ✅
```

---

## Testing Strategy

### Unit Tests (No Database)
```java
@Test
void shouldEnforceOwnershipForStatusUpdate() {
    // Pure domain logic test - no database needed!
    Document doc = Document.create(id, filename, ownerId, workspaceId);
    UserId attacker = UserId.generate();
    
    assertThrows(UnauthorizedException.class,
        () -> doc.updateStatus(DocumentStatus.PROCESSING, attacker));
}
```

### Integration Tests (With Database)
```java
@SpringBootTest
@Transactional
class DocumentApplicationServiceTest {
    @Test
    void shouldUploadAndPersistDocument() {
        // Tests orchestration and persistence
        DocumentResponse response = service.uploadDocument(command);
        assertNotNull(response.getDocumentId());
    }
}
```

---

## Migration Risk Assessment

### Low Risk ✅
- Value object changes (internal representation)
- Adding validation (fail fast is good)
- Adding new methods (backward compatible)

### Medium Risk ⚠️
- Changing @Data to @Getter (breaks external setters)
- String → Enum conversion (database migration needed)

### Mitigation
- Phase 1: Internal changes only
- Phase 2: Add new methods, deprecate old patterns
- Phase 3: Infrastructure without breaking existing code
- Comprehensive testing at each phase

---

## Success Criteria

✅ All value objects immutable  
✅ All domain objects have behavior  
✅ Business logic in domain layer (not services)  
✅ Repository abstractions in place  
✅ Application services orchestrate only  
✅ Domain events for decoupling  
✅ Test coverage >80%  
✅ Build passes  
✅ No regression in functionality  

---

This visual guide complements the detailed documentation and provides a clear picture of the transformation journey from anemic to rich domain model.
