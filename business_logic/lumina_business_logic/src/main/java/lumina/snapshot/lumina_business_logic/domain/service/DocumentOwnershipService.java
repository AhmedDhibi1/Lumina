package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentOwnership;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

import java.util.List;

public interface DocumentOwnershipService {
    // Verify if user is the primary owner of a document
    Boolean isPrimaryOwner(DocumentId documentId, UserId userId);

    // Verify if user has any ownership over document
    Boolean isOwner(DocumentId documentId, UserId userId);

    // Transfer primary ownership to another user
    DocumentOwnership transferOwnership(DocumentId documentId, UserId currentOwnerId, UserId newOwnerId);

    // Establish initial ownership when document is created
    DocumentOwnership establishOwnership(DocumentId documentId, UserId ownerId);

    // Get all documents owned by a user
    List<Document> getOwnedDocuments(UserId userId);

    // Validate ownership before critical operations
    void validateOwnership(DocumentId documentId, UserId userId);
}
