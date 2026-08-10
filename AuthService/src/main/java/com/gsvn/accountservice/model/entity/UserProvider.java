package com.gsvn.accountservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_providers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider_name", "provider_user_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_id", length = 36)
    String userId;
    @Column(name = "provider_name", nullable = false, length = 50)
    String providerName; // 'GOOGLE', 'FACEBOOK'
    @Column(name = "provider_user_id", nullable = false)
    String providerUserId;
    @CreationTimestamp
    @Column(name = "created_at")
    OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    OffsetDateTime updatedAt;
}