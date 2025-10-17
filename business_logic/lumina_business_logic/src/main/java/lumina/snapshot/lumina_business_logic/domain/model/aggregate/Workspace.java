package lumina.snapshot.lumina_business_logic.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.entity.WorkspaceMember;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.workspace.Description;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.workspace.WorkspaceName;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workspace {
    private WorkspaceId workspaceId;
    private WorkspaceName workspaceName;
    private UserId createdBy;
    private Description description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private List<WorkspaceMember> members;
    private List<Document> documents;
}
