package lumina.snapshot.lumina_business_logic.domain.model.valueobject.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timestamp {
    private LocalDateTime value;
}
