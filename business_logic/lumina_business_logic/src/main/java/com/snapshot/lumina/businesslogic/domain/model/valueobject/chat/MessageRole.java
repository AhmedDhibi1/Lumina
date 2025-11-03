package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.*;

@Value
@Builder
public class MessageRole {
    String role; // USER, ASSISTANT
    public MessageRole(String role) {
        this.role = role;
    }
}
