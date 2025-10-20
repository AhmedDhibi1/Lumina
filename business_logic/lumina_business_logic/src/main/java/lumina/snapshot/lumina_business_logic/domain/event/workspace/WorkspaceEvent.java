package lumina.snapshot.lumina_business_logic.domain.event.workspace;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.event.Event;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceEvent extends Event {
    private WorkspaceId workspaceId;
    private UserId ownerId;
}
