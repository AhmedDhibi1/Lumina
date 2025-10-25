package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetWorkspaceQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceDetailDto;

public interface GetWorkspaceUseCase {
    public WorkspaceDetailDto execute(GetWorkspaceQuery query);
}
