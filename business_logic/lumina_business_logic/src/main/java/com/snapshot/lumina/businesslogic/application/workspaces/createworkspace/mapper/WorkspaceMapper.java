package com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.mapper;

import com.snapshot.lumina.businesslogic.application.workspaces.addmember.mapper.WorkspaceMemberMapper;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceDetailDto;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;

import java.util.stream.Collectors;

public class WorkspaceMapper {
    public static WorkspaceResponseDto toResponseDto(Workspace workspace){
        return WorkspaceResponseDto.builder()
                .workspaceId(workspace.getWorkspaceId().getValue())
                .workspaceName(workspace.getWorkspaceName().getValue())
                .description(workspace.getDescription() != null ?
                        workspace.getDescription().getValue() : null)
                .createdBy(workspace.getCreatedBy().getValue())
                .createdAt(workspace.getCreatedAt().getValue())
                .updatedAt(workspace.getUpdatedAt().getValue())
                .memberCount(workspace.getMembers() != null ?
                        workspace.getMembers().size() : 0)
                .build();
    };
    public static WorkspaceDetailDto toDetailDto(Workspace workspace){
        return WorkspaceDetailDto.builder()
                .workspaceId(workspace.getWorkspaceId().getValue())
                .workspaceName(workspace.getWorkspaceName().getValue())
                .description(workspace.getDescription() != null ?
                        workspace.getDescription().getValue() : null)
                .createdBy(workspace.getCreatedBy().getValue())
                .createdAt(workspace.getCreatedAt().getValue())
                .updatedAt(workspace.getUpdatedAt().getValue())
                .members(workspace.getMembers().stream()
                        .map(WorkspaceMemberMapper::toMemberDto)
                        .collect(Collectors.toList()))
                .build();
    };
}
