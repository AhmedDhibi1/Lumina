package lumina.snapshot.lumina_business_logic.domain.event.document;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDeletedEvent extends DocumentEvent {
    private UserId deletedBy;
    private Timestamp deletedAt;
}
