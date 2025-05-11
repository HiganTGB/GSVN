package com.tgb.gsvnbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "address",schema = "adress",indexes = {  @Index(name = "idx_user_id", columnList = "user_id")}
)
public class Address extends AbstractMappedEntity{
        @Serial
        private static final long serialVersionUID = 1L;
        @Id
        private int id;
        @Column(name = "user_id")
        private String userId;
        private String receiver;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String phone;
}