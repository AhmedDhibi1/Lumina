package com.snapshot.lumina.businesslogic.domain.event.document;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.event.DomainEvent;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.Filename;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEvent extends DomainEvent {
    private DocumentId documentId;
    private UserId ownerId;
    private Filename filename;
    private WorkspaceId workspaceId;
}
