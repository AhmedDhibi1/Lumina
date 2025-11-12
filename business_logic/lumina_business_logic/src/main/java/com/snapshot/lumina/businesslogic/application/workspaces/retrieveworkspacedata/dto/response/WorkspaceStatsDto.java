package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response;

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
public class WorkspaceStatsDto {
    private UUID workspaceId;
    private String workspaceName;
    private Long totalMembers;
    private Long totalAdmins;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
}
