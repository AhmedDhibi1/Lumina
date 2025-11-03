package com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace;

import lombok.*;

@Builder
@Value
public class Description {
    String value;

    public Description(String value) {
        this.value = value;
    }
}
