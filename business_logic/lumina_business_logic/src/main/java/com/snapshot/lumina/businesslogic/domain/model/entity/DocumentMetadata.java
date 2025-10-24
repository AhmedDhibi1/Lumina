package com.snapshot.lumina.businesslogic.domain.model.entity;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MetadataId;
import lumina.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.*;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.*;

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

    /*// Extract and create metadata from document
    DocumentMetadata extractMetadata(DocumentId documentId, FilePath filePath);

    // Update document metadata
    DocumentMetadata updateMetadata(DocumentId documentId, DocumentTitle title, Author author, Keywords keywords, Summary summary);
*/
}
