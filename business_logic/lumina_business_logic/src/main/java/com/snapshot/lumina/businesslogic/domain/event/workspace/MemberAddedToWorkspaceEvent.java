package com.snapshot.lumina.businesslogic.domain.event.workspace;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberAddedToWorkspaceEvent extends WorkspaceEvent {
    private UserId memberId;
    private Timestamp addedAt;
}
