package com.snapshot.lumina.businesslogic.domain.repository;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;

import java.util.Optional;
import java.util.List;

public interface WorkspaceRepository {

    // Core CRUD operations
    Workspace save(Workspace workspace);

    Optional<Workspace> findById(WorkspaceId workspaceId);

    boolean existsById(WorkspaceId workspaceId);

    boolean existsByName(WorkspaceName workspaceName);

    void delete(WorkspaceId workspaceId);

    // Query methods for workspaces
    PageResponse<Workspace> findAllByCreator(UserId creatorId, PageRequest pageRequest);

    PageResponse<Workspace> findAllByMembership(UserId userId,PageRequest pageRequest);

    List<Workspace> findAllByMembership(UserId userId);

    // Query methods for members within workspace context
    List<WorkspaceMember> findMembersByWorkspaceId(WorkspaceId workspaceId);

    Optional<WorkspaceMember> findMemberByWorkspaceAndUser(WorkspaceId workspaceId, UserId userId);

    boolean isMemberOfWorkspace(WorkspaceId workspaceId, UserId userId);
    boolean isAdminOfWorkspace(WorkspaceId workspaceId, UserId userId);

    // Specific queries
    long countMembersByWorkspace(WorkspaceId workspaceId);

    long countAdminsByWorkspace(WorkspaceId workspaceId);

    boolean existsByNameAndCreator(String workspaceName, UserId creatorId);
}
