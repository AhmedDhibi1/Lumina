package lumina.snapshot.lumina_business_logic.domain.event.chat;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.event.Event;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.SessionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatEvent extends Event {
    private SessionId sessionId;
    private UserId userId;
    private DocumentId documentId;
}
