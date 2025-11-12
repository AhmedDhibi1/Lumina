package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.TagId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging.Color;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging.TagName;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTag {
    private TagId tagId;
    private TagName tagName;
    private Color color;
    private Timestamp createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentTag that)) return false;
        return Objects.equals(tagId, that.tagId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagId);  // ✅ Null-safe
    }

    /*// Create new tag
    DocumentTag createTag(TagName name, Color color);

    // Apply tag to document
    DocumentTagMapping tagDocument(DocumentId documentId, TagId tagId, UserId taggedBy);

    // Remove tag from document
    void untagDocument(DocumentId documentId, TagId tagId);*/

}
