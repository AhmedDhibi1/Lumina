package lumina.snapshot.lumina_business_logic.domain.model.valueobject.tagging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {
    private String hexCode;
}
