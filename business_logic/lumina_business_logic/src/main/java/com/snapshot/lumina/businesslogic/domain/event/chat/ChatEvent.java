package com.snapshot.lumina.businesslogic.domain.event.chat;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.event.Event;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.SessionId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEvent extends Event {
    private SessionId sessionId;
    private UserId userId;
    private DocumentId documentId;
}
