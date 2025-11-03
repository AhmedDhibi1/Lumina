package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.chat.MessageContent;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.chat.MessageRole;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.chat.Sources;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.chat.TokenCount;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MessageId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.SessionId;

import java.util.Objects;

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
    private MessageId previousMessageId;  // Link to previous message (null if first)
    private MessageId nextMessageId;// Link to next message (null if last)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage that)) return false;
        return Objects.equals(messageId, that.messageId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(messageId);  // ✅ Null-safe
    }
}
