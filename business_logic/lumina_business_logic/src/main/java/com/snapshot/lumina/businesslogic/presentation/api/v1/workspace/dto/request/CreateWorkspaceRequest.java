package com.snapshot.lumina.businesslogic.presentation.api.v1.workspace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new workspace")
public class CreateWorkspaceRequest {

    @NotNull(message = "Workspace name is required")
    @Size(min = 8, max = 16, message = "Workspace name must be between 8 and 16 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9]+$",
            message = "Workspace name must contain only alphanumeric characters"
    )
    @Schema(
            description = "Unique workspace name (alphanumeric only)",
            example = "MyWorkspace1",
            minLength = 8,
            maxLength = 16
    )
    private String workspaceName;

    @NotNull(message = "Description is required")
    @Size(min = 8, max = 150, message = "Description must be between 8 and 150 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 ]+$",
            message = "Description must contain only alphanumeric characters and spaces"
    )
    @Schema(
            description = "Workspace description",
            example = "This is my team workspace for project management",
            minLength = 8,
            maxLength = 150
    )
    private String description;
}
