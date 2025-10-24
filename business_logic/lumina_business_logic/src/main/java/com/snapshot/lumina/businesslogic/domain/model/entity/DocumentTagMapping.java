package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.TagId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTagMapping {
    private DocumentId documentId;
    private TagId tagId;
    private UserId taggedBy;
    private Timestamp taggedAt;
}
