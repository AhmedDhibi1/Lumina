package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.impl;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.GetWorkspacesByCreatorUseCase;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetWorkspacesByCreatorQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.PageResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.mapper.WorkspaceMapper;
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
public class GetWorkspacesByCreatorUseCaseImpl implements GetWorkspacesByCreatorUseCase {
    private final WorkspaceRepository workspaceRepository;

    public GetWorkspacesByCreatorUseCaseImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<WorkspaceResponseDto> execute(
            GetWorkspacesByCreatorQuery query) {
        validateQuery(query);

        UserId creatorId = UserId.builder()
                .value(query.getCreatorId())
                .build();

        PageRequest pageRequest = buildPageRequest(query);

        PageResponse<?> workspacePage = workspaceRepository.findAllByCreator(
                creatorId, pageRequest);

        PageResponse<WorkspaceResponseDto> dtoPage = workspacePage.map(
                workspace -> WorkspaceMapper.toResponseDto(
                        (com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace) workspace,
                        creatorId));

        return WorkspaceMapper.toPageResponseDto(dtoPage);
    }

    private PageRequest buildPageRequest(GetWorkspacesByCreatorQuery query) {
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

    private void validateQuery(GetWorkspacesByCreatorQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        if (query.getCreatorId() == null) {
            throw new IllegalArgumentException("Creator ID is required");
        }
    }
}
