package com.snapshot.lumina.businesslogic.domain.event.chat;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSessionStartedEvent extends ChatEvent {
    private Timestamp startedAt;
}
