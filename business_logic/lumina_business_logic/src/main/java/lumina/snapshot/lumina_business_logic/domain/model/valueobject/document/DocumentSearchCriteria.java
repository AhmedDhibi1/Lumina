package lumina.snapshot.lumina_business_logic.domain.model.valueobject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.TagId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.Author;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.Keywords;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchCriteria {
    private Filename filename;
    private Author author;
    private Keywords keywords;
    private List<TagId> tagIds;
    private DocumentStatus status;
    private Timestamp uploadedAfter;
    private Timestamp uploadedBefore;
    private FileSize minSize;
    private FileSize maxSize;
    private WorkspaceId workspaceId;
}
