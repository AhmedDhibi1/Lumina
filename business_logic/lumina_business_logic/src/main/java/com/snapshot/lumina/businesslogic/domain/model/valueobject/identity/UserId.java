package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class UserId {
    UUID value;

    public UserId(UUID value) {
        this.value = value;
    }

    public static UserId of(UUID userId) {
        return new UserId(userId);
    }
}
