package com.snapshot.lumina.businesslogic.domain.model.entity;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MetadataId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {
    private MetadataId metadataId;
    private DocumentId documentId;
    private DocumentTitle title;
    private Author author;
    private Timestamp documentCreationDate;
    private Keywords keywords;
    private Summary summary;
    private Language language;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentMetadata that)) return false;
        return Objects.equals(metadataId, that.metadataId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(metadataId);  // ✅ Null-safe
    }

    /*// Extract and create metadata from document
    DocumentMetadata extractMetadata(DocumentId documentId, FilePath filePath);

    // Update document metadata
    DocumentMetadata updateMetadata(DocumentId documentId, DocumentTitle title, Author author, Keywords keywords, Summary summary);
*/
}
