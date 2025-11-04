package com.snapshot.lumina.businesslogic.infrastructure.persistence.adapter;

import com.snapshot.lumina.businesslogic.domain.model.aggregate.Workspace;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageRequest;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.PageResponse;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.domain.repository.WorkspaceRepository;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceEntity;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.exceptions.WorkspacePersistenceException;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper.PaginationMapper;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper.WorkspaceEntityMapper;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.mapper.WorkspaceMemberEntityMapper;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.repository.WorkspaceJpaRepo;
import com.snapshot.lumina.businesslogic.infrastructure.persistence.validation.WorkspaceValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepo workspaceJpaRepo;
    private final WorkspaceEntityMapper workspaceMapper;
    private final PaginationMapper paginationMapper;
    private final WorkspaceMemberEntityMapper workspaceMemberMapper;
    private final WorkspaceValidator workspaceValidator;

    public WorkspaceRepositoryAdapter(WorkspaceJpaRepo workspaceJpaRepo, WorkspaceEntityMapper workspaceMapper, PaginationMapper paginationMapper, WorkspaceMemberEntityMapper workspaceMemberMapper, WorkspaceValidator workspaceValidator) {
        this.workspaceJpaRepo = workspaceJpaRepo;
        this.workspaceMapper = workspaceMapper;
        this.paginationMapper = paginationMapper;
        this.workspaceMemberMapper = workspaceMemberMapper;
        this.workspaceValidator = workspaceValidator;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "workspaces", key = "#workspace.workspaceId.value"),
            @CacheEvict(value = "workspacesByUser", allEntries = true),
            @CacheEvict(value = "workspaceExists", key = "#workspace.workspaceId.value")
    })
    public Optional<Workspace> save(@NotNull @Valid Workspace workspace) {
        // Input validation
        workspaceValidator.validateWorkspaceInput(workspace);

        try {
            log.debug("Saving workspace: {}", workspace.getWorkspaceId().getValue());

            // Map to entity
            WorkspaceEntity entity = workspaceMapper.toEntityWithMembers(workspace);

            // Validate entity before persisting
            workspaceValidator.validateEntity(entity);

            // Save entity
            WorkspaceEntity savedEntity = workspaceJpaRepo.save(entity);

            // Map back to domain
            Workspace savedWorkspace = workspaceMapper.toDomain(savedEntity);

            log.info("Successfully saved workspace: {}", savedWorkspace.getWorkspaceId().getValue());
            return Optional.of(savedWorkspace);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving workspace: {}",
                    workspace.getWorkspaceId().getValue(), e);

            String errorMessage = workspaceValidator.extractConstraintViolationMessage(e);
            throw new WorkspacePersistenceException(
                    "Failed to save workspace: " + errorMessage, e);

        } catch (ConstraintViolationException e) {
            log.error("Validation constraint violation for workspace: {}",
                    workspace.getWorkspaceId().getValue(), e);

            String violations = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Unknown validation error");

            throw new WorkspacePersistenceException(
                    "Workspace validation failed: " + violations, e);

        } catch (DataAccessException e) {
            log.error("Database access error while saving workspace: {}",
                    workspace.getWorkspaceId().getValue(), e);
            throw new WorkspacePersistenceException(
                    "Database error while saving workspace", e);

        } catch (Exception e) {
            log.error("Unexpected error while saving workspace: {}",
                    workspace.getWorkspaceId().getValue(), e);
            throw new WorkspacePersistenceException(
                    "Unexpected error saving workspace: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "workspaces", key = "#workspaceId.value", unless = "#result.isEmpty()")
    public Optional<Workspace> findById(@NotNull WorkspaceId workspaceId) {
        workspaceValidator.validateWorkspaceId(workspaceId);

        try {
            log.debug("Finding workspace by ID: {}", workspaceId.getValue());

            return workspaceJpaRepo.findById(workspaceId.getValue())
                    .map(entity -> {
                        Workspace workspace = workspaceMapper.toDomain(entity);
                        log.debug("Found workspace: {}", workspaceId.getValue());
                        return workspace;
                    });

        } catch (DataAccessException e) {
            log.error("Database error while finding workspace: {}", workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Database error while retrieving workspace", e);

        } catch (Exception e) {
            log.error("Unexpected error while finding workspace: {}", workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Unexpected error retrieving workspace: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "workspaceExists", key = "#workspaceId.value")
    public boolean existsById(@NotNull WorkspaceId workspaceId) {
        workspaceValidator.validateWorkspaceId(workspaceId);

        try {
            log.debug("Checking existence of workspace: {}", workspaceId.getValue());
            return workspaceJpaRepo.existsById(workspaceId.getValue());

        } catch (DataAccessException e) {
            log.error("Database error while checking workspace existence: {}",
                    workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Database error while checking workspace existence", e);

        } catch (Exception e) {
            log.error("Unexpected error while checking workspace existence: {}",
                    workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Unexpected error checking workspace existence: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "workspaceExistsByName", key = "#workspaceName.value")
    public boolean existsByName(@NotNull WorkspaceName workspaceName) {
        workspaceValidator.validateWorkspaceName(workspaceName);

        try {
            log.debug("Checking existence of workspace by name: {}", workspaceName.getValue());
            return workspaceJpaRepo.existsByWorkspaceName(workspaceName.getValue());

        } catch (DataAccessException e) {
            log.error("Database error while checking workspace name existence: {}",
                    workspaceName.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Database error while checking workspace name existence", e);

        } catch (Exception e) {
            log.error("Unexpected error while checking workspace name existence: {}",
                    workspaceName.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Unexpected error checking workspace name existence: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "workspaces", key = "#workspaceId.value"),
            @CacheEvict(value = "workspacesByUser", allEntries = true),
            @CacheEvict(value = "workspaceExists", key = "#workspaceId.value"),
            @CacheEvict(value = "workspaceExistsByName", allEntries = true)
    })
    public void delete(@NotNull WorkspaceId workspaceId) {
        workspaceValidator.validateWorkspaceId(workspaceId);

        try {
            log.debug("Deleting workspace: {}", workspaceId.getValue());

            if (!workspaceJpaRepo.existsById(workspaceId.getValue())) {
                log.warn("Attempted to delete non-existent workspace: {}", workspaceId.getValue());
                throw new WorkspacePersistenceException(
                        "Cannot delete workspace: workspace not found with ID " + workspaceId.getValue());
            }

            workspaceJpaRepo.deleteById(workspaceId.getValue());
            log.info("Successfully deleted workspace: {}", workspaceId.getValue());

        } catch (EmptyResultDataAccessException e) {
            log.error("Workspace not found for deletion: {}", workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Workspace not found with ID: " + workspaceId.getValue(), e);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while deleting workspace: {}",
                    workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Cannot delete workspace due to existing references", e);

        } catch (DataAccessException e) {
            log.error("Database error while deleting workspace: {}", workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Database error while deleting workspace", e);

        } catch (Exception e) {
            log.error("Unexpected error while deleting workspace: {}", workspaceId.getValue(), e);
            throw new WorkspacePersistenceException(
                    "Unexpected error deleting workspace: " + e.getMessage(), e);
        }
    }

    @Override
    public PageResponse<Workspace> findAllByCreator(UserId creatorId, PageRequest pageRequest) {
        Pageable pageable = paginationMapper.toSpringPageable(pageRequest);
        Page<WorkspaceEntity> entityPage = workspaceJpaRepo.findAllByCreatorId(creatorId.getValue(), pageable);
        List<Workspace> workspaces = entityPage.getContent().stream()
                .map(workspaceMapper::toDomain)
                .collect(Collectors.toList());

        return PageResponse.of(workspaces, pageRequest, entityPage.getTotalElements());
    }

    @Override
    public PageResponse<Workspace> findAllByMembership(UserId userId, PageRequest pageRequest) {
        Pageable pageable=paginationMapper.toSpringPageable(pageRequest);
        Page<WorkspaceEntity> entityPage= workspaceJpaRepo.findAllUserMembershipWorkspaces(userId.getValue(), pageable);
        List<Workspace> workspaces = entityPage.getContent().stream()
                .map(workspaceMapper::toDomain)
                .collect(Collectors.toList());

        return PageResponse.of(workspaces, pageRequest, entityPage.getTotalElements());
    }

    /*@Override
    public List<Workspace> findAllByMembership(UserId userId) {
        return List.of();
    }*/

    @Override
    public PageResponse<WorkspaceMember> findMembersByWorkspaceId(WorkspaceId workspaceId,PageRequest pageRequest) {
        Pageable pageable=paginationMapper.toSpringPageable(pageRequest);
        Page<WorkspaceMemberEntity> entityPage=workspaceJpaRepo.findByWorkspace_WorkspaceId(workspaceId.getValue(), pageable);
        List<WorkspaceMember> members=entityPage.getContent().stream()
                .map(workspaceMemberMapper::toDomain)
                .collect(Collectors.toList());
        return PageResponse.of(members, pageRequest, entityPage.getTotalElements());
    }


}
