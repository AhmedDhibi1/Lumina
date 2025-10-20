package lumina.snapshot.lumina_business_logic.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.common.Timestamp;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    private UUID eventId;
    private String eventName;
    private String description;
    private Timestamp occurredAt;
}
