package com.snapshot.lumina.businesslogic.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.entity.WorkspaceMember;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.MembershipId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.WorkspaceId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.Description;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceName;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace.WorkspaceRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    //private List<Document> documents;

    public static Workspace create(
            WorkspaceName workspaceName,
            UserId creatorId,
            Description description
    ) {
        if (workspaceName == null || creatorId == null) {
            throw new IllegalArgumentException("Workspace name and creator are required");
        }

        LocalDateTime now = LocalDateTime.now();
        WorkspaceId workspaceId = WorkspaceId.builder()
                .value(UUID.randomUUID())
                .build();

        // Creator starts as OWNER
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .membershipId(MembershipId.builder()
                        .value(UUID.randomUUID())
                        .build())
                .workspaceId(workspaceId)
                .userId(creatorId)
                .role(new WorkspaceRole("OWNER"))
                .joinedAt(Timestamp.builder()
                        .value(now)
                        .build())
                .build();

        return Workspace.builder()
                .workspaceId(workspaceId)
                .workspaceName(workspaceName)
                .createdBy(creatorId)
                .description(description)
                .createdAt(Timestamp.builder().value(now).build())
                .updatedAt(Timestamp.builder().value(now).build())
                .members(new ArrayList<>(List.of(ownerMember)))
                .build();
    }

    // Update workspace details
    public void updateDetails(WorkspaceName newName, Description newDescription, UserId updatedBy) {
        validateManagementPermission(updatedBy);

        if (newName != null) {
            this.workspaceName = newName;
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    public WorkspaceMember addMember(UserId userId, WorkspaceRole role, UserId addedBy) {
        validateManagementPermission(addedBy);

        if (hasMember(userId)) {
            throw new IllegalArgumentException("User is already a member");
        }

        WorkspaceMember newMember = WorkspaceMember.builder()
                .membershipId(MembershipId.builder().value(UUID.randomUUID()).build())
                .workspaceId(this.workspaceId)
                .userId(userId)
                .role(role)
                .joinedAt(Timestamp.builder().value(LocalDateTime.now()).build())
                .build();

        this.members.add(newMember);
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();

        return newMember;
    }

    public void removeMember(UserId userId, UserId removedBy) {
        validateManagementPermission(removedBy);

        if (userId.equals(removedBy) && isLastAdminOrOwner(userId)) {
            throw new IllegalStateException("Cannot remove the last admin/owner");
        }

        this.members.removeIf(m -> m.getUserId().equals(userId));
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    public void updateMemberRole(UserId userId, WorkspaceRole newRole, UserId updatedBy) {
        validateManagementPermission(updatedBy);

        if (userId.equals(updatedBy)) {
            throw new IllegalArgumentException("Cannot change your own role");
        }

        WorkspaceMember member = findMember(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // Prevent demoting the last admin/owner
        if (member.getRole().canManageMembers() && isLastAdminOrOwner(userId)) {
            throw new IllegalStateException("Cannot demote the last admin/owner");
        }

        member.setRole(newRole);
        this.updatedAt = Timestamp.builder().value(LocalDateTime.now()).build();
    }

    // Core permission validation - DRY principle
    private void validateManagementPermission(UserId userId) {
        WorkspaceMember member = findMember(userId)
                .orElseThrow(() -> new SecurityException("User is not a member of this workspace"));

        if (!member.getRole().canManageMembers()) {
            throw new SecurityException("User does not have permission to manage this workspace");
        }
    }

    // Helper methods using role-based checks
    public boolean canUserManageWorkspace(UserId userId) {
        return findMember(userId)
                .map(member -> member.getRole().canManageWorkspace())
                .orElse(false);
    }

    public boolean canUserManageMembers(UserId userId) {
        return findMember(userId)
                .map(member -> member.getRole().canManageMembers())
                .orElse(false);
    }

    public boolean canUserManageDocuments(UserId userId) {
        return findMember(userId)
                .map(member -> member.getRole().canManageDocuments())
                .orElse(false);
    }

    public boolean hasMember(UserId userId) {
        return members.stream()
                .anyMatch(m -> m.getUserId().equals(userId));
    }

    private boolean isLastAdminOrOwner(UserId userId) {
        long adminCount = members.stream()
                .filter(m -> m.getRole().canManageMembers())
                .count();

        return adminCount == 1 && findMember(userId)
                .map(m -> m.getRole().canManageMembers())
                .orElse(false);
    }

    public Optional<WorkspaceMember> findMember(UserId userId) {
        return members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<WorkspaceMember> getMember(UserId userId) {
        return findMember(userId);
    }


    /**
     * Validates if a user can perform administrative actions on a workspace.
     * Complex rule: Must be ADMIN or OWNER, and membership must be active.
     */
    //void validateAdminPermission(Workspace workspace, UserId userId);

    /**
     * Validates if a user can access a workspace.
     * Rule: Must be a member with any active role.
     */
    //void validateMemberAccess(Workspace workspace, UserId userId);

    /**
     * Validates if a user can modify another member's role.
     * Complex rule: Only admins can change roles, and you can't change your own role.
     */
    //void validateRoleChangePermission(Workspace workspace, UserId actorUserId, UserId targetUserId, WorkspaceRole newRole);

    /**
     * Validates if a user can remove a member.
     * Rule: Admins can remove members, but can't remove themselves (last admin check).
     */
    //void validateMemberRemovalPermission(Workspace workspace, UserId actorUserId, UserId targetUserId);

    /**
     * Checks if removing this member would leave workspace without admins.
     */
    //boolean isLastAdmin(Workspace workspace, UserId userId);
}
