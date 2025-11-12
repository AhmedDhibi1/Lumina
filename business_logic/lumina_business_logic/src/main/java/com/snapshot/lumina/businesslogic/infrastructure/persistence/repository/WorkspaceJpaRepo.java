package com.snapshot.lumina.businesslogic.infrastructure.persistence.repository;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceEntity;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceJpaRepo extends JpaRepository<WorkspaceEntity, UUID> {

    String ROLE_OWNER = "OWNER";

    @Query("""
            SELECT DISTINCT w FROM WorkspaceEntity w
            LEFT JOIN FETCH w.members m
            WHERE m.userId = :userId
            AND m.role = 'OWNER'
            """)
    @Transactional(readOnly = true)
    Page<WorkspaceEntity> findAllByCreatorId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT DISTINCT w FROM WorkspaceEntity w
            LEFT JOIN FETCH w.members m
            WHERE m.userId = :userId
            AND m.role <> 'OWNER'
            """)
    @Transactional(readOnly = true)
    Page<WorkspaceEntity> findAllUserMembershipWorkspaces(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT w FROM WorkspaceEntity w LEFT JOIN FETCH w.members WHERE w.workspaceId = :id")
    Optional<WorkspaceEntity> findByIdWithMembers(@Param("id") UUID id);

    boolean existsByWorkspaceName(String workspaceName);

    Page<WorkspaceMemberEntity> findByWorkspace_WorkspaceId(UUID workspaceId, Pageable pageable);

    boolean existsByWorkspaceNameAndCreatedBy(String value, UUID value1);
}
