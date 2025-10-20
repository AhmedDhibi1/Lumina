package lumina.snapshot.lumina_business_logic.domain.model.valueobject.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lumina.snapshot.lumina_business_logic.domain.model.valueobject.identity.PermissionId;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Permissions {
    private PermissionId permissionId;
    private String permissionName; // READ, WRITE, DELETE, SHARE, ADMIN
}
