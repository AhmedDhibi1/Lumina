package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentVersion;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.versioning.ChangeDescription;

public interface DocumentLifecycleService {

    // Initialize document after upload
    Document initializeDocument(Filename filename, FilePath filePath, FileSize fileSize, MimeType mimeType, UserId ownerId, WorkspaceId workspaceId);

    // Mark document as processing
    Document markAsProcessing(DocumentId documentId);

    // Mark document as indexed (after embeddings created)
    Document markAsIndexed(DocumentId documentId, ChunkCount chunkCount, EmbeddingModel embeddingModel);

    // Mark document as failed
    Document markAsFailed(DocumentId documentId, String errorMessage);

    // Create new version of document
    DocumentVersion createNewVersion(DocumentId documentId, FilePath newFilePath, FileSize fileSize, UserId uploadedBy, ChangeDescription description);

    // Delete document (owner only, cascade all related data)
    void deleteDocument(DocumentId documentId, UserId requesterId);

    // Archive document (soft delete)
    Document archiveDocument(DocumentId documentId, UserId requesterId);

    // Restore archived document
    Document restoreDocument(DocumentId documentId, UserId requesterId);
}