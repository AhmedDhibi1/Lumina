package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.TagId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTagMapping {
    private DocumentId documentId;
    private TagId tagId;
    private UserId taggedBy;
    private Timestamp taggedAt;
}
