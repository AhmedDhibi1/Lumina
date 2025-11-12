package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.*;

@Value
@Builder
public class Author {
    String value;

    public Author(String value) {
        this.value = value;
    }
}
