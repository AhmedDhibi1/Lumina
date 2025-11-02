package com.snapshot.lumina.businesslogic.domain.event.workspace;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.event.Event;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceEvent extends Event {
    private WorkspaceId workspaceId;
    private UserId ownerId;
}
