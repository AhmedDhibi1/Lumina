-- V2__create_workspace_members_table.sql
-- Create workspace_members table with foreign key relationship
CREATE TABLE workspace_members (
    membership_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL,

    -- Foreign key constraint
    CONSTRAINT fk_workspace_members_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(workspace_id)
        ON DELETE CASCADE,

    -- Unique constraint to prevent duplicate memberships
    CONSTRAINT uk_workspace_user
        UNIQUE (workspace_id, user_id),

    -- Indexes for performance
    INDEX idx_workspace_members_user_id (user_id),
    INDEX idx_workspace_members_workspace_id (workspace_id),
    INDEX idx_workspace_members_joined_at (joined_at)
);

-- Add comments
COMMENT ON TABLE workspace_members IS 'Stores workspace membership information';
COMMENT ON COLUMN workspace_members.role IS 'User role in the workspace (e.g., OWNER, ADMIN, MEMBER)';
COMMENT ON COLUMN workspace_members.joined_at IS 'Timestamp when user joined the workspace';

-- Add check constraint for valid roles (optional but recommended)
ALTER TABLE workspace_members
ADD CONSTRAINT chk_workspace_members_role
CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER', 'VIEWER'));