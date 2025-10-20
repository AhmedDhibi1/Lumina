package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentMetadata;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat.MessageContent;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.document.FilePath;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.*;

public interface DocumentMetadataService {

    // Extract and create metadata from document
    DocumentMetadata extractMetadata(DocumentId documentId, FilePath filePath);

    // Update document metadata
    DocumentMetadata updateMetadata(DocumentId documentId, DocumentTitle title, Author author, Keywords keywords, Summary summary);

    // Generate AI summary for document
    Summary generateSummary(DocumentId documentId, MessageContent documentContent);

    // Extract keywords from document
    Keywords extractKeywords(MessageContent documentContent);

    // Detect document language
    Language detectLanguage(MessageContent documentContent);

    // Enrich metadata with AI-generated insights
    DocumentMetadata enrichMetadata(DocumentId documentId);
}
