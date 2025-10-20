package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.DocumentStatus;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.FileSize;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.Filename;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.MimeType;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;

public interface DocumentValidationService {

    // Validate file upload
    void validateUpload(Filename filename, FileSize fileSize, MimeType mimeType);

    // Validate file size limits
    void validateFileSize(FileSize fileSize);

    // Validate file type
    void validateFileType(MimeType mimeType);

    // Validate filename
    void validateFilename(Filename filename);

    // Validate workspace quota
    void validateWorkspaceQuota(WorkspaceId workspaceId, FileSize additionalSize);

    // Validate user quota
    void validateUserQuota(UserId userId, FileSize additionalSize);

    // Check for duplicate documents
    Boolean isDuplicate(UserId userId, Filename filename, FileSize fileSize);

    // Validate document state for operation
    void validateDocumentState(DocumentId documentId, DocumentStatus requiredStatus);
}
