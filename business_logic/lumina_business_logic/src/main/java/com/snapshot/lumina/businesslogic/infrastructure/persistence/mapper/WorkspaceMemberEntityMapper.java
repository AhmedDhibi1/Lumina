package com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper;

import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MembershipId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceRole;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceMemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorkspaceMemberEntityMapper {
    public WorkspaceMemberEntity toEntity(WorkspaceMember workspaceMember) {
        if (workspaceMember == null) {
            log.error("WorkspaceMember is null");
            throw new IllegalArgumentException("WorkspaceMember is null");
        }
        return WorkspaceMemberEntity.builder()
                .membershipId(workspaceMember.getMembershipId().getValue())
                .role(workspaceMember.getRole().getRole())
                .userId(workspaceMember.getUserId().getValue())
                .joinedAt(workspaceMember.getJoinedAt().getValue())
                .build();
    }

    public WorkspaceMember toDomain(WorkspaceMemberEntity workspaceMemberEntity){
        if (workspaceMemberEntity == null) {
            log.error("WorkspaceMember Entity is null");
            throw new IllegalArgumentException("WorkspaceMember Entity is null");
        }
        MembershipId membershipId=new MembershipId(workspaceMemberEntity.getMembershipId());
        WorkspaceId workspaceId=new WorkspaceId(workspaceMemberEntity.getWorkspace().getWorkspaceId());
        UserId userId=new UserId(workspaceMemberEntity.getUserId());
        WorkspaceRole role=new WorkspaceRole(workspaceMemberEntity.getRole());
        Timestamp joinedAt= new Timestamp(workspaceMemberEntity.getJoinedAt());
        return WorkspaceMember.builder()
                .membershipId(membershipId)
                .workspaceId(workspaceId)
                .userId(userId)
                .role(role)
                .joinedAt(joinedAt)
                .build();
    };
}
