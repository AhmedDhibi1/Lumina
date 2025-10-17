package lumina.snapshot.lumina_business_logic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.TagId;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.tagging.Color;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.tagging.TagName;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTag {
    private TagId tagId;
    private TagName tagName;
    private Color color;
    private Timestamp createdAt;
}
