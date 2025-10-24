package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Filename {
    private String value;

    // Validate filename
    //void validateFilename(Filename filename);
}
