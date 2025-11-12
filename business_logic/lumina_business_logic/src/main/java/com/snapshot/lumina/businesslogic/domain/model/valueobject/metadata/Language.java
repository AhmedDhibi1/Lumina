package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.*;

@Value
@Builder
public class Language {
    String code; // ISO 639-1: en, fr, es, etc.

    public Language(String code) {
        this.code = code;
    }
}
