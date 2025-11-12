package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.TagId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.Author;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.Keywords;

import java.util.List;

@Value
@Builder
public class DocumentSearchCriteria {
    Filename filename;
    Author author;
    Keywords keywords;
    List<TagId> tagIds;
    DocumentStatus status;
    Timestamp uploadedAfter;
    Timestamp uploadedBefore;
    FileSize minSize;
    FileSize maxSize;
    WorkspaceId workspaceId;

    public DocumentSearchCriteria(
            Timestamp uploadedBefore,
            Filename filename,
            Author author,
            Keywords keywords,
            DocumentStatus status,
            List<TagId> tagIds,
            Timestamp uploadedAfter,
            FileSize minSize,
            FileSize maxSize,
            WorkspaceId workspaceId
    ) {
        this.uploadedBefore = uploadedBefore;
        this.filename = filename;
        this.author = author;
        this.keywords = keywords;
        this.status = status;
        this.tagIds = tagIds;
        this.uploadedAfter = uploadedAfter;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.workspaceId = workspaceId;
    }
}
