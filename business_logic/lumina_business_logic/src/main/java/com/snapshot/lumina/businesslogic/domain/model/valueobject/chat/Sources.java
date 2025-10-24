package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sources {
    private String jsonData; // JSON array of source references
}
