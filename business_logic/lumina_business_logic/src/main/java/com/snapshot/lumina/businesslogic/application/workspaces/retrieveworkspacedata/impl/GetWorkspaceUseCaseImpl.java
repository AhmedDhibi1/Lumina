package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.impl;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.GetWorkspaceUseCase;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetWorkspaceQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceDetailDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.mapper.WorkspaceRetrievalMapper;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GetWorkspaceUseCaseImpl implements GetWorkspaceUseCase {
    private final WorkspaceRepository workspaceRepository;

    public GetWorkspaceUseCaseImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceDetailDto execute(GetWorkspaceQuery query) {
        validateQuery(query);

        WorkspaceId workspaceId = WorkspaceId.builder()
                .value(query.getWorkspaceId())
                .build();

        UserId requestingUserId = UserId.builder()
                .value(query.getRequestingUserId())
                .build();

        // Load workspace
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workspace not found with id: " + query.getWorkspaceId()));

        // Validate access
        if (!workspace.hasMember(requestingUserId)) {
            throw new SecurityException(
                    "Access denied: User is not a member of this workspace");
        }

        return WorkspaceRetrievalMapper.toDetailDto(workspace, requestingUserId);
    }

    private void validateQuery(GetWorkspaceQuery query) {
        if (query == null) {
            log.error("Invalid query : Query cannot be null ");
            throw new IllegalArgumentException("Query cannot be null");
        }
        if (query.getWorkspaceId() == null) {
            log.error("Invalid query : Workspace ID is required ");
            throw new IllegalArgumentException("Workspace ID is required");
        }
        if (query.getRequestingUserId() == null) {
            log.error("Invalid query : Requesting user ID is required ");
            throw new IllegalArgumentException("Requesting user ID is required");
        }
    }
}
