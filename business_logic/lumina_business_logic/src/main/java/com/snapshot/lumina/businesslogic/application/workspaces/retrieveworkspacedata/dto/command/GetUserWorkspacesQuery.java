package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserWorkspacesQuery {
    private UUID userId;
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortOrder;
}