package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentTag;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentTagMapping;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.TagId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.tagging.Color;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.tagging.TagName;

import java.util.List;

public interface DocumentTaggingService {

    // Create new tag
    DocumentTag createTag(TagName name, Color color);

    // Apply tag to document
    DocumentTagMapping tagDocument(DocumentId documentId, TagId tagId, UserId taggedBy);

    // Remove tag from document
    void untagDocument(DocumentId documentId, TagId tagId);

    // Get all tags for a document
    List<DocumentTag> getDocumentTags(DocumentId documentId);

    // Get all documents with a specific tag
    List<Document> getDocumentsByTag(TagId tagId);

    // Get all available tags
    List<DocumentTag> getAllTags();

    // Search documents by tags
    List<Document> searchByTags(List<TagId> tagIds, UserId userId);
}