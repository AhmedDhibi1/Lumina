package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.ShareId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.Permissions;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingHistory {
    private ShareId shareId;
    private DocumentId documentId;
    private UserId sharedBy;
    private UserId sharedWith;
    private Permissions permissions;
    private Timestamp sharedAt;
    private Timestamp revokedAt;
}
