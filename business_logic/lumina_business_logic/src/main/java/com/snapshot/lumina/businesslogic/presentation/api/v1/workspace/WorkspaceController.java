package com.snapshot.lumina.businesslogic.presentation.api.v1.workspace;

import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.CreateWorkspaceUseCase;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.CreateWorkspaceCommand;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.infrastructure.config.security.KeycloakUserPrincipal;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.request.CreateWorkspaceRequest;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.response.ApiResponse;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.response.WorkspaceResponse;
import com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.mapper.WorkspaceApiMapper;
import com.snapshot.lumina.businesslogic.presentation.config.Versioning;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for workspace management
 *
 * Uses Spring Security's @AuthenticationPrincipal to access authenticated user info
 * No need to manually extract JWT from cookies - handled by JwtAuthenticationFilter
 */
@RestController
@RequestMapping(Versioning.API_VERSION + "workspaces")
@Tag(name = "Workspaces", description = "Workspace management APIs")
@SecurityRequirement(name = "cookieAuth")
@Slf4j
@RequiredArgsConstructor
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final WorkspaceApiMapper mapper;

    /**
     * Create a new workspace for the authenticated user
     *
     * The @AuthenticationPrincipal annotation automatically injects the authenticated user
     * from Spring Security's SecurityContext. This is populated by JwtAuthenticationFilter.
     *
     * @param request Workspace creation request
     * @param principal Authenticated user (injected by Spring Security)
     * @return Created workspace response
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new workspace",
            description = "Creates a new workspace for the authenticated user. " +
                    "Authentication is required via JWT cookie."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Workspace created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing authentication"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Workspace with this name already exists"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @AuthenticationPrincipal KeycloakUserPrincipal principal) {

        // Extract user ID from authenticated principal
        UUID authenticatedUserId = UUID.fromString(principal.getUserId());

        log.info("Creating workspace: name='{}', userId='{}', username='{}'",
                request.getWorkspaceName(),
                authenticatedUserId,
                principal.getUsername());

        // Map request to command (presentation → application layer)
        CreateWorkspaceCommand command = mapper.toCommand(request, authenticatedUserId);

        // Execute use case
        WorkspaceResponseDto responseDto = createWorkspaceUseCase.execute(command);

        // Map to presentation response
        WorkspaceResponse response = mapper.toResponse(responseDto);

        log.info("Workspace created successfully: workspaceId='{}', workspaceName='{}'",
                response.getWorkspaceId(),
                request.getWorkspaceName());

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
