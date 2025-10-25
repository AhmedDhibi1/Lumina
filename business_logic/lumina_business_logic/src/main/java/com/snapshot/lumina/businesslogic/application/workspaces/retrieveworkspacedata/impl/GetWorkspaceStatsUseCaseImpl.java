package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.impl;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.GetWorkspaceStatsUseCase;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetWorkspaceStatsQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceStatsDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.mapper.WorkspaceMapper;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GetWorkspaceStatsUseCaseImpl implements GetWorkspaceStatsUseCase {
    private final WorkspaceRepository workspaceRepository;

    public GetWorkspaceStatsUseCaseImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceStatsDto execute(GetWorkspaceStatsQuery query) {
        validateQuery(query);

        WorkspaceId workspaceId = WorkspaceId.builder()
                .value(query.getWorkspaceId())
                .build();

        UserId requestingUserId = UserId.builder()
                .value(query.getRequestingUserId())
                .build();

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workspace not found"));

        // Validate access
        if (!workspace.hasMember(requestingUserId)) {
            throw new SecurityException(
                    "Access denied: User is not a member of this workspace");
        }

        // Get stats from repository
        long totalMembers = workspaceRepository.countMembersByWorkspace(workspaceId);
        long totalAdmins = workspaceRepository.countAdminsByWorkspace(workspaceId);

        return WorkspaceMapper.toStatsDto(workspace, totalMembers, totalAdmins);
    }

    private void validateQuery(GetWorkspaceStatsQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        if (query.getWorkspaceId() == null) {
            throw new IllegalArgumentException("Workspace ID is required");
        }
        if (query.getRequestingUserId() == null) {
            throw new IllegalArgumentException("Requesting user ID is required");
        }
    }

}
