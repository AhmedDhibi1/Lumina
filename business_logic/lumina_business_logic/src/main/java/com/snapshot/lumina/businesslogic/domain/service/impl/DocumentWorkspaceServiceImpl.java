package com.snapshot.lumina.businesslogic.domain.service.impl;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Document;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.service.DocumentWorkspaceService;

import java.util.Optional;

public class DocumentWorkspaceServiceImpl implements DocumentWorkspaceService {
    @Override
    public void validateDocumentAddition(Document document, Workspace workspace, UserId userId) {
        if (!workspace.hasMember(userId)) {
            throw new SecurityException("User is not a workspace member");
        }

        // Check document ownership or workspace admin (both aggregates)
        boolean isDocumentOwner = document.isOwner(userId);
        Optional<WorkspaceMember> member = workspace.findMember(userId);
        boolean isWorkspaceAdmin = member.map(workspaceMember -> workspaceMember.getRole().isAdmin()).orElse(false);

        if (!isDocumentOwner && !isWorkspaceAdmin) {
            throw new SecurityException("User must be document owner or workspace admin");
        }

        // Check if document already in another workspace (Document aggregate)
        if (document.getWorkspaceId() != null &&
                !document.getWorkspaceId().equals(workspace.getWorkspaceId())) {
            throw new IllegalStateException("Document already in another workspace");
        }
    }

    @Override
    public void validateDocumentDeletion(Document document, Workspace workspace, UserId userId) {
        if (!workspace.hasMember(userId)) {
            throw new SecurityException("User is not a workspace member");
        }

        // Check document ownership or workspace admin (both aggregates)
        boolean isDocumentOwner = document.isOwner(userId);
        Optional<WorkspaceMember> member = workspace.findMember(userId);
        boolean isWorkspaceAdmin = member.map(workspaceMember -> workspaceMember.getRole().isAdmin()).orElse(false);

        if (!isDocumentOwner && !isWorkspaceAdmin) {
            throw new SecurityException("User must be document owner or workspace admin");
        }
    }

}
