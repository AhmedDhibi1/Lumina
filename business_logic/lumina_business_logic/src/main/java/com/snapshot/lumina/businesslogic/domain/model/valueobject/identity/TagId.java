package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class TagId {
    UUID tagId;

    public TagId(UUID tagId) {
        this.tagId = tagId;
    }
}
