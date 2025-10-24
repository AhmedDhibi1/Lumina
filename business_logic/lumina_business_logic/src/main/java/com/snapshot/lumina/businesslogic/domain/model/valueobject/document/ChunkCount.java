package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChunkCount {
    private Integer count;
}
