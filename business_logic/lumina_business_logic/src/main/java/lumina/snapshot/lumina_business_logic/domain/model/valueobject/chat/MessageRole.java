package lumina.snapshot.lumina_business_logic.domain.model.valueobject.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRole {
    private String role; // USER, ASSISTANT
}
