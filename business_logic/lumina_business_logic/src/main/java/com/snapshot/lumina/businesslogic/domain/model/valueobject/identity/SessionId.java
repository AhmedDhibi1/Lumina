package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class SessionId {
    private UUID value;

    public SessionId(UUID value) {
        this.value = value;
    }
}
