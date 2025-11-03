package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.*;

/*@AllArgsConstructor
@NoArgsConstructor*/
@Value
@Builder
public class MessageContent {
    String value;
    public MessageContent(String value) {
        this.value = value;
    }
}
