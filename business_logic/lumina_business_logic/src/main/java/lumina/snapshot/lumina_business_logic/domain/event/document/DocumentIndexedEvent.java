package lumina.snapshot.lumina_business_logic.domain.event.document;

import lombok.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.ChunkCount;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.EmbeddingModel;

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
