package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.MembershipId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.workspace.WorkspaceRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMember {
    private MembershipId membershipId;
    private WorkspaceId workspaceId;
    private UserId userId;
    private WorkspaceRole role;
    private Timestamp joinedAt;
}
