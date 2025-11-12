package com.snapshot.lumina.businesslogic.domain.model.valueobject.versioning;

import lombok.*;

@Value
@Builder
public class ChangeDescription {
    String value;

    public ChangeDescription(String value) {
        this.value = value;
    }
}
