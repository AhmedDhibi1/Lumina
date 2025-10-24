package com.snapshot.lumina.businesslogic.domain.event.document;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.ChunkCount;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.EmbeddingModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentIndexedEvent extends DocumentEvent {
    private Timestamp indexedAt;
    private ChunkCount chunkCount;
    private EmbeddingModel embeddingModel;
}
