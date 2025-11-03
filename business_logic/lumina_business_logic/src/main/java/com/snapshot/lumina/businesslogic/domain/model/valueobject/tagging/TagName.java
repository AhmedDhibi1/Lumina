package com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging;

import lombok.*;

@Builder
@Value
public class TagName {
    String value;

    public TagName(String value) {
        this.value = value;
    }
}
