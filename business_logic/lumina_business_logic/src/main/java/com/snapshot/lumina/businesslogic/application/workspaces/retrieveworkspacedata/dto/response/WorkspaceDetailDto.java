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
public class WorkspaceDetailDto {
    private UUID workspaceId;
    private String workspaceName;
    private String description;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer memberCount;
    private Integer adminCount;
    private Boolean isAdmin;
    private String currentUserRole;
}
