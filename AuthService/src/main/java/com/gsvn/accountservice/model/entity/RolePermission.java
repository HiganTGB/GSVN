package com.gsvn.accountservice.model.entity;

import com.gsvn.accountservice.model.entity.key.RolePermissionKey;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@IdClass(RolePermissionKey.class)
@Table(name = "role_permission")
public class RolePermission {
    @Id
    @Column(name = "role_id")
    Integer roleId;
    @Id
    @Column(name = "permission_id")
    Integer permissionId;
    @Column(name = "created_at")
    OffsetDateTime created_at;
}
