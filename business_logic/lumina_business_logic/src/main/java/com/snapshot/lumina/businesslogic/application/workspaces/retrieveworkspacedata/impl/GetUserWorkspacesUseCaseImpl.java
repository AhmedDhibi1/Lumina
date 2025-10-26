package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.impl;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.GetUserWorkspacesUseCase;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetUserWorkspacesQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.PageResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.mapper.WorkspaceMapper;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.SortCriteria;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.SortingOrder;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class GetUserWorkspacesUseCaseImpl implements GetUserWorkspacesUseCase {

    private final WorkspaceRepository workspaceRepository;

    public GetUserWorkspacesUseCaseImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<WorkspaceResponseDto> execute(GetUserWorkspacesQuery query) {
        validateQuery(query);

        UserId userId = UserId.builder()
                .value(query.getUserId())
                .build();

        // Build page request
        PageRequest pageRequest = buildPageRequest(query);

        // Fetch workspaces
        PageResponse<?> workspacePage = workspaceRepository.findAllByMembership(
                userId, pageRequest);

        // Map to DTOs
        PageResponse<WorkspaceResponseDto> dtoPage = workspacePage.map(
                workspace -> {
                    WorkspaceResponseDto res= WorkspaceMapper.toResponseDto((Workspace) workspace, userId);
                    res.setIsAdmin(workspaceRepository.isAdminOfWorkspace(((Workspace) workspace).getWorkspaceId(), userId));
                    return res;
                });

        return WorkspaceMapper.toPageResponseDto(dtoPage);
    }

    private PageRequest buildPageRequest(GetUserWorkspacesQuery query) {
        int pageNumber = query.getPageNumber() != null ? query.getPageNumber() : 0;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 20;

        SortCriteria sortCriteria = null;
        if (query.getSortBy() != null && !query.getSortBy().trim().isEmpty()) {
            SortingOrder sortOrder = "desc".equalsIgnoreCase(query.getSortOrder())
                    ? SortingOrder.DESC : SortingOrder.ASC;
            sortCriteria = new SortCriteria(query.getSortBy(), sortOrder);
        }

        return new PageRequest(pageNumber, pageSize, sortCriteria);
    }

    private void validateQuery(GetUserWorkspacesQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        if (query.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }
}
