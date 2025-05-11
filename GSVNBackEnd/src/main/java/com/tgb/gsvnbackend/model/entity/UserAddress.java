package com.tgb.gsvnbackend.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document(collection = "userAddresses")
public class UserAddress extends AbstractMappedEntity {
    @Id
    private String id;
    private String userId;
    @Data
    private static class Address {
        private String receiver;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String phone;
        private boolean isDefault;
    }
}