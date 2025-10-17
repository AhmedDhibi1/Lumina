package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.FilePath;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.FileSize;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.VersionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.versioning.ChangeDescription;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.versioning.VersionNumber;

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
}
