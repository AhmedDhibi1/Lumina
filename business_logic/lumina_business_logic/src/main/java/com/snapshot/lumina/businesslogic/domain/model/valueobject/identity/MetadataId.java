package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class MetadataId {
    UUID value;

    public MetadataId(UUID value) {
        this.value = value;
    }
}
