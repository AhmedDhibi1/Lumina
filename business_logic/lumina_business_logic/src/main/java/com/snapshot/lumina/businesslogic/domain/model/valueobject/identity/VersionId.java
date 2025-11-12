package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class VersionId {
    UUID id;

    public VersionId(UUID id) {
        this.id = id;
    }
}
