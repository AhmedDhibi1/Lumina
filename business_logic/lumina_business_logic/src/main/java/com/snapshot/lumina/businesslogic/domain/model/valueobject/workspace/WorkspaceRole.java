package com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkspaceRole {
    private String role; // ADMIN, MEMBER, VIEWER
}