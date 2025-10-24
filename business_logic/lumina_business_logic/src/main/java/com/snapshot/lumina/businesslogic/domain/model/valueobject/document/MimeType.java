package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MimeType {
    private String value;
    // Validate file type
    //void validateFileType(MimeType mimeType);
}
