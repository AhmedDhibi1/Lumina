package com.snapshot.lumina.businesslogic.infrastructure.persistence.entity;


import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MembershipId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "workspace_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_user",
                        columnNames = {"workspace_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_member_user_id", columnList = "user_id"),
                @Index(name = "idx_member_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_member_role", columnList = "role"),
                @Index(name = "idx_member_joined_at", columnList = "joined_at"),
                @Index(name = "idx_member_workspace_role", columnList = "workspace_id, role"),
                @Index(name = "idx_member_user_workspace", columnList = "user_id, workspace_id")
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberEntity {

    @Id
    @NotNull(message = "Membership ID is required")
    private UUID membershipId;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private UUID userId;

    @Column(name = "joined_at", nullable = false, updatable = false)
    @NotNull(message = "Joined at timestamp is required")
    private LocalDateTime joinedAt;

    @Column(name = "role", nullable = false)
    @NotNull(message = "Role is required")
    @NotEmpty(message = "Role cannot be empty")
    @Pattern(regexp = "^(OWNER|ADMIN|MEMBER|VIEWER)$", message = "Role must be one of: OWNER, ADMIN, MEMBER, VIEWER")
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @NotNull(message = "Workspace is required")
    private WorkspaceEntity workspace;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceMemberEntity)) return false;
        WorkspaceMemberEntity other = (WorkspaceMemberEntity) o;
        return membershipId != null && membershipId.equals(other.membershipId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}