package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.PageRequest;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.PageResponse;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.DocumentSearchCriteria;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.DocumentStatus;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.Filename;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.Author;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.DocumentTitle;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.Keywords;

import java.util.List;

public interface DocumentSearchService {

    // Search documents by metadata
    PageResponse<Document> searchByMetadata(UserId userId, DocumentTitle title, Author author, Keywords keywords, PageRequest pageRequest);

    // Search documents by filename
    PageResponse<Document> searchByFilename(UserId userId, Filename filename, PageRequest pageRequest);

    // Search documents in workspace
    PageResponse<Document> searchInWorkspace(WorkspaceId workspaceId, String searchQuery, PageRequest pageRequest);

    // Get recently accessed documents
    PageResponse<Document> getRecentlyAccessed(UserId userId, PageRequest pageRequest);

    // Get recently uploaded documents
    PageResponse<Document> getRecentlyUploaded(UserId userId, PageRequest pageRequest);

    // Filter documents by status
    PageResponse<Document> filterByStatus(UserId userId, DocumentStatus status, PageRequest pageRequest);

    // Advanced search with multiple criteria
    PageResponse<Document> advancedSearch(UserId userId, DocumentSearchCriteria criteria, PageRequest pageRequest);
    /**
     * Full-text search across document content and metadata.
     * This method searches through document text content, titles, and metadata.
     */
    PageResponse<Document> fullTextSearch(UserId userId, String searchText, PageRequest pageRequest);
    /**
     * Get all documents accessible by a user.
     * Includes owned documents and documents shared with the user.
     */
    PageResponse<Document> getAllAccessibleDocuments(UserId userId, PageRequest pageRequest);
}
