package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSize {
    private Long bytes;

    // Validate file size limits
    //void validateFileSize(FileSize fileSize);
}
