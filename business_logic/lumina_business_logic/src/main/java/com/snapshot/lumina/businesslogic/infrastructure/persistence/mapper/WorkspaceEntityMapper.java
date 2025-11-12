package com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.Description;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceEntity;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceMemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WorkspaceEntityMapper {
    private final WorkspaceMemberEntityMapper workspaceMemberMapper;

    public WorkspaceEntityMapper(WorkspaceMemberEntityMapper workspaceMemberMapper) {
        this.workspaceMemberMapper = workspaceMemberMapper;
    }

    public Workspace toDomain(WorkspaceEntity workspaceEntity){
        if(workspaceEntity == null){
            log.error("Workspace entity is null");
            throw new IllegalArgumentException("Workspace entity is null");
        }
        if(workspaceEntity.getWorkspaceId() == null){
            log.error("Workspace id is null");
            throw new IllegalArgumentException("Workspace id is null");
        }
        if (workspaceEntity.getCreatedBy() == null) {
            log.error("Workspace created by is null");
            throw new IllegalArgumentException("Workspace created by is null");
        }
        WorkspaceId workspaceId = new WorkspaceId(workspaceEntity.getWorkspaceId());
        WorkspaceName workspaceName = WorkspaceName.of(workspaceEntity.getWorkspaceName());
        UserId userId = new UserId(workspaceEntity.getCreatedBy());
        Description description = new Description(workspaceEntity.getDescription());
        Timestamp createdAt = new Timestamp(workspaceEntity.getCreatedAt());
        Timestamp updatedAt = new Timestamp(workspaceEntity.getUpdatedAt());
        return Workspace.builder()
                .workspaceId(workspaceId)
                .workspaceName(workspaceName)
                .createdBy(userId)
                .description(description)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public WorkspaceEntity toEntity(Workspace workspace){
        if(workspace == null){
            log.error("Workspace is null");
            throw new IllegalArgumentException("Workspace is null");
        }
        if (workspace.getWorkspaceId() == null){
            log.error("Workspace entity id is null");
            throw new IllegalArgumentException("Workspace entity id is null");
        }
        //WorkspaceMemberEntity memberEntity = workspaceMemberMapper.toEntity();
        return WorkspaceEntity.builder()
                .workspaceId(workspace.getWorkspaceId().getValue())
                .workspaceName(workspace.getWorkspaceName().getValue())
                .description(workspace.getDescription().getValue())
                .createdBy(workspace.getCreatedBy().getValue())
                .createdAt(workspace.getCreatedAt().getValue())
                .updatedAt(workspace.getUpdatedAt().getValue())
                .build();
    }

    public Workspace toDomainWithMembers(WorkspaceEntity workspaceEntity){
        Workspace workspace= this.toDomain(workspaceEntity);

        List<WorkspaceMember> members = workspaceEntity.getMembers().stream()
                .map(workspaceMemberMapper::toDomain)
                .collect(Collectors.toList());
        return Workspace.builder()
                .workspaceId(new WorkspaceId(workspaceEntity.getWorkspaceId()))
                .workspaceName(WorkspaceName.of(workspaceEntity.getWorkspaceName()))
                .createdBy(new UserId(workspaceEntity.getCreatedBy()))
                .description(new Description(workspaceEntity.getDescription()))
                .createdAt(new Timestamp(workspaceEntity.getCreatedAt()))
                .updatedAt(new Timestamp(workspaceEntity.getUpdatedAt()))
                .members(members)  // ✅ Set during construction
                .build();
    };

    public WorkspaceEntity toEntityWithMembers(Workspace workspace){
        WorkspaceEntity workspaceEntity = this.toEntity(workspace);

        List<WorkspaceMemberEntity> memberEntities = workspace.getMembers().stream()
                .map(workspaceMemberMapper::toEntity)
                .peek(member -> member.setWorkspace(workspaceEntity))  // ✅ Set both sides
                .collect(Collectors.toList());

        workspaceEntity.setMembers(memberEntities);
        return workspaceEntity;
    }
}
