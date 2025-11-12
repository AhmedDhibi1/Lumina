package com.snapshot.lumina.businesslogic.domain.event.chat;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.chat.MessageRole;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MessageId;

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
