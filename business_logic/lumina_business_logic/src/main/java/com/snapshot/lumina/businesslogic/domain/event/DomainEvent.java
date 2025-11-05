package com.snapshot.lumina.businesslogic.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DomainEvent {
    private UUID eventId;
    private String eventName;
    private Timestamp occurredAt;
    private int eventVersion;
    private String eventType;
}
