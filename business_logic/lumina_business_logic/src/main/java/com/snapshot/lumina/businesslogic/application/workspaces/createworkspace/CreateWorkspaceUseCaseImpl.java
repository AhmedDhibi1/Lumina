package com.snapshot.lumina.businesslogic.application.workspaces.createworkspace;

import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.CreateWorkspaceCommand;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.dto.WorkspaceResponseDto;
import com.snapshot.lumina.businesslogic.application.workspaces.createworkspace.mapper.WorkspaceMapper;
import com.snapshot.lumina.businesslogic.domain.event.eventpublisher.DomainEventPublisher;
import com.snapshot.lumina.businesslogic.domain.event.workspace.WorkspaceCreatedEvent;
import com.snapshot.lumina.businesslogic.domain.exceptions.workspace.DuplicateWorkspaceException;
import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.Description;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class CreateWorkspaceUseCaseImpl implements CreateWorkspaceUseCase {
    private final WorkspaceRepository workspaceRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateWorkspaceUseCaseImpl(WorkspaceRepository workspaceRepository, DomainEventPublisher eventPublisher) {
        this.workspaceRepository = workspaceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public WorkspaceResponseDto execute(CreateWorkspaceCommand command) {
        log.info("Creating workspace: name={}, creator={}",
                command.getWorkspaceName().getValue(),
                command.getCreatorUserId().getValue());

        // 1. Validate input
        validateCommand(command);



        // 3. Check business rules (optional: check if workspace name exists for user)
        if (workspaceRepository.existsByNameAndCreator(
                command.getWorkspaceName(),
                command.getCreatorUserId())) {
            throw new DuplicateWorkspaceException(
                    command.getWorkspaceName(),
                    command.getCreatorUserId());
        }


        // 4. Create domain aggregate
        Workspace workspace = Workspace.create(
                command.getWorkspaceName(),
                command.getCreatorUserId(),
                command.getDescription()
        );

        // 5. Persist
        Workspace savedWorkspace = workspaceRepository.save(workspace).orElseThrow(
                ()-> new RuntimeException("Failed to save workspace")
        );

        // Publish domain event to Kafka
        WorkspaceCreatedEvent event = WorkspaceCreatedEvent.builder()
                .workspaceId(savedWorkspace.getWorkspaceId())
                .workspaceName(savedWorkspace.getWorkspaceName())
                .createdBy(savedWorkspace.getCreatedBy())
                .createdAt(Timestamp.now())
                .build();

        eventPublisher.publish(event);

        log.info("Workspace created and event published: {}",
                savedWorkspace.getWorkspaceId().getValue());

        // 6. Map to DTO and return
        return WorkspaceMapper.toResponseDto(savedWorkspace);
    }


    // No validation needed if command uses value objects!
    private void validateCommand(CreateWorkspaceCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        // That's it! Value objects are already validated
    }
}
