-- V4__add_workspace_search_indexes.sql (Optional: Full-text search)
-- Add GIN index for full-text search on workspace names and descriptions
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Create trigram indexes for fuzzy search
CREATE INDEX idx_workspace_name_trgm ON workspaces USING GIN (workspace_name gin_trgm_ops);
CREATE INDEX idx_workspace_description_trgm ON workspaces USING GIN (description gin_trgm_ops);

-- Add full-text search column (optional)
ALTER TABLE workspaces ADD COLUMN search_vector tsvector;

-- Create function to update search vector
CREATE OR REPLACE FUNCTION workspace_search_vector_update()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('english', COALESCE(NEW.workspace_name, '')), 'A') ||
        setweight(to_tsvector('english', COALESCE(NEW.description, '')), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update search vector
CREATE TRIGGER trg_workspace_search_vector
    BEFORE INSERT OR UPDATE ON workspaces
    FOR EACH ROW EXECUTE FUNCTION workspace_search_vector_update();

-- Create GIN index for full-text search
CREATE INDEX idx_workspace_search_vector ON workspaces USING GIN (search_vector);

-- Update existing rows
UPDATE workspaces SET updated_at = updated_at;

COMMENT ON COLUMN workspaces.search_vector IS 'Full-text search vector for workspace name and description';