package com.snapshot.lumina.businesslogic.infrastructure.persistence.adapter;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceEntity;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper.PaginationMapper;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper.WorkspaceEntityMapper;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper.WorkspaceMemberEntityMapper;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.repository.WorkspaceJpaRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepo workspaceJpaRepo;
    private final WorkspaceEntityMapper workspaceMapper;
    private final PaginationMapper paginationMapper;
    private final WorkspaceMemberEntityMapper workspaceMemberMapper;

    public WorkspaceRepositoryAdapter(WorkspaceJpaRepo workspaceJpaRepo, WorkspaceEntityMapper workspaceMapper, PaginationMapper paginationMapper, WorkspaceMemberEntityMapper workspaceMemberMapper) {
        this.workspaceJpaRepo = workspaceJpaRepo;
        this.workspaceMapper = workspaceMapper;
        this.paginationMapper = paginationMapper;
        this.workspaceMemberMapper = workspaceMemberMapper;
    }

    @Override
    @Transactional
    public Optional<Workspace> save(@NotNull Workspace workspace) {
        return Optional.ofNullable(workspaceMapper.toDomain(
                workspaceJpaRepo.save(
                        workspaceMapper.toEntityWithMembers(workspace)
                )
        ));
    }

    @Override
    public Optional<Workspace> findById(WorkspaceId workspaceId) {
        return workspaceJpaRepo.findById(workspaceId.getValue())
                .map(workspaceMapper::toDomain);
    }

    @Override
    public boolean existsById(WorkspaceId workspaceId) {
        return workspaceJpaRepo.existsById(workspaceId.getValue());
    }

    @Override
    public boolean existsByName(WorkspaceName workspaceName) {
        return workspaceJpaRepo.existsByWorkspaceName(workspaceName.getValue());
    }

    @Override
    public void delete(WorkspaceId workspaceId) {
        workspaceJpaRepo.deleteById(workspaceId.getValue());
    }

    @Override
    public PageResponse<Workspace> findAllByCreator(UserId creatorId, PageRequest pageRequest) {
        Pageable pageable = paginationMapper.toSpringPageable(pageRequest);
        Page<WorkspaceEntity> entityPage = workspaceJpaRepo.findAllByCreatorId(creatorId.getValue(), pageable);
        List<Workspace> workspaces = entityPage.getContent().stream()
                .map(workspaceMapper::toDomain)
                .collect(Collectors.toList());

        return PageResponse.of(workspaces, pageRequest, entityPage.getTotalElements());
    }

    @Override
    public PageResponse<Workspace> findAllByMembership(UserId userId, PageRequest pageRequest) {
        Pageable pageable=paginationMapper.toSpringPageable(pageRequest);
        Page<WorkspaceEntity> entityPage= workspaceJpaRepo.findAllUserMembershipWorkspaces(userId.getValue(), pageable);
        List<Workspace> workspaces = entityPage.getContent().stream()
                .map(workspaceMapper::toDomain)
                .collect(Collectors.toList());

        return PageResponse.of(workspaces, pageRequest, entityPage.getTotalElements());
    }

    /*@Override
    public List<Workspace> findAllByMembership(UserId userId) {
        return List.of();
    }*/

    @Override
    public PageResponse<WorkspaceMember> findMembersByWorkspaceId(WorkspaceId workspaceId,PageRequest pageRequest) {
        Pageable pageable=paginationMapper.toSpringPageable(pageRequest);
        Page<WorkspaceMemberEntity> entityPage=workspaceJpaRepo.findByWorkspace_WorkspaceId(workspaceId.getValue(), pageable);
        List<WorkspaceMember> members=entityPage.getContent().stream()
                .map(workspaceMemberMapper::toDomain)
                .collect(Collectors.toList());
        return PageResponse.of(members, pageRequest, entityPage.getTotalElements());
    }


}
