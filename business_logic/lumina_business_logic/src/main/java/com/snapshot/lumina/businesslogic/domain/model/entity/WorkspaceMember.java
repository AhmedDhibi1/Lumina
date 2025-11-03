package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MembershipId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceRole;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMember {
    private MembershipId membershipId;
    private WorkspaceId workspaceId;
    private UserId userId;
    private WorkspaceRole role;
    private Timestamp joinedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceMember that)) return false;
        return Objects.equals(membershipId, that.membershipId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(membershipId);  // ✅ Null-safe
    }
}
