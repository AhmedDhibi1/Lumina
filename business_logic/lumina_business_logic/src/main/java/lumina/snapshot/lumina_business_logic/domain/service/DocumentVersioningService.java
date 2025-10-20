package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Document;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentVersion;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.FilePath;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.FileSize;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.VersionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.versioning.ChangeDescription;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.versioning.VersionComparison;

import java.util.List;

public interface DocumentVersioningService {

    // Create initial version
    DocumentVersion createInitialVersion(DocumentId documentId, FilePath filePath, FileSize fileSize, UserId uploadedBy);

    // Create new version
    DocumentVersion uploadNewVersion(DocumentId documentId, FilePath filePath, FileSize fileSize, UserId uploadedBy, ChangeDescription description);

    // Get version history
    List<DocumentVersion> getVersionHistory(DocumentId documentId);

    // Get specific version
    DocumentVersion getVersion(VersionId versionId);

    // Get latest version
    DocumentVersion getLatestVersion(DocumentId documentId);

    // Restore to specific version
    Document restoreToVersion(DocumentId documentId, VersionId versionId, UserId requesterId);

    // Compare versions
    VersionComparison compareVersions(VersionId version1, VersionId version2);

    // Delete old versions (retention policy)
    void pruneOldVersions(DocumentId documentId, Integer keepCount);
}