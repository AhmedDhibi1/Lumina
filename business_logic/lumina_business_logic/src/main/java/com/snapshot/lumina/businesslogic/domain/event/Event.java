package com.snapshot.lumina.businesslogic.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private UUID eventId;
    private String eventName;
    private String description;
    private Timestamp occurredAt;
}
