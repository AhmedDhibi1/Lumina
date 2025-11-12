package com.snapshot.lumina.businesslogic.domain.event.document;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDeletedEvent extends DocumentEvent {
    private UserId deletedBy;
    private Timestamp deletedAt;
}
