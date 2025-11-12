package com.snapshot.lumina.businesslogic.domain.service;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Document;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

public interface DocumentWorkspaceService {
    void validateDocumentAddition(Document document, Workspace workspace, UserId userId);
    void validateDocumentDeletion(Document document, Workspace workspace, UserId userId);
}
