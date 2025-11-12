-- V2__create_workspace_members_table.sql
-- Create workspace_members table with foreign key relationship and validation
CREATE TABLE workspace_members (
    membership_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    role VARCHAR(10) NOT NULL,
    joined_at TIMESTAMP NOT NULL,

    -- Foreign key constraint with cascade delete
    CONSTRAINT fk_workspace_members_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(workspace_id)
        ON DELETE CASCADE,

    -- Unique constraint to prevent duplicate memberships
    CONSTRAINT uk_workspace_user
        UNIQUE (workspace_id, user_id),

    -- Check constraint for valid roles
    CONSTRAINT chk_workspace_members_role
        CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER', 'VIEWER'))
);

-- Create indexes for performance optimization
CREATE INDEX idx_workspace_members_user_id ON workspace_members(user_id);
CREATE INDEX idx_workspace_members_workspace_id ON workspace_members(workspace_id);
CREATE INDEX idx_workspace_members_role ON workspace_members(role);
CREATE INDEX idx_workspace_members_joined_at ON workspace_members(joined_at);
CREATE INDEX idx_workspace_members_workspace_role ON workspace_members(workspace_id, role);
CREATE INDEX idx_workspace_members_user_workspace ON workspace_members(user_id, workspace_id);

-- Add table and column comments
COMMENT ON TABLE workspace_members IS 'Stores workspace membership information and user roles';
COMMENT ON COLUMN workspace_members.membership_id IS 'Unique identifier for the membership (UUID)';
COMMENT ON COLUMN workspace_members.user_id IS 'User ID who is a member (foreign key to users table)';
COMMENT ON COLUMN workspace_members.workspace_id IS 'Workspace ID (foreign key to workspaces table)';
COMMENT ON COLUMN workspace_members.role IS 'User role in the workspace (OWNER, ADMIN, MEMBER, VIEWER)';
COMMENT ON COLUMN workspace_members.joined_at IS 'Timestamp when user joined the workspace';