package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.OwnershipId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentOwnership {
    private OwnershipId ownershipId;
    private DocumentId documentId;
    private UserId ownerId;
    private Boolean isPrimaryOwner;
    private Timestamp acquiredAt;
}
