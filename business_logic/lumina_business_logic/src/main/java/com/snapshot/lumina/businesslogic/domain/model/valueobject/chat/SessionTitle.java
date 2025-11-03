package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.*;

@Value
@Builder
public class SessionTitle {
    String value;

    public SessionTitle(String value) {
        this.value = value;
    }
}
