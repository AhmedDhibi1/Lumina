package com.snapshot.lumina.businesslogic.domain.event.document;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadedEvent extends DocumentEvent {
    private Timestamp uploadedAt;
    private UserId uploadedBy;
}
