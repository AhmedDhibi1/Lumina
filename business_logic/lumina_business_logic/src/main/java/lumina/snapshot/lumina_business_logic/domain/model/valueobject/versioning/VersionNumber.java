package lumina.snapshot.lumina_business_logic.domain.model.valueobject.versioning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionNumber {
    private Integer number;
}
