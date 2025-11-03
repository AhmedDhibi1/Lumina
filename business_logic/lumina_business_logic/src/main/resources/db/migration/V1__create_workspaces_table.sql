-- V1__create_workspaces_table.sql
-- Create workspaces table
CREATE TABLE workspaces (
    workspace_id UUID PRIMARY KEY,
    workspace_name VARCHAR(255) NOT NULL,
    created_by UUID NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    -- Indexes for common queries
    INDEX idx_workspaces_created_by (created_by),
    INDEX idx_workspaces_created_at (created_at)
);

-- Add comment for documentation
COMMENT ON TABLE workspaces IS 'Stores workspace information and metadata';
COMMENT ON COLUMN workspaces.workspace_id IS 'Unique identifier for the workspace';
COMMENT ON COLUMN workspaces.created_by IS 'User ID who created the workspace';
COMMENT ON COLUMN workspaces.description IS 'Optional workspace description (max 500 chars)';