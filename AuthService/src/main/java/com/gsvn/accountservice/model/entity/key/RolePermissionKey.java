package com.gsvn.accountservice.model.entity.key;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionKey implements Serializable {
    private int roleId;
    private int permissionId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionKey that = (RolePermissionKey) o;
        return roleId == that.roleId && permissionId == that.permissionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }
}
