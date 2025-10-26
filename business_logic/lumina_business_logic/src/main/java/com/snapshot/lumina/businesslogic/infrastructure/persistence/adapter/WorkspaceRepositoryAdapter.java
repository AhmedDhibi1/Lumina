package com.snapshot.lumina.businesslogic.infrastructure.persistence.adapter;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {
    @Override
    public Workspace save(Workspace workspace) {
        return null;
    }

    @Override
    public Optional<Workspace> findById(WorkspaceId workspaceId) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(WorkspaceId workspaceId) {
        return false;
    }

    @Override
    public boolean existsByName(WorkspaceName workspaceName) {
        return false;
    }

    @Override
    public void delete(WorkspaceId workspaceId) {

    }

    @Override
    public PageResponse<Workspace> findAllByCreator(UserId creatorId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public PageResponse<Workspace> findAllByMembership(UserId userId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public List<Workspace> findAllByMembership(UserId userId) {
        return List.of();
    }

    @Override
    public List<WorkspaceMember> findMembersByWorkspaceId(WorkspaceId workspaceId) {
        return List.of();
    }

    @Override
    public Optional<WorkspaceMember> findMemberByWorkspaceAndUser(WorkspaceId workspaceId, UserId userId) {
        return Optional.empty();
    }

    @Override
    public boolean isMemberOfWorkspace(WorkspaceId workspaceId, UserId userId) {
        return false;
    }

    @Override
    public boolean isAdminOfWorkspace(WorkspaceId workspaceId, UserId userId) {
        return false;
    }

    @Override
    public long countMembersByWorkspace(WorkspaceId workspaceId) {
        return 0;
    }

    @Override
    public long countAdminsByWorkspace(WorkspaceId workspaceId) {
        return 0;
    }

    @Override
    public boolean existsByNameAndCreator(String workspaceName, UserId creatorId) {
        return false;
    }
}
