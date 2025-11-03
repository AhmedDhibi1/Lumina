package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class ActivityId {
    UUID id;

    public ActivityId(UUID id) {
        this.id = id;
    }
}
