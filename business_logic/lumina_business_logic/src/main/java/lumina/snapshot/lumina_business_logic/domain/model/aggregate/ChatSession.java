package lumina.snapshot.lumina_business_logic.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.entity.ChatMessage;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.*;
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
    private String ChatLLMModel;
    private Timestamp createdAt;
    private Timestamp lastMessageAt;

    private MessageId firstMessageId;  // Head of linked list
    private MessageId lastMessageId;   // Tail of linked list
    private Integer messageCount;      // Track total messages
}
