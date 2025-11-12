package com.snapshot.lumina.businesslogic.infrastructure.persistence.validation;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;


import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class WorkspaceValidator {
    private final Validator validator;

    public WorkspaceValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateWorkspaceInput(Workspace workspace){
        Objects.requireNonNull(workspace, "Workspace cannot be null");
        Objects.requireNonNull(workspace.getWorkspaceId(), "Workspace ID cannot be null");
        Objects.requireNonNull(workspace.getWorkspaceName(), "Workspace name cannot be null");
        Objects.requireNonNull(workspace.getCreatedBy(), "Created by user ID cannot be null");
        Objects.requireNonNull(workspace.getDescription(), "Description cannot be null");

        if (workspace.getWorkspaceName().getValue() == null ||
                workspace.getWorkspaceName().getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }
    }
    public void  validateWorkspaceId(WorkspaceId workspaceId) {
        Objects.requireNonNull(workspaceId, "Workspace ID cannot be null");
        Objects.requireNonNull(workspaceId.getValue(), "Workspace ID value cannot be null");
    }

    public void validateWorkspaceName(WorkspaceName workspaceName) {
        Objects.requireNonNull(workspaceName, "Workspace name cannot be null");
        Objects.requireNonNull(workspaceName.getValue(), "Workspace name value cannot be null");

        if (workspaceName.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }
    }

    public void validateEntity(WorkspaceEntity entity) {
        Set<ConstraintViolation<WorkspaceEntity>> violations = validator.validate(entity);

        if (!violations.isEmpty()) {
            log.error("Entity validation failed with {} violations", violations.size());
            throw new ConstraintViolationException(violations);
        }
    }

    public String extractConstraintViolationMessage(DataIntegrityViolationException e) {
        Throwable cause = e.getRootCause();

        if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
            String constraintName = ((org.hibernate.exception.ConstraintViolationException) cause)
                    .getConstraintName();

            if (constraintName != null) {
                if (constraintName.contains("workspace_name")) {
                    return "Workspace name already exists";
                } else if (constraintName.contains("workspace_id")) {
                    return "Workspace ID already exists";
                }
            }
        }

        return "Data integrity constraint violation";
    }

}
