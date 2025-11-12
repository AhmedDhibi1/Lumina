package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.*;

import java.util.UUID;

@Value
@Builder
public class MembershipId {
    UUID value;

    public MembershipId(UUID value) {
        this.value = value;
    }
}
