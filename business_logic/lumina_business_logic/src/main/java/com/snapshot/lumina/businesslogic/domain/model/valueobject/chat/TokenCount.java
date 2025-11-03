package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.*;

@Value
@Builder
public class TokenCount {
    Integer count;

    public TokenCount(Integer count) {
        this.count = count;
    }
}
