-- V1__create_workspaces_table.sql
-- Create workspaces table with enhanced validation and indexing
CREATE TABLE workspaces (
    workspace_id UUID PRIMARY KEY,
    workspace_name VARCHAR(16) NOT NULL UNIQUE,
    created_by UUID NOT NULL,
    description VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    -- Add check constraints for validation
    CONSTRAINT chk_workspace_name_length CHECK (LENGTH(workspace_name) >= 8 AND LENGTH(workspace_name) <= 16),
    CONSTRAINT chk_workspace_name_format CHECK (workspace_name ~ '^[A-Za-z0-9]+$'),
    CONSTRAINT chk_description_length CHECK (LENGTH(description) >= 8 AND LENGTH(description) <= 150),
    CONSTRAINT chk_description_format CHECK (description ~ '^[A-Za-z0-9 ]+$')
);

-- Create indexes for common query patterns
CREATE UNIQUE INDEX idx_workspace_name ON workspaces(workspace_name);
CREATE INDEX idx_workspaces_created_by ON workspaces(created_by);
CREATE INDEX idx_workspaces_created_at ON workspaces(created_at);
CREATE INDEX idx_workspaces_updated_at ON workspaces(updated_at);
CREATE INDEX idx_workspace_name_created_by ON workspaces(workspace_name, created_by);

-- Add table and column comments for documentation
COMMENT ON TABLE workspaces IS 'Stores workspace information and metadata';
COMMENT ON COLUMN workspaces.workspace_id IS 'Unique identifier for the workspace (UUID)';
COMMENT ON COLUMN workspaces.workspace_name IS 'Unique workspace name (8-16 alphanumeric characters)';
COMMENT ON COLUMN workspaces.created_by IS 'User ID who created the workspace (foreign key to users table)';
COMMENT ON COLUMN workspaces.description IS 'Workspace description (8-150 alphanumeric characters with spaces)';
COMMENT ON COLUMN workspaces.created_at IS 'Timestamp when workspace was created';
COMMENT ON COLUMN workspaces.updated_at IS 'Timestamp when workspace was last updated';