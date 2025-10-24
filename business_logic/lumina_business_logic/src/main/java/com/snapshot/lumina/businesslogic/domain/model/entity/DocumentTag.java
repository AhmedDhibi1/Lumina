package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.TagId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging.Color;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging.TagName;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTag {
    private TagId tagId;
    private TagName tagName;
    private Color color;
    private Timestamp createdAt;

    /*// Create new tag
    DocumentTag createTag(TagName name, Color color);

    // Apply tag to document
    DocumentTagMapping tagDocument(DocumentId documentId, TagId tagId, UserId taggedBy);

    // Remove tag from document
    void untagDocument(DocumentId documentId, TagId tagId);*/

}
