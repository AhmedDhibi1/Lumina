package lumina.snapshot.lumina_business_logic.domain.event.workspace;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceCreatedEvent extends WorkspaceEvent {
    private Timestamp createdAt;
}
