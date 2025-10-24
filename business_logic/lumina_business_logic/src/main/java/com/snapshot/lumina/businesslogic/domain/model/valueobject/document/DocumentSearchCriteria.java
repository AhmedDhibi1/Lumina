package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.TagId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.Author;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.Keywords;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchCriteria {
    private Filename filename;
    private Author author;
    private Keywords keywords;
    private List<TagId> tagIds;
    private DocumentStatus status;
    private Timestamp uploadedAfter;
    private Timestamp uploadedBefore;
    private FileSize minSize;
    private FileSize maxSize;
    private WorkspaceId workspaceId;
}
