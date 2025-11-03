package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class WorkspaceId {
    UUID value;

    public WorkspaceId(UUID value) {
        this.value = value;
    }
}
