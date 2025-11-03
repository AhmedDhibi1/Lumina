package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.*;

@Value
@Builder
public class DocumentTitle {
    String value;

    public DocumentTitle(String value) {
        this.value = value;
    }
}
