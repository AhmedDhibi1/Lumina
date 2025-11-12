package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class FileSize {
    private Long bytes;

    public FileSize(Long bytes) {
        this.bytes = bytes;
    }
// Validate file size limits
    //void validateFileSize(FileSize fileSize);
}
