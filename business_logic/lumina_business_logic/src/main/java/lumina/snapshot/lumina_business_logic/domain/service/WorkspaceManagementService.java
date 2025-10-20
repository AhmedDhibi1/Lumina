package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.aggregate.Workspace;
import lumina.snapshot.lumina_business_logic.domain.model.entity.WorkspaceMember;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.workspace.Description;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.workspace.WorkspaceName;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.workspace.WorkspaceRole;

import java.util.List;

public interface WorkspaceManagementService {

    // Create new workspace
    Workspace createWorkspace(WorkspaceName name, Description description, UserId createdBy);

    // Add member to workspace
    WorkspaceMember addMember(WorkspaceId workspaceId, UserId userId, WorkspaceRole role, UserId addedBy);

    // Remove member from workspace
    void removeMember(WorkspaceId workspaceId, UserId userId, UserId removedBy);

    // Update member role
    WorkspaceMember updateMemberRole(WorkspaceId workspaceId, UserId userId, WorkspaceRole newRole, UserId updatedBy);

    // Check if user is workspace admin
    Boolean isWorkspaceAdmin(WorkspaceId workspaceId, UserId userId);

    // Check if user is workspace member
    Boolean isWorkspaceMember(WorkspaceId workspaceId, UserId userId);

    // Get all workspace members
    List<WorkspaceMember> getWorkspaceMembers(WorkspaceId workspaceId);

    // Get all workspaces for a user
    List<Workspace> getUserWorkspaces(UserId userId);

    // Validate workspace access
    void validateWorkspaceAccess(WorkspaceId workspaceId, UserId userId);
}
