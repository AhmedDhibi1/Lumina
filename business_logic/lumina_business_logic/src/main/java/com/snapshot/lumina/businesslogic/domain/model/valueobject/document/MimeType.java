package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class MimeType {
    String value;

    public MimeType(String value) {
        this.value = value;
    }
// Validate file type
    //void validateFileType(MimeType mimeType);
}
