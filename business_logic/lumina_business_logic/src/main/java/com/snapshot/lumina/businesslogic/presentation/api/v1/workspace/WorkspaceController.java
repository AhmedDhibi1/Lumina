package com.snapshot.lumina.businesslogic.presentation.api.v1.workspace;

import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.CreateWorkspaceUseCase;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.CreateWorkspaceCommand;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.request.CreateWorkspaceRequest;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.response.ApiResponse;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.response.WorkspaceResponse;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.mapper.WorkspaceApiMapper;
import com.snapshot.lumina.businesslogic.presentation.auth.AuthenticationService;
import com.snapshot.lumina.businesslogic.presentation.config.Versioning;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Versioning.API_VERSION + "workspaces")
@Tag(name = "Workspaces", description = "Workspace management APIs")
@Slf4j
@RequiredArgsConstructor
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final WorkspaceApiMapper mapper;
    private final AuthenticationService authenticationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new workspace",
            description = "Creates a new workspace for the specified user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Workspace created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Workspace already exists"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            HttpServletRequest httpRequest) {

        // Extract authenticated user ID from JWT token in cookies
        UUID authenticatedUserId = authenticationService.extractAuthenticatedUserId(httpRequest);

        log.info("Received create workspace request: workspaceName={}, authenticatedUserId={}",
                request.getWorkspaceName(),
                authenticatedUserId);

        // Map request to command (presentation → application layer)
        CreateWorkspaceCommand command = mapper.toCommand(request, authenticatedUserId);

        // Execute use case
        WorkspaceResponseDto responseDto = createWorkspaceUseCase.execute(command);

        // Map to presentation response
        WorkspaceResponse response = mapper.toResponse(responseDto);

        log.info("Workspace created successfully: workspaceId={}", response.getWorkspaceId());

        // Wrap in API response envelope
        ApiResponse<WorkspaceResponse> apiResponse = ApiResponse.success(
                "Workspace created successfully",
                response
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
}
