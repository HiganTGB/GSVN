package com.gsvn.customerservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressCard {
    private Long addressId;
    private Long customerId;
    private String receiverName;
    private String receiverPhone;
    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private String fullAddressText;
    private Boolean isDefault;
    private OffsetDateTime createdAt;
}