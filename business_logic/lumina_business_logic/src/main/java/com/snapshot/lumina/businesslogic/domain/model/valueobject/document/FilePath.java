package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class FilePath {
    String value;

    public FilePath(String value) {
        this.value = value;
    }
}
