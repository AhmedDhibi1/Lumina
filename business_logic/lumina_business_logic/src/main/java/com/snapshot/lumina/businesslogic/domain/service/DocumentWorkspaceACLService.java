package com.snapshot.lumina.businesslogic.domain.service;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Document;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

import java.security.Permission;

public interface DocumentWorkspaceACLService {



    boolean canAccessInWorkspace(Document document, Workspace workspace, UserId userId);

    /**
     * Enforce workspace-aware access control
     */
    void enforceWorkspaceAccess(Document document, Workspace workspace, UserId userId, Permission permission);

    public void validateWorkspaceAccess(UserId userId, WorkspaceMember member);
}
