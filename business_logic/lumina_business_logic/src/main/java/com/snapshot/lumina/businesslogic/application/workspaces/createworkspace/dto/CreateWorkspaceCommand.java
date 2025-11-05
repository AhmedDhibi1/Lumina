package com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.Description;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import lombok.*;

import java.util.UUID;

@Value
@Builder
public class CreateWorkspaceCommand {
    WorkspaceName workspaceName;
    Description description;
    UserId creatorUserId;

    public static CreateWorkspaceCommand of(String name, String desc, UUID userId) {
        return new CreateWorkspaceCommand(
                WorkspaceName.of(name),
                desc != null ? Description.of(desc) : null,
                UserId.of(userId)
        );
    }
}
