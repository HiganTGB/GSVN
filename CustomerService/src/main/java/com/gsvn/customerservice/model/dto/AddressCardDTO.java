package com.gsvn.customerservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressCardDTO {
    private Long addressId;
    private Long customerId;
    private String receiverName;
    private String receiverPhone;
    private String provinceCode;
    private String wardCode;
    private String addressDetail;
    private Boolean isDefault;
}