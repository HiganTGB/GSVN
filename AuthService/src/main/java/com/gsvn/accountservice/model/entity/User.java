package com.gsvn.accountservice.model.entity;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE acc_db.users SET enabled = FALSE, updated_at = NOW() WHERE user_id = ?")
@FilterDef(name = "enabledFilter", parameters = {})
@Filter(name = "enabledFilter", condition = "enabled = TRUE")
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id",columnDefinition = "VARCHAR(36)")
    String userId;
    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255)" , nullable = false)
    String email;
    @Column(name ="phone_number")
    String phone;
    @Column(name = "user_name",nullable = false)
    String userName;
    @Column(name = "password",nullable = false)
    String password;
    @Column(name = "verifier",nullable = false)
    Boolean verifier;
    @Column(name = "is_active",nullable = false)
    Boolean isActive = true;
    @Column(name = "deleted_at",nullable = true)
    OffsetDateTime deletedAt;
    @Column(name = "is_staff",nullable = false)
    Boolean isStaff;
    @Column(name = "created_at")
    OffsetDateTime createdAt;
    @Column(name = "updated_at")
    OffsetDateTime updatedAt;
}