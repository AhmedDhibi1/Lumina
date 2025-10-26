package com.snapshot.lumina.businesslogic.application.workspaces.addmember.mapper;

import com.snapshot.lumina.businesslogic.application.workspaces.addmember.dto.WorkspaceMemberDto;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMemberMapper {
    public static WorkspaceMemberDto toMemberDto(WorkspaceMember member){
        return WorkspaceMemberDto.builder()
                .membershipId(member.getMembershipId().getValue())
                .workspaceId(member.getWorkspaceId().getValue())
                .userId(member.getUserId().getValue())
                .role(member.getRole().toString())
                .joinedAt(member.getJoinedAt().getValue())
                .build();
    };
}
