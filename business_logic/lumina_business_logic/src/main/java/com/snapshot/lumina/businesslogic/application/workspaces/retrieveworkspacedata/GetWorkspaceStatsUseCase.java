package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetWorkspaceStatsQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceStatsDto;

public interface GetWorkspaceStatsUseCase {
    public WorkspaceStatsDto execute(GetWorkspaceStatsQuery query);
}
