package com.gsvn.accountservice.model.entity;

import com.gsvn.accountservice.model.entity.key.UserRoleKey;
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
@IdClass(UserRoleKey.class)
@Table(name = "user_role")
public class UserRole {
    @Id
    @Column(name = "user_id",columnDefinition = "VARCHAR(36)")
    String userId;
    @Id
    @Column(name = "role_id")
    Integer roleId;
    @Column(name = "created_at")
    OffsetDateTime created_at;
}
