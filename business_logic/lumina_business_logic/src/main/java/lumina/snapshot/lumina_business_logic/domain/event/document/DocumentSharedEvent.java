package lumina.snapshot.lumina_business_logic.domain.event.document;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.Permissions;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSharedEvent extends DocumentEvent {
    private UserId sharedBy;
    private UserId sharedWith;
    private List<Permissions> permissions;
    private Timestamp sharedAt;
}
