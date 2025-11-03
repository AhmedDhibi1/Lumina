package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class AclId {
    UUID value;

    public AclId(UUID value) {
        this.value = value;
    }
}
