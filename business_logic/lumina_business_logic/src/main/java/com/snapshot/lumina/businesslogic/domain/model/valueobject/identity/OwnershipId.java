package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder

public class OwnershipId {
    UUID value;

    public OwnershipId(UUID value) {
        this.value = value;
    }
}
