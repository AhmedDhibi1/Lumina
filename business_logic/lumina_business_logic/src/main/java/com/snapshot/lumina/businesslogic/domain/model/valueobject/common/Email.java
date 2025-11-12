package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import lombok.*;

@Builder
@Value
public class Email {
    String value;

    public Email(String value) {
        this.value = value;
    }
}
