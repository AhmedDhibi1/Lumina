package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetWorkspacesByCreatorQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.PageResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceResponseDto;

public interface GetWorkspacesByCreatorUseCase {
    public PageResponseDto<WorkspaceResponseDto> execute(GetWorkspacesByCreatorQuery query);
}
