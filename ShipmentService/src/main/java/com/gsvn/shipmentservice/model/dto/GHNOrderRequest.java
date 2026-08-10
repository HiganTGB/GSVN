package com.gsvn.shipmentservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GHNOrderRequest {

    @JsonIgnore
    private String token;
    @JsonIgnore
    private Integer shopId;

    @JsonProperty("payment_type_id")
    private Integer paymentTypeId;

    private String note;

    @JsonProperty("required_note")
    private String requiredNote;

    @JsonProperty("client_order_code")
    private String clientOrderCode;

    @JsonProperty("to_name")
    private String toName;

    @JsonProperty("to_phone")
    private String toPhone;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("cod_amount")
    private Integer codAmount;

    private String content;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;

    @JsonProperty("insurance_value")
    private Integer insuranceValue;

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    private List<GHNItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GHNItem {
        private String name;
        private String code;
        private Integer quantity;
        private Integer price;
    }
}