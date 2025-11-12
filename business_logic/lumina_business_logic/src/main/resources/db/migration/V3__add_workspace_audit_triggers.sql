-- V3__add_workspace_audit_triggers.sql (Optional: Add audit trail)
-- Create audit log table for tracking changes
CREATE TABLE workspace_audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    workspace_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE', 'UPDATE', 'DELETE')),
    changed_by UUID,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_values JSONB,
    new_values JSONB
);

CREATE INDEX idx_audit_workspace_id ON workspace_audit_log(workspace_id);
CREATE INDEX idx_audit_changed_at ON workspace_audit_log(changed_at);
CREATE INDEX idx_audit_action ON workspace_audit_log(action);

COMMENT ON TABLE workspace_audit_log IS 'Audit trail for workspace changes';

-- Create trigger function for workspace audit
CREATE OR REPLACE FUNCTION audit_workspace_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO workspace_audit_log(workspace_id, action, old_values)
        VALUES (OLD.workspace_id, 'DELETE', row_to_json(OLD));
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO workspace_audit_log(workspace_id, action, old_values, new_values)
        VALUES (OLD.workspace_id, 'UPDATE', row_to_json(OLD), row_to_json(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO workspace_audit_log(workspace_id, action, new_values)
        VALUES (NEW.workspace_id, 'CREATE', row_to_json(NEW));
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create triggers
CREATE TRIGGER trg_workspace_audit
    AFTER INSERT OR UPDATE OR DELETE ON workspaces
    FOR EACH ROW EXECUTE FUNCTION audit_workspace_changes();

CREATE TRIGGER trg_workspace_member_audit
    AFTER INSERT OR UPDATE OR DELETE ON workspace_members
    FOR EACH ROW EXECUTE FUNCTION audit_workspace_changes();