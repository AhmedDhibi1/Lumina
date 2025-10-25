package com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkspaceCommand {
    private String workspaceName;
    private String description;
    private UUID creatorUserId;
}
