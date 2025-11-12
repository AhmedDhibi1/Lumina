package com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.mapper;

import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.CreateWorkspaceCommand;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.request.CreateWorkspaceRequest;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.response.WorkspaceResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkspaceApiMapper {

    public CreateWorkspaceCommand toCommand(CreateWorkspaceRequest request, UUID authenticatedUserId) {
        return CreateWorkspaceCommand.of(
                request.getWorkspaceName(),
                request.getDescription(),
                authenticatedUserId
        );
    }

    public WorkspaceResponse toResponse(WorkspaceResponseDto dto) {
        return WorkspaceResponse.builder()
                .workspaceId(dto.getWorkspaceId())
                .workspaceName(dto.getWorkspaceName())
                .description(dto.getDescription())
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .memberCount(dto.getMemberCount())
                .build();
    }
}
