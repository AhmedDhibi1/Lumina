package lumina.snapshot.lumina_business_logic.domain.event.chat;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.MessageRole;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.MessageId;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageSentEvent extends ChatEvent {
    private MessageId messageId;
    private MessageRole role;
    private Timestamp sentAt;
}
