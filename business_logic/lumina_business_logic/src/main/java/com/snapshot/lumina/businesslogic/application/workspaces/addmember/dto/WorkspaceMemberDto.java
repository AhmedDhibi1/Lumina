package com.snapshot.lumina.businesslogic.application.workspaces.addmember.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberDto {
    private UUID membershipId;
    private UUID workspaceId;
    private UUID userId;
    private String role;
    private LocalDateTime joinedAt;
}