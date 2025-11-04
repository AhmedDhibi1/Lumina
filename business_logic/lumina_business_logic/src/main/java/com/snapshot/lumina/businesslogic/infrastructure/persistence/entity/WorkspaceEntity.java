package com.snapshot.lumina.businesslogic.infrastructure.persistence.entity;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.Description;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "workspaces",
        indexes = {
                @Index(name = "idx_workspace_name", columnList = "workspace_name", unique = true),
                @Index(name = "idx_workspace_created_by", columnList = "created_by"),
                @Index(name = "idx_workspace_created_at", columnList = "created_at"),
                @Index(name = "idx_workspace_name_created_by", columnList = "workspace_name, created_by")
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceEntity {
    @Id
    private UUID workspaceId;

    @Column(name = "workspace_name", nullable = false, unique = true)
    @NotNull(message = "Workspace name is required")
    @Size(min = 8, max = 16, message = "Workspace name must be between 8 and 16 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Workspace name must contain only alphanumeric characters")
    private String workspaceName;

    @Column(name = "created_by", nullable = false)
    @NotNull(message = "Created by is required")
    private UUID createdBy;

    @Column(name = "description", length = 500)
    @NotNull(message = "Description is required")
    @Size(min = 8, max = 150, message = "Description must be between 8 and 150 characters")
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Description must contain only alphanumeric characters and spaces")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkspaceMemberEntity> members = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceEntity)) return false;
        WorkspaceEntity other = (WorkspaceEntity) o;
        return workspaceId != null && workspaceId.equals(other.workspaceId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}