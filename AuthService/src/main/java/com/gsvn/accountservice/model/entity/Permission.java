package com.gsvn.accountservice.model.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
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
@Table(name="permissions")
public class Permission {
    @Id
    @Column(name = "permission_id")
    Integer permissionId;
    @Column(name = "permission_name")
    String permissionName;
    String description;
    OffsetDateTime created_at;
}