package lumina.snapshot.lumina_business_logic.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentACL;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentMetadata;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentOwnership;
import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentVersion;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private DocumentId documentId;
    private Filename filename;
    private FilePath filePath;
    private FileSize fileSize;
    private MimeType mimeType;
    private PageCount pageCount;
    private UserId ownerId;
    private WorkspaceId workspaceId;
    private DocumentStatus status;
    private ChunkCount chunkCount;
    private EmbeddingModel embeddingModel ;
    private Timestamp indexedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private List<DocumentOwnership> ownerships;
    private List<DocumentACL> accessControlList;
    private DocumentMetadata metadata;
    private List<DocumentVersion> versions;
}
