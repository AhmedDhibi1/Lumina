package lumina.snapshot.lumina_business_logic.domain.event.workspace;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WorkspaceDeletedEvent extends WorkspaceEvent {
    private Timestamp deletedAt;
}
