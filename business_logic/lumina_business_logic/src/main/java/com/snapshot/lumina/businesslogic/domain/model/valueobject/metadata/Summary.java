package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.*;

@Value
@Builder
public class Summary {
    String value;

    public Summary(String value) {
        this.value = value;
    }
}
