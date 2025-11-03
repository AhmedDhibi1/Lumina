package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.FilePath;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.FileSize;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.VersionId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.versioning.ChangeDescription;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.versioning.VersionNumber;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVersion {
    private VersionId versionId;
    private DocumentId documentId;
    private VersionNumber versionNumber;
    private FilePath filePath;
    private FileSize fileSize;
    private UserId uploadedBy;
    private Timestamp createdAt;
    private ChangeDescription changeDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentVersion that)) return false;
        return Objects.equals(versionId, that.versionId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(versionId);  // ✅ Null-safe
    }

    /*// Create new version of document
    DocumentVersion createNewVersion(DocumentId documentId, FilePath newFilePath, FileSize fileSize, UserId uploadedBy, ChangeDescription description);

    // Create initial version
    DocumentVersion createInitialVersion(DocumentId documentId, FilePath filePath, FileSize fileSize, UserId uploadedBy);

    // Create new version
    DocumentVersion uploadNewVersion(DocumentId documentId, FilePath filePath, FileSize fileSize, UserId uploadedBy, ChangeDescription description);

    // Delete old versions (retention policy)
    void pruneOldVersions(DocumentId documentId, Integer keepCount);

    // Restore to specific version
    Document restoreToVersion(DocumentId documentId, VersionId versionId, UserId requesterId);*/

}
