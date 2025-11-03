package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class MessageId {
    UUID value;

    public MessageId(UUID value) {
        this.value = value;
    }
}
