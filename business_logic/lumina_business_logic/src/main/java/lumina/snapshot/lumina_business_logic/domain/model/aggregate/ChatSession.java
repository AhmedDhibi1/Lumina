package lumina.snapshot.lumina_business_logic.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.entity.ChatMessage;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.SessionId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.SessionTitle;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatSession {
    private SessionId sessionId;
    private UserId userId;
    private DocumentId documentId;
    private WorkspaceId workspaceId;
    private SessionTitle title;
    private Timestamp createdAt;
    private Timestamp lastMessageAt;

    private List<ChatMessage> messages;
}
