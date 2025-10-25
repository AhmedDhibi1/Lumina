package com.snapshot.lumina.businesslogic.application.workspaces.createworkspace;

import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.CreateWorkspaceCommand;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.mapper.WorkspaceMapper;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.Description;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateWorkspaceUseCaseImpl implements CreateWorkspaceUseCase {
    private final WorkspaceRepository workspaceRepository;

    public CreateWorkspaceUseCaseImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional
    public WorkspaceResponseDto execute(CreateWorkspaceCommand command) {
        // 1. Validate input
        validateCommand(command);

        // 2. Create value objects
        WorkspaceName workspaceName = new WorkspaceName(command.getWorkspaceName());
        UserId creatorId = UserId.builder()
                .value(command.getCreatorUserId())
                .build();
        Description description = command.getDescription() != null ?
                new Description(command.getDescription()) : null;

        // 3. Check business rules (optional: check if workspace name exists for user)
        if (workspaceRepository.existsByNameAndCreator(
                command.getWorkspaceName(), creatorId)) {
            throw new IllegalArgumentException(
                    "You already have a workspace with this name");
        }

        // 4. Create domain aggregate
        Workspace workspace = Workspace.create(
                workspaceName,
                creatorId,
                description
        );

        // 5. Persist
        Workspace savedWorkspace = workspaceRepository.save(workspace);

        // 6. Map to DTO and return
        return WorkspaceMapper.toResponseDto(savedWorkspace);
    }


    private void validateCommand(CreateWorkspaceCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (command.getWorkspaceName() == null ||
                command.getWorkspaceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name is required");
        }
        if (command.getCreatorUserId() == null) {
            throw new IllegalArgumentException("Creator user ID is required");
        }
    }
}
