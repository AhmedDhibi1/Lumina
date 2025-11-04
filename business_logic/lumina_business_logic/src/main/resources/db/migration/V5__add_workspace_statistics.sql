-- V5__add_workspace_statistics.sql (Optional: Performance monitoring)
-- Create table for workspace statistics
CREATE TABLE workspace_statistics (
    workspace_id UUID PRIMARY KEY REFERENCES workspaces(workspace_id) ON DELETE CASCADE,
    member_count INTEGER NOT NULL DEFAULT 0,
    last_activity_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workspace_stats_last_activity ON workspace_statistics(last_activity_at);
CREATE INDEX idx_workspace_stats_member_count ON workspace_statistics(member_count);

COMMENT ON TABLE workspace_statistics IS 'Cached statistics for workspace metrics';

-- Function to update member count
CREATE OR REPLACE FUNCTION update_workspace_member_count()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE workspace_statistics
        SET member_count = member_count - 1,
            updated_at = CURRENT_TIMESTAMP
        WHERE workspace_id = OLD.workspace_id;
        RETURN OLD;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO workspace_statistics (workspace_id, member_count, updated_at)
        VALUES (NEW.workspace_id, 1, CURRENT_TIMESTAMP)
        ON CONFLICT (workspace_id)
        DO UPDATE SET
            member_count = workspace_statistics.member_count + 1,
            updated_at = CURRENT_TIMESTAMP;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for member count
CREATE TRIGGER trg_update_workspace_member_count
    AFTER INSERT OR DELETE ON workspace_members
    FOR EACH ROW EXECUTE FUNCTION update_workspace_member_count();