package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.mapper;

import com.snapshot.lumina.businesslogic.application.workspaces.addmember.dto.WorkspaceMemberDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.PageResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceDetailDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceStatsDto;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

public class WorkspaceMapper {

    public static WorkspaceResponseDto toResponseDto(Workspace workspace, UserId currentUserId) {
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
                .isAdmin(currentUserId != null ? workspace.isAdmin(currentUserId) : false)
                .build();
    }


    public static WorkspaceDetailDto toDetailDto(Workspace workspace, UserId currentUserId) {
        WorkspaceMember currentMember = workspace.getMembers().stream()
                .filter(m -> m.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        long adminCount = workspace.getMembers().stream()
                .filter(m -> m.getRole().toString().equals("ADMIN") ||
                        m.getRole().toString().equals("OWNER"))
                .count();

        return WorkspaceDetailDto.builder()
                .workspaceId(workspace.getWorkspaceId().getValue())
                .workspaceName(workspace.getWorkspaceName().getValue())
                .description(workspace.getDescription() != null ?
                        workspace.getDescription().getValue() : null)
                .createdBy(workspace.getCreatedBy().getValue())
                .createdAt(workspace.getCreatedAt().getValue())
                .updatedAt(workspace.getUpdatedAt().getValue())
                .memberCount(workspace.getMembers().size())
                .adminCount((int) adminCount)
                .isAdmin(workspace.isAdmin(currentUserId))
                .currentUserRole(currentMember != null ?
                        currentMember.getRole().toString() : null)
                .build();
    }

    public static WorkspaceMemberDto toMemberDto(WorkspaceMember member) {
        return WorkspaceMemberDto.builder()
                .membershipId(member.getMembershipId().getValue())
                .workspaceId(member.getWorkspaceId().getValue())
                .userId(member.getUserId().getValue())
                .role(member.getRole().toString())
                .joinedAt(member.getJoinedAt().getValue())
                .build();
    }



    public static WorkspaceStatsDto toStatsDto(Workspace workspace,
                                               long totalMembers, long totalAdmins) {
        return WorkspaceStatsDto.builder()
                .workspaceId(workspace.getWorkspaceId().getValue())
                .workspaceName(workspace.getWorkspaceName().getValue())
                .totalMembers(totalMembers)
                .totalAdmins(totalAdmins)
                .createdAt(workspace.getCreatedAt().getValue())
                .lastUpdated(workspace.getUpdatedAt().getValue())
                .build();
    }

    public static <T> PageResponseDto<T> toPageResponseDto(PageResponse<T> pageResponse) {
        return PageResponseDto.<T>builder()
                .content(pageResponse.getContent())
                .pageNumber(pageResponse.getPageNumber())
                .pageSize(pageResponse.getPageSize())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .first(pageResponse.isFirst())
                .last(pageResponse.isLast())
                .hasNext(pageResponse.hasNext())
                .hasPrevious(pageResponse.hasPrevious())
                .build();
    }
}
