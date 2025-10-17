package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.ActivityId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.DocumentId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.UserId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.ActivityDetails;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission.ActivityType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentActivity {
    private ActivityId activityId;
    private DocumentId documentId;
    private UserId userId;
    private ActivityType activityType;
    private ActivityDetails details;
    private Timestamp timestamp;
}
