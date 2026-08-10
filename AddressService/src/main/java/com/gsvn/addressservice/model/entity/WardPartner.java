package com.gsvn.addressservice.model.entity;

import com.gsvn.addressservice.model.enums.PartnerName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class WardPartner {
    private String wardCode;
    private String partnerName;
    private String partnerWardCode;
    private PartnerName partnerDistrictCode;
}
