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
    Optional<Workspace> save(Workspace workspace);

    Optional<Workspace> findById(WorkspaceId workspaceId);

    boolean existsById(WorkspaceId workspaceId);

    boolean existsByName(WorkspaceName workspaceName);

    void delete(WorkspaceId workspaceId);

    // Query methods for workspaces
    PageResponse<Workspace> findAllByCreator(UserId creatorId, PageRequest pageRequest);

    PageResponse<Workspace> findAllByMembership(UserId userId,PageRequest pageRequest);

    //PageResponse<Workspace> findAllByMembership(UserId userId,PageRequest pageRequest);

    // Query methods for members within workspace context
    PageResponse<WorkspaceMember> findMembersByWorkspaceId(WorkspaceId workspaceId,PageRequest pageRequest);

}
