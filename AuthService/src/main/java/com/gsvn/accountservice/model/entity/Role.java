package com.gsvn.accountservice.model.entity;
import java.time.OffsetDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="roles")
public class Role {
    @Id
    @Column(name = "role_id")
    Integer roleId;
    @Column(name = "role_name",unique = true)
    String roleName;
    String description;
    @Column(name = "created_at")
    OffsetDateTime createdAt;
    @Column(name = "updated_at")
    OffsetDateTime updatedAt;
}