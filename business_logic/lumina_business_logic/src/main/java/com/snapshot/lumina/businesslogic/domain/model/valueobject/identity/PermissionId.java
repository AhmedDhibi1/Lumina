package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;


@Value
@Builder
public class PermissionId {
    UUID id;

    public PermissionId(UUID id) {
        this.id = id;
    }
}
