package com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata;

import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.command.GetUserWorkspacesQuery;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.PageResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.retrieveworkspacedata.dto.response.WorkspaceResponseDto;

public interface GetUserWorkspacesUseCase {
    public PageResponseDto<WorkspaceResponseDto> execute(GetUserWorkspacesQuery query);
}
