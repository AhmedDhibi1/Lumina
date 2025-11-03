package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class ChunkCount {
    private Integer count;

    public ChunkCount(Integer count) {
        this.count = count;
    }
}
