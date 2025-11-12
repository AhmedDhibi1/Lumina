package com.snapshot.lumina.businesslogic.domain.model.valueobject.permission;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.PermissionId;

@Builder
@Value
public class Permission {

    PermissionId permissionId;
    PermissionName permissionName;

    public Permission(PermissionId permissionId, PermissionName permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }

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
