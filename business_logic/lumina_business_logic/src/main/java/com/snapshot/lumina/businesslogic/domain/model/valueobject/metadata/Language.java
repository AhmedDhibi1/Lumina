package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Language {
    private String code; // ISO 639-1: en, fr, es, etc.
}
