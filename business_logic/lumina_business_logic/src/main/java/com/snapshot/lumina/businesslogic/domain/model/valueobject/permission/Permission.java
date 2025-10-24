package com.snapshot.lumina.businesslogic.domain.model.valueobject.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.PermissionId;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Permission {

    private PermissionId permissionId;
    private PermissionName permissionName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission other = (Permission) o;

        return permissionName != null
                && other.permissionName != null
                && permissionName.value().equals(other.permissionName.value());
    }

    @Override
    public int hashCode() {
        return permissionName != null ? permissionName.value().hashCode() : 0;
    }

    public record PermissionName(String value) {
        public static final PermissionName READ = new PermissionName("READ");
        public static final PermissionName WRITE = new PermissionName("WRITE");
        public static final PermissionName DELETE = new PermissionName("DELETE");
        public static final PermissionName SHARE = new PermissionName("SHARE");

        @Override
        public String toString() {
            return value;
        }
    }
}
