package lumina.snapshot.lumina_business_logic.domain.event.document;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.event.Event;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.Filename;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEvent extends Event {
    private DocumentId documentId;
    private UserId ownerId;
    private Filename filename;
    private WorkspaceId workspaceId;
}
