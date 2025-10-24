package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.OwnershipId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentOwnership {
    private OwnershipId ownershipId;
    private DocumentId documentId;
    private UserId ownerId;
    private Boolean isPrimaryOwner;
    private Timestamp acquiredAt;

   /* // Transfer primary ownership to another user
    DocumentOwnership transferOwnership(DocumentId documentId, UserId currentOwnerId, UserId newOwnerId);

    // Establish initial ownership when document is created
    DocumentOwnership establishOwnership(DocumentId documentId, UserId ownerId);

    // Validate ownership before critical operations
    void validateOwnership(DocumentId documentId, UserId userId);*/
}
