package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class ShareId {
    public ShareId(UUID value) {
        this.value = value;
    }

    UUID value;
}
