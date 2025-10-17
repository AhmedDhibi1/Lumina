package lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Permissions {
    private List<String> permissionNames; // READ, WRITE, DELETE, SHARE, ADMIN
}
