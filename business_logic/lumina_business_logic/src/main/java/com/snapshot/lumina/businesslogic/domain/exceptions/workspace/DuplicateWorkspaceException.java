package com.snapshot.lumina.businesslogic.domain.exceptions.workspace;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;

public class DuplicateWorkspaceException extends WorkspaceDomainException {
    private final WorkspaceName workspaceName;
    private final UserId ownerId;

    public DuplicateWorkspaceException(WorkspaceName workspaceName, UserId ownerId) {
        super(String.format("Workspace '%s' already exists for this owner. Please choose a different name.",
                workspaceName.getValue()));
        this.workspaceName = workspaceName;
        this.ownerId = ownerId;
    }

    public WorkspaceName getWorkspaceName() {
        return workspaceName;
    }

    public UserId getOwnerId() {
        return ownerId;
    }
}
