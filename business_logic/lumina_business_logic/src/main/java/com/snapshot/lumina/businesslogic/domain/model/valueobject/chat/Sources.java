package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.*;

@Value
@Builder

public class Sources {
    public Sources(String jsonData) {
        this.jsonData = jsonData;
    }

    String jsonData; // JSON array of source references
}
