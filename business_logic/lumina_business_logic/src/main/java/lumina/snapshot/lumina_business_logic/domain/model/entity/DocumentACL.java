package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.AclId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.PermissionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentACL {
    private AclId aclId;
    private DocumentId documentId;
    private UserId userId;
    private PermissionId permissionId;
    private UserId grantedBy;
    private Timestamp grantedAt;
    private Timestamp expiresAt;
}
