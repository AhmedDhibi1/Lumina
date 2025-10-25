package com.snapshot.lumina.businesslogic.application.workspaces.createworkspace;

import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.CreateWorkspaceCommand;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;

public interface CreateWorkspaceUseCase {
    public WorkspaceResponseDto execute(CreateWorkspaceCommand command);
}
