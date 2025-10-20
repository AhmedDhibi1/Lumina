package lumina.snapshot.lumina_business_logic.domain.model.valueobject.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityAnalytics {
    private DocumentId documentId;
    private Integer totalViews;
    private Integer totalDownloads;
    private Integer totalShares;
    private List<UserId> topAccessors;
    private Timestamp lastAccessed;
    private Timestamp createdAt;
}
