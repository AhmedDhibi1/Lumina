package lumina.snapshot.lumina_business_logic.domain.service;

import lumina.snapshot.lumina_business_logic.domain.model.entity.DocumentActivity;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.WorkspaceId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata.ActivityAnalytics;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.ActivityDetails;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.ActivityType;

import java.util.List;

public interface DocumentActivityService {

    // Log document activity
    DocumentActivity logActivity(DocumentId documentId, UserId userId, ActivityType activityType, ActivityDetails details);

    // Get activity history for document
    List<DocumentActivity> getDocumentActivityHistory(DocumentId documentId);

    // Get user activity across all documents
    List<DocumentActivity> getUserActivityHistory(UserId userId);

    // Get recent activities in workspace
    List<DocumentActivity> getWorkspaceRecentActivity(WorkspaceId workspaceId, Integer limit);

    // Track document view
    void trackView(DocumentId documentId, UserId userId);

    // Track document download
    void trackDownload(DocumentId documentId, UserId userId);

    // Track document share
    void trackShare(DocumentId documentId, UserId sharedBy, UserId sharedWith);

    // Generate activity analytics
    ActivityAnalytics generateAnalytics(DocumentId documentId);
}
