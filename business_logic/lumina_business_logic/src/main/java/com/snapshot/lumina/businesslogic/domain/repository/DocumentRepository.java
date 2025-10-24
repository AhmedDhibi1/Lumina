package com.snapshot.lumina.businesslogic.domain.repository;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Document;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.DocumentSearchCriteria;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.DocumentStatus;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.document.Filename;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.Author;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.DocumentTitle;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata.Keywords;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository {
    Optional<Document> save(Document document);
    List<Document> saveAll(List<Document> documents);
    Optional<Document> findById(DocumentId documentId);
    boolean existsById(DocumentId documentId);
    void deleteById(DocumentId documentId);
    void delete(Document document);
    long count();
    PageResponse<Document> findAllByUserId(UserId userId, PageRequest pageRequest);
    PageResponse<Document> findByMetadata(UserId userId, DocumentTitle title, Author author, Keywords keywords, PageRequest pageRequest);
    PageResponse<Document> findByFilename(UserId userId, Filename filename, PageRequest pageRequest);
    PageResponse<Document> findByWorkspace(
            WorkspaceId workspaceId,
            String searchQuery,
            PageRequest pageRequest
    );
    PageResponse<Document> findByStatus(
            UserId userId,
            DocumentStatus status,
            PageRequest pageRequest
    );
    PageResponse<Document> findByCriteria(
            UserId userId,
            DocumentSearchCriteria criteria,
            PageRequest pageRequest
    );
    PageResponse<Document> fullTextSearch(
            UserId userId,
            String searchText,
            PageRequest pageRequest
    );
    PageResponse<Document> findRecentlyAccessed(UserId userId, PageRequest pageRequest);
    PageResponse<Document> findRecentlyUploaded(UserId userId, PageRequest pageRequest);
    PageResponse<Document> findUploadedAfter(
            UserId userId,
            Timestamp after,
            PageRequest pageRequest
    );
    //Finds documents accessed after a specific timestamp.
    PageResponse<Document> findAccessedAfter(
            UserId userId,
            Timestamp after,
            PageRequest pageRequest
    );
    PageResponse<Document> findAllByWorkspaceId(
            WorkspaceId workspaceId,
            PageRequest pageRequest
    );

    long countByWorkspaceId(WorkspaceId workspaceId);
    boolean existsByIdAndWorkspaceId(DocumentId documentId, WorkspaceId workspaceId);
    PageResponse<Document> findAllOwnedByUser(UserId userId, PageRequest pageRequest);
    PageResponse<Document> findAllSharedWithUser(UserId userId, PageRequest pageRequest);
    long countByOwnerId(UserId userId);
    boolean existsByUserIdAndFilename(UserId userId, Filename filename);
    Optional<Document> findByUserIdAndFilename(UserId userId, Filename filename);
}
