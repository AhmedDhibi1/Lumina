package com.snapshot.lumina.businesslogic.domain.event.workspace;

import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WorkspaceCreatedEvent extends WorkspaceEvent {
    private Timestamp createdAt;
    private WorkspaceName workspaceName;
    private UserId createdBy;
}
