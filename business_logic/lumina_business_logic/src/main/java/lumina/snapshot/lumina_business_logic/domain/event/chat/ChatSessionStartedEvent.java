package lumina.snapshot.lumina_business_logic.domain.event.chat;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSessionStartedEvent extends ChatEvent {
    private Timestamp startedAt;
}
