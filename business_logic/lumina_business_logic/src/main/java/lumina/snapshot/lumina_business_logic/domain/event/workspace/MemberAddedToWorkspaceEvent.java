package lumina.snapshot.lumina_business_logic.domain.event.workspace;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberAddedToWorkspaceEvent extends WorkspaceEvent {
    private UserId memberId;
    private Timestamp addedAt;
}
