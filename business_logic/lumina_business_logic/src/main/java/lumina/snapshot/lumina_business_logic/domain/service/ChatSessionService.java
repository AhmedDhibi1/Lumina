package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.ChatSession;
import lumina.snapshot.lumina_business_logic.domain.model.entity.ChatMessage;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.*;

import java.util.List;

public interface ChatSessionService {

    // Create new chat session
    ChatSession createSession(UserId userId, DocumentId documentId, WorkspaceId workspaceId, SessionTitle title);

    // Add message to end of session (append to tail)
    ChatMessage addMessage(SessionId sessionId, MessageRole role, MessageContent content, Sources sources, TokenCount tokenCount);

    // Insert message after specific message (for threading/edits)
    ChatMessage insertMessageAfter(SessionId sessionId, MessageId afterMessageId, MessageRole role, MessageContent content, Sources sources, TokenCount tokenCount);

    // Get entire session as ordered list (traverse linked list)
    List<ChatMessage> getSessionHistory(SessionId sessionId);

    // Get messages starting from specific message (paginated traversal)
    List<ChatMessage> getMessagesFrom(SessionId sessionId, MessageId startMessageId, Integer limit);

    // Get last N messages (traverse backward from tail)
    List<ChatMessage> getLastMessages(SessionId sessionId, Integer count);

    // Get first message in session
    ChatMessage getFirstMessage(SessionId sessionId);

    // Get last message in session
    ChatMessage getLastMessage(SessionId sessionId);

    // Get next message
    ChatMessage getNextMessage(MessageId currentMessageId);

    // Get previous message
    ChatMessage getPreviousMessage(MessageId currentMessageId);

    // Get user's recent sessions
    List<ChatSession> getUserRecentSessions(UserId userId, Integer limit);

    // Get sessions for a document
    List<ChatSession> getDocumentSessions(DocumentId documentId);

    // Update session last message timestamp
    ChatSession updateLastMessageTime(SessionId sessionId);

    // Delete session (and all linked messages)
    void deleteSession(SessionId sessionId, UserId requesterId);

    // Delete specific message (update links)
    void deleteMessage(MessageId messageId, UserId requesterId);

    // Validate session access
    void validateSessionAccess(SessionId sessionId, UserId userId);

    // Get message count
    Integer getMessageCount(SessionId sessionId);
}
