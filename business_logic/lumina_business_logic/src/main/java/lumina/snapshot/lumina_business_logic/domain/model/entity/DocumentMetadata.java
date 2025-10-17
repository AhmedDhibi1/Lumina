package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.MetadataId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {
    private MetadataId metadataId;
    private DocumentId documentId;
    private DocumentTitle title;
    private Author author;
    private Timestamp documentCreationDate;
    private Keywords keywords;
    private Summary summary;
    private Language language;
}
