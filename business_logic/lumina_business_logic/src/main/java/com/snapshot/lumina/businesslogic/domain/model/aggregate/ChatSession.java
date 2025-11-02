package com.snapshot.lumina.businesslogic.domain.model.aggregate;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.*;
//import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.chat.SessionTitle;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;

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
