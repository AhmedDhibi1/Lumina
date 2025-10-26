package com.snapshot.lumina.businesslogic.infrastructure.persistence.entity;


import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MembershipId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspace_members", uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "user_id"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberEntity {

    @Id
    private UUID membershipId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    private String role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

}
