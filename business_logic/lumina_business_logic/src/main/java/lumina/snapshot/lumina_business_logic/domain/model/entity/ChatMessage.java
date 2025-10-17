package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.MessageContent;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.MessageRole;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.Sources;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.TokenCount;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.MessageId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.SessionId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private MessageId messageId;
    private SessionId sessionId;
    private MessageRole role;
    private MessageContent content;
    private Sources sources;
    private TokenCount tokenCount;
    private Timestamp createdAt;
}
